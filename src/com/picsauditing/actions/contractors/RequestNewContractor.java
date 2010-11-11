package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
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
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.SpringUtils;
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
	protected AccountDAO accountDAO;

	protected boolean redirect = false;
	protected int conID;
	protected int opID;
	protected int requestID;
	protected int requestedOperator;
	protected int requestedUser = 0;
	protected String emailSubject;
	protected String emailBody;
	protected String addToNotes;
	protected String[] filenames = null;
	protected Country country;
	protected State state;
	protected EmailTemplate template;
	protected List<ContractorAccount> potentialMatches;
	
	protected String term;
	protected String type;
	
	protected List<String> unusedTerms;
	protected List<String> usedTerms;
	
	protected boolean continueCheck = true;
	
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
	
	private String[] noteReason = new String[] { "The Contractor doesn't want to register",
			"The contractor wants to register but keeps delaying", "The company is no longer in business",
			"We were unable to locate this company" };
	
	private String picsSignature = "PICS\nP.O. Box 51387\nIrvine CA 92619-1387\nTel: (949)387-1940\n"
		+ "Fax: (949)269-9153\nhttp://www.picsauditing.com\nemail: info@picsauditing.com "
		+ "(Please add this email address to your address book to prevent it from being labeled " 
		+ "as spam)";
	
	public RequestNewContractor(ContractorRegistrationRequestDAO crrDAO, OperatorAccountDAO operatorAccountDAO,
			UserDAO userDAO, CountryDAO countryDAO, StateDAO stateDAO, ContractorAccountDAO contractorAccountDAO,
			AccountDAO accountDAO) {
		this.crrDAO = crrDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.userDAO = userDAO;
		this.countryDAO = countryDAO;
		this.stateDAO = stateDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.accountDAO = accountDAO;
	}

	public void prepare() throws Exception {
		getPermissions();
		
		newContractor.setDeadline(DateBean.addMonths(new Date(), 2));

		requestID = getParameter("requestID");
		if (requestID > 0)
			newContractor = crrDAO.find(requestID);
		else {
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
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		EmailTemplateDAO templateDAO = (EmailTemplateDAO) SpringUtils.getBean("EmailTemplateDAO");
		template = templateDAO.find(83);

		if (button != null) {
			if("ajaxcheck".equals(button)){
				SearchEngine searchEngine = new SearchEngine(permissions);
				List<BasicDynaBean> matches = newGap(searchEngine, term, type);
				if(matches!=null && !matches.isEmpty()) // results
					continueCheck = false;
				else return null;
				Database db = new Database();
				JSONArray result = new JSONArray();
				List<Integer> ids = new ArrayList<Integer>();
				StringBuilder query = new StringBuilder();
				result.add(continueCheck);
				if("C".equalsIgnoreCase(type))
					query.append("SELECT a.id, a.name FROM accounts a WHERE a.id IN ("); 
				else if("U".equalsIgnoreCase(type))
					query.append("SELECT a.id, a.name FROM accounts a JOIN users u ON a.id = u.accountID WHERE a.id IN(");
				for(BasicDynaBean bdb : matches){
					int id = Integer.parseInt(bdb.get("foreignKey").toString());
					ids.add(id);
				}		
				JSONArray jObj = new JSONArray();
				for(final String str : unusedTerms){
					jObj.add(new JSONObject(){
						{
							put("unused", str);
						}
					});
				}
				result.add(jObj);
				jObj = new JSONArray();
				for(final String str : usedTerms){
					jObj.add(new JSONObject(){
						{
							put("used", str);
						}
					});
				}
				result.add(jObj);
				query.append(Strings.implode(ids, ",")).append(')');
				List<BasicDynaBean> cons = db.select(query.toString(), false);
				final Hashtable<Integer, Integer> ht = searchEngine.getConIds(permissions);			
				for(BasicDynaBean bdb : cons){
					final String name = bdb.get("name").toString();
					final String id = bdb.get("id").toString();
					result.add(new JSONObject(){
						{
							put("name", name);
							put("id", id);
							if(ht.containsKey(id))
								put("add",false);
							else
								put("add",true);
						}
					});
				}
				json.put("result", result);
				return JSON;
			}
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
					if (!Strings.isEmpty(newContractor.getRequestedByUserOther()))
						newContractor.setRequestedByUser(null);
				}
				if (conID > 0 && (newContractor.getContractor() == null || 
						conID != newContractor.getContractor().getId())) {
					ContractorAccount con = contractorAccountDAO.find(conID);
					
					if (con != null) {
						newContractor.setContractor(con);
						newContractor.setHandledBy(WaitingOn.Operator);
						
						if (newContractor.isWatch() && newContractor.getRequestedByUser() != null) {
							// Need to check if the watch exists all ready?
							List<ContractorWatch> existing = 
								userDAO.findContractorWatch(newContractor.getRequestedByUser().getId());
							boolean exists = false;
							
							for (ContractorWatch cw : existing) {
								if (cw.getContractor().equals(con))
									exists = true;
							}
							
							if (!exists) {
								ContractorWatch watch = new ContractorWatch();
								watch.setAuditColumns(permissions);
								watch.setContractor(con);
								watch.setUser(newContractor.getRequestedByUser());
								crrDAO.save(watch);
							}
						}
					} else
						addActionError("PICS Contractor not found");
				} else if (conID == 0)
					newContractor.setContractor(null);
				
				// Add notes, if it's been filled out
				if (!Strings.isEmpty(addToNotes)) {
					newContractor.setNotes(prepend(addToNotes, newContractor.getNotes()));
					addToNotes = null;
				}
				
				potentialMatches = runGapAnalysis(newContractor);
				if (potentialMatches.size() > 0)
					newContractor.setMatchCount(potentialMatches.size());
				
				if (newContractor.getId() == 0) {
					if (!Strings.isEmpty(newContractor.getEmail())) {
						newContractor.setAuditColumns(permissions);
						newContractor = crrDAO.save(newContractor);
						
						EmailQueue emailQueue = createEmail();
						List<OperatorForm> forms = getForms();
						
						if (forms.size() == 1 && emailQueue != null) {
							String filename = forms.get(0).getFile();
							addAttachments(emailQueue, filename);
						}
					}
					
					redirect = true;
				}
			}
			
			if (button.equals("MatchingList")) {
				if (requestID > 0) {
					newContractor = crrDAO.find(requestID);
					potentialMatches = runGapAnalysis(newContractor);
					
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
			
			if (button.equals("Return To Operator")) {
				newContractor.setHandledBy(WaitingOn.Operator);
				redirect = true;
			}
			
			if (button.equals("Close Request")) {
				// Last minute notes?
				if (!Strings.isEmpty(addToNotes)) {
					newContractor.setNotes(prepend(addToNotes, newContractor.getNotes()));
					addToNotes = null;
				}
				
				newContractor.setNotes(prepend("Closed the request.", newContractor.getNotes()));
				newContractor.setOpen(false);
				redirect = true;
			}

			if (button.equals("Send Email") || button.equals("Contacted By Phone")) {
				if (button.equals("Send Email")) {
					EmailQueue emailQueue = createEmail();
					
					if (filenames != null && emailQueue != null) {
						for (String filename : filenames) {
							addAttachments(emailQueue, filename);
						}
					}
					
					newContractor.setNotes(prepend("Contacted by email.", newContractor.getNotes()));
				} else
					newContractor.setNotes(prepend("Contacted by phone.", newContractor.getNotes()));
				
				newContractor.setContactCount(newContractor.getContactCount() + 1);
				newContractor.setLastContactedBy(new User(permissions.getUserId()));
				newContractor.setLastContactDate(new Date());
			}
			
			newContractor.setAuditColumns(permissions);
			crrDAO.save(newContractor);

			if(redirect)
				return "backToReport";
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
	
	public int getPrimaryContact(int opID){
		return operatorAccountDAO.find(opID).getPrimaryContact().getId();
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
		if (requestedUser == 0 && Strings.isEmpty(newContractor.getRequestedByUserOther()))
			return getPrimaryContact(opID);
		else return requestedUser;
	}

	public void setRequestedUser(int requestedUser) {
		this.requestedUser = requestedUser;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public String[] getFilenames() {
		return filenames;
	}

	public void setFilenames(String[] filenames) {
		this.filenames = filenames;
	}
	
	public String getAddToNotes() {
		return addToNotes;
	}
	
	public void setAddToNotes(String addToNotes) {
		this.addToNotes = addToNotes;
	}

	public String getEmailSubject() {
		if (emailSubject == null) {
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
		
		FacilitiesDAO facilitiesDAO = (FacilitiesDAO) SpringUtils.getBean("FacilitiesDAO");
		List<Facility> facilities = facilitiesDAO.findSiblings(operator.getId());
		
		for (Facility facility : facilities) {
			allForms.addAll(facility.getOperator().getOperatorForms());
		}
		
		allForms.addAll(operator.getOperatorForms());
		forms = new ArrayList<OperatorForm>(allForms);
		
		Iterator<OperatorForm> iterator = forms.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().getFormName().toLowerCase().contains("*"))
				iterator.remove();
		}
		
		Collections.sort(forms, new ByFacilityName());
		return forms;
	}

	public String[] getTokens() {
		return names;
	}
	
	public static List<ContractorAccount> runGapAnalysis(ContractorRegistrationRequest newContractor) {
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
			
			ContractorAccountDAO conDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
			return conDAO.findWhere(where);
		}
		
		return null;
	}
	
	public List<BasicDynaBean> newGap(SearchEngine searchEngine, String term, String type){
		unusedTerms = new ArrayList<String>();
		usedTerms = new ArrayList<String>();
		type = "'"+Utilities.escapeQuotes(type)+"'";
		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		Database db = new Database();
		List<String> termsArray = searchEngine.sortSearchTerms(searchEngine.buildTerm(term, false, false), true);
		while(results.isEmpty() && termsArray.size()>0){
			String query = searchEngine.buildQuery(null, termsArray, "i1.indexType = "+type, null, 20, false, true);
			try {
				results = db.select(query, false);
			} catch (SQLException e) {
				System.out.println("Error running query in RequestNewCon");
				e.printStackTrace();
				return null;
			}
			if(!searchEngine.getNullTerms().isEmpty() && unusedTerms.isEmpty()){
				unusedTerms.addAll(searchEngine.getNullTerms());
				termsArray.removeAll(searchEngine.getNullTerms());
			}
			usedTerms = termsArray;
			termsArray=termsArray.subList(0, termsArray.size()-1);						
			//termsArray.subList(1, termsArray.size());
		}
		return results;
		
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
	
	public boolean isContractorWatch() {
		if (newContractor.getRequestedByUser() != null) {
			try {
				Permissions perm = new Permissions();
				perm.setAccountPerms(newContractor.getRequestedByUser());
				
				return perm.hasPermission(OpPerms.ContractorWatch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	private String prepend(String note, String body) {
		if (note != null)
			return maskDateFormat(new Date()) + " - " + permissions.getName() + " - " + note + "\n\n"
				+ (body != null ? body : "");
		
		return body;
	}
	
	private class ByFacilityName implements Comparator<OperatorForm> {
		public int compare(OperatorForm o1, OperatorForm o2) {
			if (o1.getAccount().getName().compareTo(o2.getAccount().getName()) == 0)
				return (o1.getFormName().compareTo(o2.getFormName()));

			return o1.getAccount().getName().compareTo(o2.getAccount().getName());
		}
	}

	public String[] getNoteReason() {
		return noteReason;
	}
	
	public void setNoteReason(String[] noteReason) {
		this.noteReason = noteReason;
	}
	
	private EmailQueue createEmail() {
		String requestLink = "http://www.picsorganizer.com/ContractorRegistration.action?button="
			+ "request&requestID=" + newContractor.getId();
		// Point to the contractor registration page with some information pre-filled
		String[] fields = new String[] {
				newContractor.getName(), newContractor.getPhone(), newContractor.getEmail(),
				newContractor.getRequestedBy().getName(),
				newContractor.getRequestedByUser() != null ? newContractor.getRequestedByUser().getName()
						: newContractor.getRequestedByUserOther(), newContractor.getContact(),
				newContractor.getTaxID(), newContractor.getAddress(), newContractor.getCity(),
				newContractor.getState().getEnglish(), newContractor.getZip(), newContractor.getCountry().getEnglish(),
				maskDateFormat(newContractor.getDeadline()), requestLink, picsSignature };

		if (Strings.isEmpty(emailBody) || Strings.isEmpty(emailSubject)) {
			// Operator Request for Registration
			if (Strings.isEmpty(emailBody))
				emailBody = getEmailBody();
			if (Strings.isEmpty(emailSubject))
				emailSubject = getEmailSubject();
		}

		for (int i = 0; i < names.length; i++) {
			emailBody = emailBody.replace("<" + names[i] + ">", fields[i] != null ? fields[i] : "");
			emailSubject = emailSubject.replace("<" + names[i] + ">", fields[i] != null ? fields[i] : "");
		}

		EmailQueue emailQueue = new EmailQueue();
		emailQueue.setPriority(80);
		
		emailQueue.setFromAddress(getAssignedCSR() == null ? "info@picsauditing.com" : getAssignedCSR().getEmail());
		emailQueue.setToAddresses(newContractor.getEmail());
		
		if (newContractor.getRequestedByUser() != null && !Strings.isEmpty(newContractor.getRequestedByUser().getEmail()))
			emailQueue.setCcAddresses(newContractor.getRequestedByUser().getEmail());
		
		emailQueue.setBody(emailBody);
		emailQueue.setSubject(emailSubject);
		try {
			EmailSender.send(emailQueue);
			return emailQueue;
		} catch (Exception e1) {
			addActionError("Could not send email with subject '" + emailSubject + "' to " + newContractor.getEmail());
		}
		
		return null;
	}
	
	private void addAttachments(EmailQueue emailQueue, String filename) {
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
			EmailAttachmentDAO attachmentDAO = 
				(EmailAttachmentDAO) SpringUtils.getBean("EmailAttachmentDAO");
			attachmentDAO.save(attachment);
			
			newContractor.setNotes(prepend("Sent email on request creation", 
					newContractor.getNotes()));
		} catch (Exception e) {
			System.out.println("Unable to open file: /forms/" + filename);
		}
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
