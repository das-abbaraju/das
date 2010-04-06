package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport implements Preparable {
	private ContractorRegistrationRequest newContractor = new ContractorRegistrationRequest();
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	protected OperatorAccountDAO operatorAccountDAO;
	protected UserDAO userDAO;
	protected CountryDAO countryDAO;
	protected StateDAO stateDAO;
	protected ContractorAccountDAO contractorAccountDAO;
	protected EmailAttachmentDAO emailAttachmentDAO;
	protected EmailTemplateDAO emailTemplateDAO;

	private int requestID;
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

	private String[] names = new String[] { "ContractorName", "ContractorPhone", "ContractorEmail", "RequestedBy",
			"RequestedByUser", "ContractorContactName", "ContractorTaxID", "ContractorAddress", "ContractorCity",
			"ContractorState", "ContractorZip", "ContractorCountry", "Deadline", "RegistrationLink", "PICSSignature" };

	private String[] velocityCodes = new String[] { "${newContractor.name}", "${newContractor.phone}",
			"${newContractor.email}", "${newContractor.requestedBy.name}", "${requestedBy}",
			"${newContractor.contact}", "${newContractor.taxID}", "${newContractor.address}", "${newContractor.city}",
			"${newContractor.state.english}", "${newContractor.zip}", "${newContractor.country.english}",
			"${newContractor.deadline}", "${requestLink}", "<PICSSignature>" };

	public RequestNewContractor(ContractorRegistrationRequestDAO contractorRegistrationRequestDAO,
			OperatorAccountDAO operatorAccountDAO, UserDAO userDAO, CountryDAO countryDAO, StateDAO stateDAO,
			ContractorAccountDAO contractorAccountDAO, EmailAttachmentDAO emailAttachmentDAO,
			EmailTemplateDAO emailTemplateDAO) {
		this.contractorRegistrationRequestDAO = contractorRegistrationRequestDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.userDAO = userDAO;
		this.countryDAO = countryDAO;
		this.stateDAO = stateDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.emailAttachmentDAO = emailAttachmentDAO;
		this.emailTemplateDAO = emailTemplateDAO;
	}

	public void prepare() throws Exception {
		getPermissions();

		requestID = getParameter("requestID");
		if (requestID > 0) {
			newContractor = contractorRegistrationRequestDAO.find(requestID);
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
					if (state == null || Strings.isEmpty(state.getIsoCode())) {
						addActionError("Please select a State");
					}
				}
				if (Strings.isEmpty(newContractor.getPhone())) {
					addActionError("Please fill a Phone Number");
				}
				if (!Strings.isEmpty(newContractor.getEmail()) && !Strings.isValidEmail(newContractor.getEmail())) {
					addActionError("Please fill in a Valid Email Address");
				}

				if (getActionErrors().size() > 0) {
					return SUCCESS;
				}

				if (newContractor.getDeadline() == null) {
					newContractor.setDeadline(DateBean.addMonths(new Date(), 3));
				}

				if (country != null && !country.equals(newContractor.getCountry())) {
					newContractor.setCountry(country);
				}
				if (state != null && !state.equals(newContractor.getState())) {
					newContractor.setState(state);
				}
				if (requestedOperator > 0
						&& (newContractor.getRequestedBy() == null || requestedOperator != newContractor
								.getRequestedBy().getId())) {
					newContractor.setRequestedBy(operatorAccountDAO.find(requestedOperator));
				}
				if (requestedUser > 0
						&& (newContractor.getRequestedByUser() == null || requestedUser != newContractor
								.getRequestedByUser().getId())) {
					newContractor.setRequestedByUser(userDAO.find(requestedUser));
					newContractor.setRequestedByUserOther(null);
				} else if (requestedOther != null) {
					newContractor.setRequestedByUserOther(requestedOther);
					newContractor.setRequestedByUser(null);
				}
				if (conID > 0
						&& (newContractor.getContractor() == null || conID != newContractor.getContractor().getId())) {
					newContractor.setContractor(new ContractorAccount(conID));
				}
			}
			if (button.equals("Close Request")) {
				newContractor.setOpen(false);
			}

			if (button.equals("Send Email") || button.equals("Contacted By Phone")) {
				if (button.equals("Send Email")) {
					EmailBuilder emailBuilder = new EmailBuilder();
					EmailTemplate emailTemplate = emailTemplateDAO.find(83); // Operator
					// Request
					// for
					// Registration
					// If the operator edited any part of the template, set it
					// here.
					if (!Strings.isEmpty(emailBody))
						emailTemplate.setBody(emailBody);
					if (!Strings.isEmpty(emailSubject))
						emailTemplate.setSubject(emailSubject);

					// Replace custom tokens
					emailBody = emailTemplate.getBody();
					emailSubject = emailTemplate.getSubject();

					for (int i = 0; i < names.length; i++) {
						emailBody = emailBody.replace("<" + names[i] + ">", velocityCodes[i]);
						emailSubject = emailSubject.replace("<" + names[i] + ">", velocityCodes[i]);
					}
					emailTemplate.setBody(emailBody);
					emailTemplate.setSubject(emailSubject);

					// Set the template
					emailBuilder.setTemplate(emailTemplate);
					emailBuilder.addToken("newContractor", newContractor);
					emailBuilder.addToken("csr", getAssignedCSR());

					// Point to the contractor registration page with some
					// information pre-filled
					String requestLink = "http://www.picsauditing.com/app/ContractorRegistration.action?button=request&rID="
							+ newContractor.getId();
					emailBuilder.addToken("requestLink", requestLink);

					// Get who requested this account be created
					String requestedBy;
					if (newContractor.getRequestedByUser() != null)
						requestedBy = newContractor.getRequestedByUser().getName();
					else
						requestedBy = newContractor.getRequestedByUserOther();
					emailBuilder.addToken("requestedBy", requestedBy);

					emailBuilder.setToAddresses(newContractor.getEmail());
					EmailQueue emailQueue = emailBuilder.build();
					emailQueue.setPriority(80);
					emailQueue.setFromAddress(getAssignedCSR().getEmail());
					EmailSender.send(emailQueue);

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
				}
				newContractor.setContactCount(newContractor.getContactCount() + 1);
				newContractor.setLastContactedBy(new User(permissions.getUserId()));
				newContractor.setLastContactDate(new Date());
			}
			newContractor.setAuditColumns(permissions);
			contractorRegistrationRequestDAO.save(newContractor);
			requestID = newContractor.getId();
			addActionMessage("Successfully saved the Contractor");
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

	public List<OperatorAccount> getOperatorsWithCorporate() {
		if (permissions == null)
			return null;
		return operatorAccountDAO.findWhere(true, "", permissions);
	}

	public List<User> getUsersList(int accountID) {
		return userDAO.findByAccountID(accountID, "Yes", "No");
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

		if (permissions.isOperatorCorporate() || permissions.isAdmin()) {
			OperatorAccount operator = newContractor.getRequestedBy();
			Set<OperatorForm> inheritedFrom = new HashSet<OperatorForm>();

			inheritedFrom.addAll(operator.getInheritAuditCategories().getOperatorForms());
			inheritedFrom.addAll(operator.getInheritAudits().getOperatorForms());
			inheritedFrom.addAll(operator.getInheritFlagCriteria().getOperatorForms());
			inheritedFrom.addAll(operator.getInheritInsurance().getOperatorForms());
			inheritedFrom.addAll(operator.getInheritInsuranceCriteria().getOperatorForms());

			forms = new ArrayList<OperatorForm>(inheritedFrom);
		}

		Collections.sort(forms, new ByFacilityName());
		return forms;
	}

	public String[] getTokens() {
		return names;
	}

	private class ByFacilityName implements Comparator<OperatorForm> {
		public int compare(OperatorForm o1, OperatorForm o2) {
			if (o1.getAccount().getName().compareTo(o2.getAccount().getName()) == 0)
				return (o1.getFormName().compareTo(o2.getFormName()));

			return o1.getAccount().getName().compareTo(o2.getAccount().getName());
		}
	}
}
