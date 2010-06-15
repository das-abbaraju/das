package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport implements Preparable {
	private ContractorRegistrationRequest newContractor = new ContractorRegistrationRequest();
	protected ContractorRegistrationRequestDAO crrDAO;
	protected OperatorAccountDAO operatorAccountDAO;
	protected UserDAO userDAO;
	protected CountryDAO countryDAO;
	protected StateDAO stateDAO;
	protected ContractorAccountDAO contractorAccountDAO;
	protected EmailAttachmentDAO emailAttachmentDAO;
	protected EmailTemplateDAO emailTemplateDAO;
	protected AccountDAO accountDAO;
	protected FacilitiesDAO facilitiesDAO;

	protected int requestID;
	protected Country country;
	protected State state;
	protected int requestedOperator;
	protected int requestedUser;
	protected String requestedOther = null;
	protected int conID;
	protected int opID;
	protected ContractorAccount conAccount = null;
	protected String[] filenames = null;
	protected String emailSubject;
	protected String emailBody;
	protected List<ContractorAccount> potentialMatches;
	protected String conName;
	protected boolean redirect = true;
	protected boolean watched = true;

	private String[] names = new String[] { "ContractorName",
			"ContractorPhone", "ContractorEmail", "RequestedByOperator",
			"RequestedByUser", "ContractorContactName", "ContractorTaxID",
			"ContractorAddress", "ContractorCity", "ContractorState",
			"ContractorZip", "ContractorCountry", "Deadline",
			"RegistrationLink", "PICSSignature" };

	private String[] velocityCodes = new String[] { "${newContractor.name}",
			"${newContractor.phone}", "${newContractor.email}",
			"${newContractor.requestedBy.name}", "${requestedBy}",
			"${newContractor.contact}", "${newContractor.taxID}",
			"${newContractor.address}", "${newContractor.city}",
			"${newContractor.state.english}", "${newContractor.zip}",
			"${newContractor.country.english}", "${newContractor.deadline}",
			"${requestLink}", "<PICSSignature>" };

	public RequestNewContractor(ContractorRegistrationRequestDAO crrDAO,
			OperatorAccountDAO operatorAccountDAO, UserDAO userDAO, CountryDAO countryDAO, StateDAO stateDAO,
			ContractorAccountDAO contractorAccountDAO, EmailAttachmentDAO emailAttachmentDAO,
			EmailTemplateDAO emailTemplateDAO, AccountDAO accountDAO, FacilitiesDAO facilitiesDAO) {
		this.crrDAO = crrDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.userDAO = userDAO;
		this.countryDAO = countryDAO;
		this.stateDAO = stateDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.emailAttachmentDAO = emailAttachmentDAO;
		this.emailTemplateDAO = emailTemplateDAO;
		this.accountDAO = accountDAO;
		this.facilitiesDAO = facilitiesDAO;
	}

	public void prepare() throws Exception {
		getPermissions();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 2);
		newContractor.setDeadline(cal.getTime());

		requestID = getParameter("requestID");
		if (requestID > 0) {
			newContractor = crrDAO.find(requestID);
			redirect = false;
		} else {
			newContractor.setCountry(new Country(permissions.getCountry()));
			if (permissions.isOperatorCorporate()) {
				newContractor.setRequestedBy(new OperatorAccount());
				newContractor.getRequestedBy().setId(permissions.getAccountId());
				newContractor.setRequestedByUser(new User(permissions.getUserId()));
			}
		}

		String[] countryIsos = (String[]) ActionContext.getContext().getParameters().get("country.isoCode");
		if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
			country = countryDAO.find(countryIsos[0]);

		String[] stateIsos = (String[]) ActionContext.getContext().getParameters().get("state.isoCode");
		if (stateIsos != null && stateIsos.length > 0 && !Strings.isEmpty(stateIsos[0]))
			state = stateDAO.find(stateIsos[0]);

		if (newContractor.getContractor() != null) {
			conAccount = contractorAccountDAO.find(newContractor.getContractor().getId());
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		tryPermissions(OpPerms.RequestNewContractor, OpType.Edit);

		if (button != null) {
			if (button.equals("Save")) {
				if (Strings.isEmpty(newContractor.getName()))
					addActionError("Please fill the contractor Name");
				if (Strings.isEmpty(newContractor.getContact()))
					addActionError("Please fill the Contact Name");
				if (requestedOperator == 0)
					addActionError("Please select the Requested By Account");
				if (requestedUser == 0 && Strings.isEmpty(newContractor.getRequestedByUserOther()))
					addActionError("Please select the Requested User for the Account");
				if (country == null)
					addActionError("Please select a Country");
				else if (country.getIsoCode().equals("US") || country.getIsoCode().equals("CA")) {
					if (state == null || Strings.isEmpty(state.getIsoCode()))
						addActionError("Please select a State");
				}
				// One of phone OR email is required.
				if (Strings.isEmpty(newContractor.getPhone()) && Strings.isEmpty(newContractor.getEmail()))
					addActionError("Contact information is required. Please enter in a phone number and/or email address.");
				if (!Strings.isEmpty(newContractor.getEmail()) && !Strings.isValidEmail(newContractor.getEmail()))
					addActionError("Please fill in a Valid Email Address");
				// There are errors, just exit out
				if (getActionErrors().size() > 0)
					return SUCCESS;

				if (newContractor.getDeadline() == null)
					newContractor.setDeadline(DateBean.addMonths(new Date(), 2));
				if (country != null && !country.equals(newContractor.getCountry()))
					newContractor.setCountry(country);
				if (state != null && !state.equals(newContractor.getState()))
					newContractor.setState(state);
				if (requestedOperator > 0 
						&& (newContractor.getRequestedBy() == null
								|| requestedOperator != newContractor.getRequestedBy().getId())) {
					newContractor.setRequestedBy(operatorAccountDAO.find(requestedOperator));
				}
				if (requestedUser > 0
						&& (newContractor.getRequestedByUser() == null
								|| requestedUser != newContractor.getRequestedByUser().getId())) {
					newContractor.setRequestedByUser(userDAO.find(requestedUser));
					newContractor.setRequestedByUserOther(null);
				} else if (requestedUser == 0) {
					if (!Strings.isEmpty(requestedOther)) {
						newContractor.setRequestedByUser(null);
						newContractor.setRequestedByUserOther(requestedOther);
					}
				}
				if (!Strings.isEmpty(conName) && 
						(newContractor.getContractor() == null || conName != newContractor.getContractor().getName())) {
					ContractorAccount con = contractorAccountDAO.findConID(conName);
					newContractor.setContractor(con);
					
					if (watched && newContractor.getRequestedByUser() != null) {
						ContractorWatch watch = new ContractorWatch();
						watch.setAuditColumns(permissions);
						watch.setContractor(con);
						watch.setUser(newContractor.getRequestedByUser());
						crrDAO.save(watch);
					}
				} else if (Strings.isEmpty(conName)) {
					newContractor.setContractor(null);
				}
				
				potentialMatches = runGapAnalysis();
				if (potentialMatches.size() > 0)
					newContractor.setMatchCount(potentialMatches.size());
			}
			
			if (button.equals("MatchingList")) {
				if (requestID > 0) {
					newContractor = crrDAO.find(requestID);
					potentialMatches = runGapAnalysis();
					
					if (potentialMatches.size() != newContractor.getMatchCount()) {
						newContractor.setMatchCount(potentialMatches.size());
						crrDAO.save(newContractor);
						crrDAO.clear();
					}
					
					return "matches";
				} else {
					addActionError("Requested contractor not found.");
					return BLANK;
				}
			}
			
			if (button.equals("Close Request")) {
				newContractor.setOpen(false);
				redirect = true;
			}

			if (button.equals("Send Email") || button.equals("Contacted By Phone")) {
				String contacted = maskDateFormat(new Date()) + " - " + permissions.getName() + " - ";
				
				if (button.equals("Send Email")) {
					// Point to the contractor registration page with some information pre-filled
					String requestLink = "http://www.picsorganizer.com/ContractorRegistration.action?button=" +
							"request&requestID=" + newContractor.getId();
					
					String picsSignature = "PICS\nP.O. Box 51387\nIrvine CA 92619-1387\nTel: (949)387-1940\n"
						+ "Fax: (949)269-9153\nhttp://www.picsauditing.com\nemail: info@picsauditing.com "
						+ "(Please add this email address to your address book to prevent it from being labeled "
						+ "as spam)";
					
					String[] fields = new String[] { newContractor.getName(), newContractor.getPhone(), 
							newContractor.getEmail(), newContractor.getRequestedBy().getName(), 
							newContractor.getRequestedByUser() != null ? 
								newContractor.getRequestedByUser().getName() : 
									newContractor.getRequestedByUserOther(), newContractor.getContact(),
							newContractor.getTaxID(), newContractor.getAddress(), newContractor.getCity(),
							newContractor.getState().getEnglish(), newContractor.getZip(),
							newContractor.getCountry().getEnglish(), maskDateFormat(newContractor.getDeadline()),
							requestLink, picsSignature };
					
					if (Strings.isEmpty(emailBody) || Strings.isEmpty(emailSubject)) {
						// Operator Request for Registration
						EmailTemplate emailTemplate = emailTemplateDAO.find(83);
						
						if (Strings.isEmpty(emailBody))
							emailBody = emailTemplate.getBody();
						if (Strings.isEmpty(emailSubject))
							emailSubject = emailTemplate.getSubject();
					}
					
					for (int i = 0; i < names.length; i++) {
						emailBody = emailBody.replace("<" + names[i] + ">", fields[i] != null ? fields[i] : "");
						emailSubject = emailSubject.replace("<" + names[i] + ">", 
								fields[i] != null ? fields[i] : "");
					}

					EmailQueue emailQueue = new EmailQueue();
					emailQueue.setPriority(80);
					emailQueue.setFromAddress(getAssignedCSR().getEmail());
					emailQueue.setToAddresses(newContractor.getEmail());
					emailQueue.setBody(emailBody);
					emailQueue.setSubject(emailSubject);
					EmailSender.send(emailQueue);

					// Need to do an update to where these files are stored.
					if (filenames != null) {
						for (String filename : filenames) {
							try {
								EmailAttachment attachment = new EmailAttachment();
								File file = new File(getFtpDir() + "/forms/" + filename);

								byte[] bytes = new byte[(int) file.length()];
								FileInputStream fis = new FileInputStream(file);
								fis.read(bytes);

								attachment.setFileName(getFtpDir() + "/forms/" + filename);
								attachment.setContent(bytes);
								attachment.setFileSize((int) file.length());
								attachment.setEmailQueue(emailQueue);
								emailAttachmentDAO.save(attachment);
							} catch (Exception e) {
								System.out.println("Unable to open file: /forms/" + filename);
							}
						}
					}
					
					contacted += "Contacted by email.";
				} else {
					contacted += "Contacted by phone.";
				}
				
				newContractor.setNotes(contacted + "\n\n" + newContractor.getNotes());
				newContractor.setContactCount(newContractor.getContactCount() + 1);
				newContractor.setLastContactedBy(new User(permissions.getUserId()));
				newContractor.setLastContactDate(new Date());
			}
			
			newContractor.setAuditColumns(permissions);
			crrDAO.save(newContractor);
			if(redirect)
				return "backToReport";
			else return SUCCESS;
		}
		return SUCCESS;
	}

	public ContractorRegistrationRequest getNewContractor() {
		return newContractor;
	}

	public void setNewContractor(ContractorRegistrationRequest newContractor) {
		this.newContractor = newContractor;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public List<OperatorAccount> getOperatorsList() {
		if (permissions == null)
			return null;
		return operatorAccountDAO.findWhere(false, "", permissions);
	}

	public List<User> getUsersList(int accountID) {
		return userDAO.findByAccountID(accountID, "Yes", "No");
	}
	
	public User getPrimaryContact(int opID){
		return operatorAccountDAO.find(opID).getPrimaryContact();
	}

	public List<Country> getCountryList() {
		return countryDAO.findAll();
	}

	public List<State> getStateList() {
		return stateDAO.findAll();
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}
	
	public int getMoo(int x){
		return x + 1;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getRequestedOperator() {
		return requestedOperator;
	}

	public void setRequestedOperator(int requestedOperator) {
		this.requestedOperator = requestedOperator;
	}

	public int getRequestedUser() {
		return requestedUser;
	}

	public void setRequestedUser(int requestedUser) {
		this.requestedUser = requestedUser;
	}

	public String getRequestedOther() {
		return requestedOther;
	}

	public void setRequestedOther(String requestedOther) {
		this.requestedOther = requestedOther;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public String getConName() {
		return conName;
	}
	
	public void setConName(String conName) {
		this.conName = conName;
	}
	
	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public ContractorAccount getConAccount() {
		return conAccount;
	}

	public String[] getFilenames() {
		return filenames;
	}

	public void setFilenames(String[] filenames) {
		this.filenames = filenames;
	}
	
	public boolean isWatched() {
		return watched;
	}
	
	public void setWatched(boolean watched) {
		this.watched = watched;
	}

	public String getEmailSubject() {
		if (emailSubject == null) {
			EmailTemplate template = emailTemplateDAO.find(83);
			if (template.getSubject() != null)
				emailSubject = template.getSubject();
			else
				emailSubject = "";
		}

		for (int i = 0; i < velocityCodes.length; i++) {
			emailSubject = emailSubject.replace(velocityCodes[i], "<" + names[i] + ">");
		}

		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		if (emailBody == null) {
			EmailTemplate template = emailTemplateDAO.find(83);
			emailBody = template.getBody();
		}

		for (int i = 0; i < velocityCodes.length; i++) {
			emailBody = emailBody.replace(velocityCodes[i], "<" + names[i] + ">");
		}

		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	
	public List<ContractorAccount> getPotentialMatches() {
		return potentialMatches;
	}

	public User getAssignedCSR() {
		if (newContractor.getId() > 0) {
			if (newContractor.getCountry().getCsr() != null)
				return newContractor.getCountry().getCsr();
			else
				return newContractor.getState().getCsr();
		}
		return null;
	}

	public boolean isFormsViewable() {
		return permissions.hasPermission(OpPerms.FormsAndDocs);
	}

	public List<OperatorForm> getForms() {
		List<OperatorForm> forms = null;
		Set<OperatorForm> allForms = new HashSet<OperatorForm>();
		OperatorAccount operator = newContractor.getRequestedBy();
		
		List<Facility> facilities = facilitiesDAO.findSiblings(operator.getId());
		
		for (Facility facility : facilities) {
			allForms.addAll(facility.getOperator().getOperatorForms());
		}
		
		allForms.addAll(operator.getOperatorForms());
		forms = new ArrayList<OperatorForm>(allForms);
		
		Collections.sort(forms, new ByFacilityName());
		return forms;
	}

	public String[] getTokens() {
		return names;
	}
	
	public List<ContractorAccount> runGapAnalysis() {
		List<String> whereClauses = new ArrayList<String>();
		
		if (!Strings.isEmpty(newContractor.getName()))
			whereClauses.add("a.name LIKE '%" + Utilities.escapeQuotes(newContractor.getName()) 
					+ "%' OR a.nameIndex LIKE '%" + Strings.indexName(newContractor.getName()) 
					+ "%' OR a.dbaName LIKE '%" + Utilities.escapeQuotes(newContractor.getName()) + "%'");
		if (!Strings.isEmpty(newContractor.getTaxID()))
			whereClauses.add("a.taxId LIKE '%" + Utilities.escapeQuotes(newContractor.getTaxID()) + "%'");
		if (!Strings.isEmpty(newContractor.getAddress()))
			whereClauses.add("a.address LIKE '%" + Utilities.escapeQuotes(newContractor.getAddress()) + "%'");
		if (!Strings.isEmpty(newContractor.getPhone()))
			whereClauses.add("a.phone LIKE '%" + Utilities.escapeQuotes(newContractor.getPhone()) + "%'");
		if (!Strings.isEmpty(newContractor.getEmail()))
			whereClauses.add("a.id IN (SELECT u.account.id from User u WHERE u.email LIKE '%" + 
					Utilities.escapeQuotes(newContractor.getEmail()) + "%')");
		if (!Strings.isEmpty(newContractor.getContact())) {
			whereClauses.add("a.id IN (SELECT u.account.id from User u WHERE u.name LIKE '%" + 
					Utilities.escapeQuotes(newContractor.getContact()) + "%')");
		}
		
		if (whereClauses.size() > 0) {
			String where = Strings.implode(whereClauses, ") OR (");
			where = "(" + where + ")";
			return contractorAccountDAO.findWhere(where);
		}
		
		return null;
	}
	
	public boolean worksForOperator(int conID) {
		if (permissions.isOperatorCorporate()) {
			OperatorAccount operator = operatorAccountDAO.find(permissions.getAccountId());
			List<ContractorOperator> cos = operator.getContractorOperators();
			
			for (ContractorOperator co : cos) {
				if (co.getContractorAccount().getId() == conID)
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isSearchForNew() {
		return permissions.hasPermission(OpPerms.SearchContractors);
	}
	
	private class ByFacilityName implements Comparator<OperatorForm> {
		public int compare(OperatorForm o1, OperatorForm o2) {
			if (o1.getAccount().getName().compareTo(o2.getAccount().getName()) == 0)
				return (o1.getFormName().compareTo(o2.getFormName()));

			return o1.getAccount().getName().compareTo(o2.getAccount().getName());
		}
	}
}
