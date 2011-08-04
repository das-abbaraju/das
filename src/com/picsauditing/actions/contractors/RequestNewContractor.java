package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport implements Preparable {
	@Autowired
	protected ContractorRegistrationRequestDAO crrDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected CountryDAO countryDAO;
	@Autowired
	protected StateDAO stateDAO;
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected UserAssignmentDAO csrDAO;
	@Autowired
	protected EmailTemplateDAO templateDAO;
	@Autowired
	protected EmailAttachmentDAO attachmentDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private EmailQueueDAO emailQueueDAO;

	private ContractorRegistrationRequest newContractor = new ContractorRegistrationRequest();
	protected boolean redirect = false;
	protected boolean increaseContactCount = true;
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
	protected String status;

	protected String term;
	protected String type;

	protected List<String> unusedTerms;
	protected List<String> usedTerms;
	protected List<OperatorForm> forms;
	protected List<OperatorTag> requestedTags = new ArrayList<OperatorTag>(); // selected tags of requestor
	protected List<OperatorTag> operatorTags = new ArrayList<OperatorTag>(); // available tags of the operator

	protected boolean continueCheck = true;

	private String[] names = new String[] { "ContractorName", "ContractorPhone", "ContractorEmail",
			"RequestedByOperator", "RequestedByUser", "ContractorContactName", "ContractorTaxID", "ContractorAddress",
			"ContractorCity", "ContractorState", "ContractorZip", "ContractorCountry", "CSRName", "CSREmail",
			"CSRPhone", "Deadline", "RegistrationLink", "PICSSignature", "RegistrationReason" };

	private String[] noteReason = new String[] { "The Contractor doesn't want to register",
			"The contractor wants to register but keeps delaying", "The company is no longer in business",
			"We were unable to locate this company" };

	private String picsSignature;

	public void prepare() throws Exception {
		getPermissions();

		requestID = getParameter("requestID");

		if (requestID > 0)
			newContractor = crrDAO.find(requestID);
		else {
			newContractor.setCountry(new Country(permissions.getCountry()));
			if (permissions.isOperatorCorporate()) {
				newContractor.setRequestedBy(operatorAccountDAO.find(permissions.getAccountId()));
				newContractor.getRequestedBy().setId(permissions.getAccountId());
				newContractor.setRequestedByUser(userDAO.find(permissions.getUserId()));
			}
		}

		// initialize tags
		if (!Strings.isEmpty(newContractor.getOperatorTags())) {
			StringTokenizer st = new StringTokenizer(newContractor.getOperatorTags(), ", ");
			while (st.hasMoreTokens()) {
				OperatorTag tag = operatorTagDAO.find(Integer.parseInt(st.nextToken()));
				if (tag != null) {
					requestedTags.add(tag);
				}
			}
		}
		loadOperatorTags();

		String[] countryIsos = (String[]) ActionContext.getContext().getParameters().get("country.isoCode");
		if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
			country = countryDAO.find(countryIsos[0]);

		String[] stateIsos = (String[]) ActionContext.getContext().getParameters().get("state.isoCode");
		if (stateIsos != null && stateIsos.length > 0 && !Strings.isEmpty(stateIsos[0]))
			state = stateDAO.find(stateIsos[0]);

		picsSignature = "PICS\nP.O. Box 51387\nIrvine CA 92619-1387\nTel: " + permissions.getPicsPhone() + "\n"
				+ "Fax: " + permissions.getPicsCustomerServiceFax()
				+ "\nhttp://www.picsauditing.com\nemail: marketing@picsauditing.com "
				+ "(Please add this email address to your address book to prevent it from being labeled as spam)";
		status = newContractor.getStatus();
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		template = templateDAO.find(83);

		if (button != null) {
			if ("ajaxcheck".equals(button)) {
				SearchEngine searchEngine = new SearchEngine(permissions);
				List<BasicDynaBean> matches = newGap(searchEngine, term, type);
				if (matches != null && !matches.isEmpty()) // results
					continueCheck = false;
				else
					return null;
				Database db = new Database();
				JSONArray result = new JSONArray();
				List<Integer> ids = new ArrayList<Integer>();
				StringBuilder query = new StringBuilder();
				result.add(continueCheck);
				if ("C".equalsIgnoreCase(type))
					query.append("SELECT a.id, a.name FROM accounts a WHERE a.id IN (");
				else if ("U".equalsIgnoreCase(type))
					query.append("SELECT a.id, a.name FROM accounts a JOIN users u ON a.id = u.accountID WHERE a.id IN(");
				for (BasicDynaBean bdb : matches) {
					int id = Integer.parseInt(bdb.get("foreignKey").toString());
					ids.add(id);
				}
				JSONArray jObj = new JSONArray();
				for (final String str : unusedTerms) {
					jObj.add(new JSONObject() {
						{
							put("unused", str);
						}
					});
				}
				result.add(jObj);
				jObj = new JSONArray();
				for (final String str : usedTerms) {
					jObj.add(new JSONObject() {
						{
							put("used", str);
						}
					});
				}
				result.add(jObj);
				query.append(Strings.implode(ids, ",")).append(')');
				List<BasicDynaBean> cons = db.select(query.toString(), false);
				final Hashtable<Integer, Integer> ht = searchEngine.getConIds(permissions);
				for (BasicDynaBean bdb : cons) {
					final String name = bdb.get("name").toString();
					final String id = bdb.get("id").toString();
					result.add(new JSONObject() {
						{
							put("name", name);
							put("id", id);
							if (ht.containsKey(id))
								put("add", false);
							else
								put("add", true);
						}
					});
				}
				json.put("result", result);
				return JSON;
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
					addActionError(getText("RequestNewContractor.error.RequestedContractorNotFound"));
					return BLANK;
				}
			}
		}
		conID = (newContractor.getContractor() == null) ? 0 : newContractor.getContractor().getId();

		return SUCCESS;
	}

	public String save() throws Exception {
		if (Strings.isEmpty(newContractor.getName()))
			addActionError(getText("RequestNewContractor.error.FillContractorName"));
		if (Strings.isEmpty(newContractor.getContact()))
			addActionError(getText("RequestNewContractor.error.FillContactName"));
		if (requestedOperator == 0)
			addActionError(getText("RequestNewContractor.error.SelectRequestedByAccount"));
		if (requestedUser == 0 && Strings.isEmpty(newContractor.getRequestedByUserOther()))
			addActionError(getText("RequestNewContractor.error.SelectRequestedUser"));
		if (country == null)
			addActionError(getText("RequestNewContractor.error.SelectCountry"));
		else if (country.getIsoCode().equals("US") || country.getIsoCode().equals("CA")) {
			if (state == null || Strings.isEmpty(state.getIsoCode()))
				addActionError(getText("RequestNewContractor.error.SelectState"));
		}
		if (Strings.isEmpty(newContractor.getPhone()))
			addActionError(getText("RequestNewContractor.error.FillPhoneNumber"));
		if (Strings.isEmpty(newContractor.getEmail()) || !Strings.isValidEmail(newContractor.getEmail()))
			addActionError(getText("RequestNewContractor.error.FillValidEmail"));
		if (newContractor.getDeadline() == null)
			addActionError(getText("RequestNewContractor.error.SelectDeadline"));
		if (Strings.isEmpty(newContractor.getReasonForRegistration()))
			addActionError(getText("RequestNewContractor.error.EnterRegistrationReason"));

		if (increaseContactCount)
			newContractor.contact();

		if (!status.equals("Hold")) {
			newContractor.setHoldDate(null);
		} else {
			if (newContractor.getHoldDate() == null) {
				addActionError(getText("RequestNewContractor.error.EnterHoldDate"));
			}

			else if ("Active".equals(newContractor.getStatus())) {
				addToNotes = "Request set to hold until " + maskDateFormat(newContractor.getHoldDate());
				String requestLink = "http://www.picsorganizer.com/ContractorRegistration.action?button="
						+ "request&requestID=" + newContractor.getId();

				sendHoldEmail(requestLink);
			}
		}

		if (!status.equals("Closed Unsuccessful"))
			newContractor.setReasonForDecline(null);
		else if (Strings.isEmpty(newContractor.getReasonForDecline()))
			addActionError(getText("RequestNewContractor.error.EnterReasonDeclined"));

		if (status.equals("Active") || status.equals("Hold"))
			newContractor.setOpen(true);
		else
			newContractor.setOpen(false);

		// There are errors, just exit out
		if (getActionErrors().size() > 0)
			return SUCCESS;

		if (country != null && !country.equals(newContractor.getCountry()))
			newContractor.setCountry(country);
		if (state != null && !state.equals(newContractor.getState()))
			newContractor.setState(state);
		if (requestedOperator > 0
				&& (newContractor.getRequestedBy() == null || requestedOperator != newContractor.getRequestedBy()
						.getId())) {
			newContractor.setRequestedBy(operatorAccountDAO.find(requestedOperator));
		}
		if (requestedUser > 0
				&& (newContractor.getRequestedByUser() == null || requestedUser != newContractor.getRequestedByUser()
						.getId())) {
			newContractor.setRequestedByUser(userDAO.find(requestedUser));
			newContractor.setRequestedByUserOther(null);
		} else if (requestedUser == 0) {
			if (!Strings.isEmpty(newContractor.getRequestedByUserOther()))
				newContractor.setRequestedByUser(null);
		}
		if (conID > 0 && (newContractor.getContractor() == null || conID != newContractor.getContractor().getId())) {
			ContractorAccount con = contractorAccountDAO.find(conID);

			if (con != null) {
				newContractor.setContractor(con);
				newContractor.setHandledBy(WaitingOn.Operator);

				if (newContractor.isWatch() && newContractor.getRequestedByUser() != null) {
					// Need to check if the watch exists all ready?
					List<ContractorWatch> existing = userDAO.findContractorWatch(newContractor.getRequestedByUser()
							.getId());
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
				addActionError(getText("RequestNewContractor.error.PICSContractorNotFound"));
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

		newContractor.setAuditColumns(permissions);

		StringBuffer tagIds = new StringBuffer("");
		for (OperatorTag tag : requestedTags) {
			if (tagIds.length() > 0)
				tagIds.append(",");
			tagIds.append("" + tag.getId());
		}
		newContractor.setOperatorTags(tagIds.toString());

		if (newContractor.getId() == 0) {
			newContractor = crrDAO.save(newContractor);

			EmailQueue emailQueue = null;
			if (newContractor.getRequestedByUser() != null
					&& !Strings.isEmpty(newContractor.getRequestedByUser().getEmail()))
				emailQueue = createEmail(newContractor.getRequestedByUser().getEmail());
			else
				emailQueue = createEmail();

			newContractor.setNotes(prepend("Sent email on request creation", newContractor.getNotes()));
			sendEmail(emailQueue);
			OperatorForm form = getForm();
			if (form != null)
				addAttachments(emailQueue, form);

			if (getActionErrors().size() == 0)
				return "backToReport";
			else
				return SUCCESS;
		}

		newContractor = crrDAO.save(newContractor);

		return SUCCESS;
	}

	private void sendHoldEmail(String requestLink) throws IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(163);
		emailBuilder.setFromAddress("\"PICS System\"<marketing@picsauditing.com>");

		emailBuilder.setToAddresses(newContractor.getEmail());
		
		EmailSubscription sub = newContractor.getRequestedByUser()
				.getEmailSubscription(Subscription.RegistrationRequests);
		if (sub == null || sub.getTimePeriod() == SubscriptionTimePeriod.Event)
			emailBuilder.setCcAddresses(newContractor.getRequestedByUser().getEmail());
		
		emailBuilder.addToken("con", newContractor);
		emailBuilder.addToken("op", newContractor.getRequestedBy());
		emailBuilder.addToken("op_contact", newContractor.getRequestedByUser());
		emailBuilder.addToken("deadline", maskDateFormat(newContractor.getDeadline()));
		emailBuilder.addToken("holdDate", maskDateFormat(newContractor.getHoldDate()));
		emailBuilder.addToken("link", requestLink);

		EmailQueue email = emailBuilder.build();
		email.setPriority(30);
		email.setViewableById(Account.PicsID);
		emailQueueDAO.save(email);
	}

	public String phone() throws Exception {
		if (Strings.isEmpty(addToNotes))
			addActionError(getText("RequestNewContractor.error.EnterAdditionalNotes"));

		// Temporarily save add to notes and reset the instance field.
		String addToNotes = this.addToNotes;
		this.addToNotes = null;

		if (getActionErrors().size() == 0)
			return contact("Contacted by phone: " + addToNotes);
		return SUCCESS;
	}

	public String email() throws Exception {
		EmailQueue emailQueue = createEmail();

		sendEmail(emailQueue);
		if (filenames != null && emailQueue != null) {
			for (String filename : filenames)
				addAttachments(emailQueue, filename);
		}

		if (getActionErrors().size() == 0)
			newContractor.setNotes(prepend("Contacted by email", newContractor.getNotes()));
		return SUCCESS;
	}

	private String contact(String notes) {
		newContractor.setNotes(prepend(notes, newContractor.getNotes()));
		newContractor.setLastContactedBy(new User(permissions.getUserId()));
		newContractor.setLastContactDate(new Date());
		newContractor.contact();
		newContractor.setAuditColumns(permissions);
		crrDAO.save(newContractor);

		return SUCCESS;
	}

	public String returnToOperator() {
		if (newContractor.getRequestedByUser() != null || Strings.isValidEmail(newContractor.getRequestedByUserOther())) {

			String requestLink = "http://www.picsorganizer.com/ContractorRegistration.action?button="
					+ "request&requestID=" + newContractor.getId();

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(167);
			emailBuilder.setToAddresses(newContractor.getEmail());
			emailBuilder.setFromAddress("\"PICS System\"<marketing@picsauditing.com>");
			
			EmailSubscription sub = newContractor.getRequestedByUser()
					.getEmailSubscription(Subscription.RegistrationRequests);
			if (sub == null	|| sub.getTimePeriod() == SubscriptionTimePeriod.Event)
				emailBuilder.setCcAddresses(newContractor.getRequestedByUser().getEmail());

			emailBuilder.addToken("con", newContractor);
			emailBuilder.addToken("op", newContractor.getRequestedBy());
			emailBuilder.addToken("op_contact", newContractor.getRequestedByUser());
			emailBuilder.addToken("deadline", maskDateFormat(newContractor.getDeadline()));
			emailBuilder.addToken("link", requestLink);

			try {
				EmailQueue email = emailBuilder.build();
				email.setPriority(30);
				email.setViewableById(Account.PicsID);
				emailQueueDAO.save(email);
			} catch (Exception e) {
				addActionError("Unable to send email notification to operator");
				return SUCCESS;
			}

		}

		newContractor.setHandledBy(WaitingOn.Operator);
		newContractor.setAuditColumns(permissions);
		newContractor = crrDAO.save(newContractor);

		return "backToReport";
	}

	public String returnToPICS() {
		newContractor.setHandledBy(WaitingOn.PICS);
		newContractor.setAuditColumns(permissions);
		newContractor = crrDAO.save(newContractor);

		return "backToReport";
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

	public int getPrimaryContact(int opID) {
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
		else
			return requestedUser;
	}

	public void setRequestedUser(int requestedUser) {
		this.requestedUser = requestedUser;
	}

	public boolean isIncreaseContactCount() {
		return increaseContactCount;
	}

	public void setIncreaseContactCount(boolean increaseContactCount) {
		this.increaseContactCount = increaseContactCount;
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

		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		if (emailBody == null) {
			emailBody = template.getBody();
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
			ContractorAccount temp = new ContractorAccount();
			temp.setId(0);
			temp.setCountry(newContractor.getCountry());
			temp.setState(newContractor.getState());
			temp.setZip(newContractor.getZip());
			UserAssignment ua = csrDAO.findByContractor(temp);

			if (ua != null)
				return ua.getUser();
			else
				return userDAO.find(8397); // Default to Valeree
		}

		return null;
	}

	public boolean isFormsViewable() {
		return permissions.hasPermission(OpPerms.FormsAndDocs);
	}

	public OperatorForm getForm() {
		if (newContractor != null && newContractor.getRequestedBy() != null) {
			List<OperatorAccount> hierarchy = new ArrayList<OperatorAccount>();
			hierarchy.add(newContractor.getRequestedBy());

			List<Facility> corpFac = new ArrayList<Facility>(newContractor.getRequestedBy().getCorporateFacilities());
			Collections.reverse(corpFac);

			for (Facility f : corpFac) {
				if (!f.getCorporate().equals(newContractor.getRequestedBy().getTopAccount())
						&& !Account.PICS_CORPORATE.contains(f.getCorporate().getId()))
					hierarchy.add(f.getCorporate());
			}

			if (!newContractor.getRequestedBy().getTopAccount().equals(newContractor.getRequestedBy())
					&& !Account.PICS_CORPORATE.contains(newContractor.getRequestedBy().getTopAccount().getId()))
				hierarchy.add(newContractor.getRequestedBy().getTopAccount());

			for (OperatorAccount o : hierarchy) {
				for (OperatorForm form : o.getOperatorForms()) {
					if (form.getFormName().contains("*"))
						return form;
				}
			}
		}

		return null;
	}

	public List<OperatorForm> getForms() {
		if (forms == null) {
			Set<OperatorForm> allForms = new HashSet<OperatorForm>();
			Set<OperatorAccount> family = new HashSet<OperatorAccount>();
			family.add(newContractor.getRequestedBy());

			for (Facility f : newContractor.getRequestedBy().getCorporateFacilities()) {
				if (!Account.PICS_CORPORATE.contains(f.getCorporate().getId())) {
					for (Facility f2 : f.getCorporate().getOperatorFacilities())
						family.add(f2.getOperator()); // Siblings
					family.add(f.getCorporate()); // Direct parents
				}
			}

			family.add(newContractor.getRequestedBy().getTopAccount());

			for (OperatorAccount o : family) {
				for (OperatorForm f : o.getOperatorForms()) {
					if (f.getFormName().contains("*"))
						allForms.add(f);
				}
			}

			forms = new ArrayList<OperatorForm>(allForms);
			// Sort alphabetically
			Collections.sort(forms, new Comparator<OperatorForm>() {
				public int compare(OperatorForm o1, OperatorForm o2) {
					if (o1.getAccount().getName().compareTo(o2.getAccount().getName()) == 0)
						return (o1.getFormName().compareTo(o2.getFormName()));

					return o1.getAccount().getName().compareTo(o2.getAccount().getName());
				}
			});
		}

		return forms;
	}

	public String[] getTokens() {
		return names;
	}

	public List<ContractorAccount> runGapAnalysis(ContractorRegistrationRequest newContractor) {
		List<String> terms = new ArrayList<String>();
		terms.add(newContractor.getName());
		terms.add(newContractor.getContact());

		if (!Strings.isEmpty(newContractor.getAddress()))
			terms.add(newContractor.getAddress());
		if (!Strings.isEmpty(newContractor.getPhone()))
			terms.add(newContractor.getPhone());
		if (!Strings.isEmpty(newContractor.getEmail()))
			terms.add(newContractor.getEmail());

		SearchEngine search = new SearchEngine(permissions);
		List<BasicDynaBean> results = newGap(search, Strings.implode(terms, " "), "C");

		Set<Integer> conIDs = new HashSet<Integer>();
		for (BasicDynaBean r : results) {
			conIDs.add(Integer.parseInt(r.get("foreignKey").toString()));
		}

		if (conIDs.size() > 0)
			return contractorAccountDAO.findByContractorIds(conIDs);

		return new ArrayList<ContractorAccount>();
	}

	public List<BasicDynaBean> newGap(SearchEngine searchEngine, String term, String type) {
		unusedTerms = new ArrayList<String>();
		usedTerms = new ArrayList<String>();

		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		Database db = new Database();
		List<String> termsArray = searchEngine.sortSearchTerms(searchEngine.buildTerm(term, false, false), true);
		while (results.isEmpty() && termsArray.size() > 0) {
			String query = searchEngine.buildQuery(null, termsArray, (Strings.isEmpty(type) ? null : "i1.indexType = '"
					+ Utilities.escapeQuotes(type) + "'"), null, 20, false, true);
			try {
				results = db.select(query, false);
			} catch (SQLException e) {
				System.out.println("Error running query in RequestNewCon");
				e.printStackTrace();
				return null;
			}
			if (!searchEngine.getNullTerms().isEmpty() && unusedTerms.isEmpty()) {
				unusedTerms.addAll(searchEngine.getNullTerms());
				termsArray.removeAll(searchEngine.getNullTerms());
			}
			usedTerms = termsArray;
			termsArray = termsArray.subList(0, termsArray.size() - 1);
			// termsArray.subList(1, termsArray.size());
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

	public String[] getNoteReason() {
		return noteReason;
	}

	public void setNoteReason(String[] noteReason) {
		this.noteReason = noteReason;
	}

	private EmailQueue createEmail() {
		return createEmail(null);
	}

	private EmailQueue createEmail(String cc) {
		if (template == null)
			template = templateDAO.find(83);

		String requestLink = "http://www.picsorganizer.com/ContractorRegistration.action?button="
				+ "request&requestID=" + newContractor.getId();
		// Point to the contractor registration page with some information
		// pre-filled
		User csr = getAssignedCSR();
		String[] fields = new String[] {
				newContractor.getName(),
				newContractor.getPhone(),
				newContractor.getEmail(),
				newContractor.getRequestedBy().getName(),
				newContractor.getRequestedByUser() != null ? newContractor.getRequestedByUser().getName()
						: newContractor.getRequestedByUserOther(), newContractor.getContact(),
				newContractor.getTaxID(), newContractor.getAddress(), newContractor.getCity(),
				newContractor.getState() == null ? null : newContractor.getState().getEnglish(),
				newContractor.getZip(), newContractor.getCountry().getEnglish(), csr != null ? csr.getName() : null,
				csr != null ? csr.getEmail() : null, csr != null ? csr.getPhone() : null,
				maskDateFormat(newContractor.getDeadline()), requestLink, picsSignature,
				newContractor.getReasonForRegistration() };

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
		emailQueue.setEmailTemplate(template);
		emailQueue.setPriority(80);
		emailQueue.setHtml(template.isHtml());

		emailQueue.setFromAddress("PICS Auditing <marketing@picsauditing.com>");
		emailQueue.setToAddresses(newContractor.getContact() + " <" + newContractor.getEmail() + ">");
		emailQueue.setBody(emailBody);
		emailQueue.setSubject(emailSubject);

		EmailSubscription sub = newContractor.getRequestedByUser().getEmailSubscription(Subscription.RegistrationRequests);
		if (sub == null || sub.getTimePeriod() == SubscriptionTimePeriod.Event)
			emailQueue.setCcAddresses(cc);

		return emailQueue;
	}

	private void sendEmail(EmailQueue emailQueue) {
		try {
			EmailSender.send(emailQueue);
		} catch (Exception e) {
			addActionError("Could not send registration request email to " + emailQueue.getToAddresses());
		}
	}

	private void addAttachments(EmailQueue emailQueue, OperatorForm form) {
		addAttachments(emailQueue, form.getFile());
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
			attachmentDAO.save(attachment);
		} catch (Exception e) {
			System.out.println("Unable to open file: /forms/" + filename);
		}
	}

	private void loadOperatorTags() {
		List<OperatorTag> list = operatorTagDAO.findByOperator(permissions.getAccountId(), true);

		// add only tags not in request
		for (OperatorTag tag : list) {
			if (!requestedTags.contains(tag))
				operatorTags.add(tag);
		}
	}

	public List<OperatorTag> getRequestedTags() {
		return requestedTags;
	}

	public void setRequestedTags(List<OperatorTag> requestedTags) {
		this.requestedTags = requestedTags;
	}

	public List<OperatorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}