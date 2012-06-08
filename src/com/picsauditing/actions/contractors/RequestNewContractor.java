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

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractor extends AccountActionSupport {
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected ContractorRegistrationRequestDAO crrDAO;
	@Autowired
	protected EmailAttachmentDAO attachmentDAO;
	@Autowired
	protected EmailTemplateDAO templateDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	protected OperatorTagDAO operatorTagDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserSwitchDAO userSwitchDAO;
	
	@Autowired
	protected EmailSenderSpring emailSenderSpring;

	protected String requestedTagIds;
	protected List<String> rightAnswers;

	protected List<OperatorForm> forms;
	protected List<OperatorTag> requestedTags = new ArrayList<OperatorTag>();
	// selected tags of requestor
	protected List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	// available tags of the operator

	private ContractorRegistrationRequest newContractor;
	private ContractorRegistrationRequestStatus status = ContractorRegistrationRequestStatus.Active;

	private int opID;
	private String addToNotes;

	private String contactType;

	// fields used for matching
	protected String term;
	protected String type;
	protected List<String> unusedTerms;
	protected List<String> usedTerms;
	protected boolean continueCheck = true;
	protected List<ContractorAccount> potentialMatches;

	private int requestID;

	private static final int INITIAL_EMAIL = 83;
	public static final String PERSONAL_EMAIL = "Personal Email";
	public static final String DRAFT_EMAIL = "Email";
	public static final String PHONE = "Phone";

	public String execute() {
		if (newContractor == null || newContractor.getId() == 0) {
			newContractor = new ContractorRegistrationRequest();
			if (!permissions.isPicsEmployee()) {
				newContractor.setRequestedBy(operatorAccountDAO.find(permissions.getAccountId()));
				newContractor.setRequestedByUser(userDAO.find(permissions.getUserId()));
				opID = newContractor.getRequestedBy().getId();
			}

			if (permissions.isOperatorCorporate()) {
				addActionMessage(getText("RequestNewContractor.help.Purpose"));
			}
		} else {
			status = newContractor.getStatus();
			opID = newContractor.getRequestedBy().getId();
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String ajaxCheck() throws Exception {
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

	public String matchingList() {
		if (button.equals("MatchingList")) {
			if (newContractor != null) {
				newContractor = crrDAO.find(newContractor.getId());
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
		return SUCCESS;

	}

	public String loadTags() {
		/*	
		 *  (\__/)
		*	(='.'=)
		*	(")_(")
		*/ 
		return SUCCESS;
	}

	public String save() throws Exception {
		checkContactFields();
		checkOperatorSpecifiedFields();
		checkStatusRequirements();

		// There are errors, just exit out
		if (getActionErrors().size() > 0)
			return SUCCESS;

		potentialMatches = runGapAnalysis(newContractor);
		if (potentialMatches.size() > 0)
			newContractor.setMatchCount(potentialMatches.size());

		requestedTagIds = "";

		List<Integer> requestedIds = new ArrayList<Integer>();
		for (OperatorTag tag : requestedTags)
			requestedIds.add(tag.getId());
		newContractor.setOperatorTags(Strings.implode(requestedIds));

		if (!Strings.isEmpty(newContractor.getRequestedByUserOther()))
			newContractor.setRequestedByUser(null);

		newContractor.setAuditColumns(permissions);

		if (status == ContractorRegistrationRequestStatus.Hold) {
			if (newContractor.getStatus() != ContractorRegistrationRequestStatus.Hold) {
				String notes = "Request set to Hold until " + maskDateFormat(newContractor.getHoldDate());
				prependToRequestNotes(notes);
			}
		} else {
			newContractor.setHoldDate(null);
		}

		if (newContractor.getId() == 0) {
			newContractor.setStatus(ContractorRegistrationRequestStatus.Active);

			newContractor.setLastContactedBy(userDAO.find(permissions.getUserId()));
			newContractor.setLastContactDate(new Date());

			String notes = "Sent initial contact email.";
			prependToRequestNotes(notes);
			newContractor.contactByEmail();

			// Save the contractor before sending the email
			newContractor = crrDAO.save(newContractor);
			sendEmail();
		} else {
			if (status != null)
				newContractor.setStatus(status);

			if (ContractorRegistrationRequestStatus.ClosedContactedSuccessful == newContractor.getStatus()) {
				prependToRequestNotes("Contacted Closed Successful with contractor in PICS System.");
			} else if (ContractorRegistrationRequestStatus.ClosedSuccessful == newContractor.getStatus()) {
				prependToRequestNotes("Closed Successful with contractor in PICS System.");
			}

			newContractor = crrDAO.save(newContractor);
		}

		addActionMessage(getText("RequestNewContractor.SuccessfullySaved"));
		return SUCCESS;
	}

	public String contact() throws Exception {
		if (Strings.isEmpty(addToNotes) && !PERSONAL_EMAIL.equals(contactType)) {
			addActionError(getText("RequestNewContractor.error.EnterAdditionalNotes"));
			return SUCCESS;
		}

		String notes = "Contacted by " + contactType + (PERSONAL_EMAIL.equals(contactType) ? "" : ": " + addToNotes);

		if (DRAFT_EMAIL.equals(contactType)) {
			sendEmail();
			newContractor.contactByEmail();
		} else if (PERSONAL_EMAIL.equals(contactType)) {
			newContractor.contactByEmail();
		} else
			newContractor.contactByPhone();

		prependToRequestNotes(notes);
		newContractor.setLastContactedBy(new User(permissions.getUserId()));
		newContractor.setLastContactDate(new Date());

		crrDAO.save(newContractor);

		return redirect("RequestNewContractor.action?newContractor=" + newContractor.getId());
	}

	public EmailQueue previewEmail() throws IOException {
		EmailBuilder builder = prepareEmailBuilder();
		return builder.build();
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
					+ Strings.escapeQuotes(type) + "'"), null, 20, false, true);
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

	public List<OperatorAccount> getOperatorsList() {
		if (permissions == null)
			return null;
		return operatorAccountDAO.findWhere(false, "", permissions);
	}

	public List<User> getUsersList(int accountID) {
		List<User> usersAndSwitchTos = userDAO.findByAccountID(accountID, "Yes", "No");
		List<User> switchTos = userSwitchDAO.findUsersBySwitchToAccount(accountID);

		usersAndSwitchTos.addAll(switchTos);
		return usersAndSwitchTos;
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

	public ContractorRegistrationRequest getNewContractor() {
		return newContractor;
	}

	public void setNewContractor(ContractorRegistrationRequest newContractor) {
		this.newContractor = newContractor;
	}

	public String getAddToNotes() {
		return addToNotes;
	}

	public void setAddToNotes(String addToNotes) {
		this.addToNotes = addToNotes;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public List<OperatorTag> getRequestedTags() {
		return requestedTags;
	}

	public void setRequestedTags(List<OperatorTag> requestedTags) {
		this.requestedTags = requestedTags;
	}

	public List<OperatorTag> getOperatorTags() {
		List<OperatorTag> results = operatorTagDAO.findByOperator(opID, true);
		loadRequestedTags();
		results.removeAll(requestedTags);
		return results;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	public List<String> getRightAnswers() {
		return rightAnswers;
	}

	public void setRightAnswers(List<String> rightAnswers) {
		this.rightAnswers = rightAnswers;
	}

	public String getDraftEmailSubject() {
		return templateDAO.find(INITIAL_EMAIL).getSubject();
	}

	public String getDraftEmailBody() {
		return templateDAO.find(INITIAL_EMAIL).getBody();
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
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

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public ContractorRegistrationRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ContractorRegistrationRequestStatus status) {
		this.status = status;
	}

	private void checkContactFields() {
		if (Strings.isEmpty(newContractor.getName()))
			addActionError(getText("RequestNewContractor.error.FillContractorName"));

		if (Strings.isEmpty(newContractor.getContact()))
			addActionError(getText("RequestNewContractor.error.FillContactName"));

		if (newContractor.getCountry() == null)
			addActionError(getText("RequestNewContractor.error.SelectCountry"));
		else if (newContractor.getCountry().getIsoCode().equals("US")
				|| newContractor.getCountry().getIsoCode().equals("CA")) {
			if (newContractor.getState() == null || Strings.isEmpty(newContractor.getState().getIsoCode()))
				addActionError(getText("RequestNewContractor.error.SelectState"));
		}

		if (Strings.isEmpty(newContractor.getPhone()))
			addActionError(getText("RequestNewContractor.error.FillPhoneNumber"));

		if (Strings.isEmpty(newContractor.getEmail()) || !Strings.isValidEmail(newContractor.getEmail()))
			addActionError(getText("RequestNewContractor.error.FillValidEmail"));
	}

	private void checkOperatorSpecifiedFields() {
		if (newContractor.getRequestedBy() == null)
			addActionError(getText("RequestNewContractor.error.SelectRequestedByAccount"));

		if (newContractor.getRequestedByUser() == null && Strings.isEmpty(newContractor.getRequestedByUserOther()))
			addActionError(getText("RequestNewContractor.error.SelectRequestedUser"));

		if (newContractor.getDeadline() == null)
			addActionError(getText("RequestNewContractor.error.SelectDeadline"));

		if (Strings.isEmpty(newContractor.getReasonForRegistration()))
			addActionError(getText("RequestNewContractor.error.EnterRegistrationReason"));
	}

	private void checkStatusRequirements() {
		if (ContractorRegistrationRequestStatus.Hold.equals(status) && newContractor.getHoldDate() == null)
			addActionError(getText("RequestNewContractor.error.EnterHoldDate"));

		if ((ContractorRegistrationRequestStatus.ClosedContactedSuccessful.equals(status) || ContractorRegistrationRequestStatus.ClosedSuccessful
				.equals(status)) && newContractor.getContractor() == null) {
			addActionError(getText("RequestNewContractor.error.PICSContractorNotFound"));
		}

		if (ContractorRegistrationRequestStatus.ClosedUnsuccessful.equals(status)
				&& Strings.isEmpty(newContractor.getReasonForDecline()))
			addActionError(getText("RequestNewContractor.error.EnterReasonDeclined"));

		if (newContractor.getId() > 0 && status == null && newContractor.getStatus() == null)
			addActionError(getText("RequestNewContractor.error.StatusMissing"));
	}

	private void prependToRequestNotes(String note) {
		if (newContractor != null && note != null)
			newContractor.setNotes(maskDateFormat(new Date()) + " - " + permissions.getName() + " - " + note
					+ (newContractor.getNotes() != null ? "\n\n" + newContractor.getNotes() : ""));
	}

	private void sendEmail() {
		if (newContractor.getRequestedBy().getId() != OperatorAccount.SALES) {
			EmailBuilder emailBuilder = prepareEmailBuilder();
			try {
				EmailQueue q = emailBuilder.build();
				emailSenderSpring.send(q);
				OperatorForm form = getForm();
				if (form != null)
					addAttachments(q, form);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private EmailBuilder prepareEmailBuilder() {
		EmailBuilder email = new EmailBuilder();
		email.setToAddresses(newContractor.getEmail());

		email.setFromAddress("info@picsauditing.com");

		email.setTemplate(INITIAL_EMAIL);
		email.addToken("newContractor", newContractor);
		return email;
	}

	private void loadRequestedTags() {
		requestedTags.clear();

		if (newContractor == null || newContractor.getId() < 0)
			return;

		requestedTagIds = newContractor.getOperatorTags();

		if (!Strings.isEmpty(requestedTagIds)) {
			StringTokenizer st = new StringTokenizer(requestedTagIds, ", ");
			while (st.hasMoreTokens()) {
				OperatorTag tag = operatorTagDAO.find(Integer.parseInt(st.nextToken()));
				if (tag != null) {
					requestedTags.add(tag);
				}
			}
		}
	}

	private void addAttachments(EmailQueue emailQueue, OperatorForm form) {
		String filename = FileUtils.thousandize(form.getId()) + form.getFile();

		try {
			EmailAttachment attachment = new EmailAttachment();

			File file = new File(getFtpDir() + "/files/" + filename);

			byte[] bytes = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bytes);

			attachment.setFileName(getFtpDir() + "/files/" + filename);
			attachment.setContent(bytes);
			attachment.setFileSize((int) file.length());
			attachment.setEmailQueue(emailQueue);
			attachmentDAO.save(attachment);
		} catch (Exception e) {
			System.out.println("Unable to open file: /files/" + filename);
		}
	}
}