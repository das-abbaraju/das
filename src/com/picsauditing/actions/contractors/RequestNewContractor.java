package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("serial")
public class RequestNewContractor extends AccountActionSupport {
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private ContractorRegistrationRequestDAO crrDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private RegistrationRequestEmailHelper emailHelper;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private UserSwitchDAO userSwitchDAO;

	private String requestedTagIds;
	private List<String> rightAnswers;

	private List<OperatorTag> requestedTags = new ArrayList<OperatorTag>();
	// selected tags of requestor

	private ContractorRegistrationRequest newContractor;
	private ContractorRegistrationRequestStatus status = ContractorRegistrationRequestStatus.Active;

	private int opID;
	private String addToNotes;

	private String contactType;

	// fields used for matching
	private String term;
	private String type;
	private List<String> unusedTerms;
	private List<String> usedTerms;
	private boolean continueCheck = true;
	private List<ContractorAccount> potentialMatches;

	private int requestID;
	// For previews
	private EmailQueue email;
	private Locale emailLanguage;
	// URL helper
	private URLUtils urlUtils = new URLUtils();

	public static final String PERSONAL_EMAIL = "Personal Email";
	public static final String DRAFT_EMAIL = "Email";
	public static final String PHONE = "Phone";

	private final Logger LOG = LoggerFactory.getLogger(RequestNewContractor.class);

	public String execute() throws Exception {
		if (!permissions.isPicsEmployee() && !permissions.isOperatorCorporate()) {
			throw new NoRightsException(getText("global.Operator"));
		}

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

		if (matches != null && !matches.isEmpty()) {
			continueCheck = false;
		} else {
			return null;
		}

		Database db = new Database();
		JSONArray result = new JSONArray();
		List<Integer> ids = new ArrayList<Integer>();
		StringBuilder query = new StringBuilder();
		result.add(continueCheck);

		if ("C".equalsIgnoreCase(type)) {
			query.append("SELECT a.id, a.name FROM accounts a WHERE a.status IN ('Active', 'Pending', 'Requested') AND a.id IN (");
		} else if ("U".equalsIgnoreCase(type)) {
			query.append("SELECT a.id, a.name FROM accounts a JOIN users u ON a.id = u.accountID WHERE a.id IN(");
		}

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
			final int id = Integer.parseInt(bdb.get("id").toString());

			final boolean visible = ht.containsKey(id);

			result.add(new JSONObject() {
				{
					put("name", name);
					put("id", id);

					if (visible || permissions.isPicsEmployee()) {
						put("url", urlUtils.getActionUrl("ContractorView", "id", id));
					} else {
						put("url", urlUtils.getActionUrl("NewContractorSearch", null, new HashMap<String, Object>() {
							{
								put("filter.performedBy", "Self Performed");
								put("filter.primaryInformation", true);
								put("filter.tradeInformation", true);
								put("filter.accountName", name);
								put("button", "Search");
							}
						}, false));
					}
				}
			});
		}

		json.put("result", result);
		return JSON;
	}

	public String matchingList() {
		if ("MatchingList".equals(button)) {
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
		// (\__/)
		// (='.'=)
		// (")_(")
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

		transferTagsToContractor();

		if (newContractor.getId() == 0) {
			newContractor.setStatus(ContractorRegistrationRequestStatus.Active);

			newContractor.setLastContactedBy(userDAO.find(permissions.getUserId()));
			newContractor.setLastContactDate(new Date());

			String notes = "Sent initial contact email.";
			prependToRequestNotes(notes);
			newContractor.contactByEmail();

			// Save the contractor before sending the email
			newContractor = crrDAO.save(newContractor);
			emailHelper.sendInitialEmail(newContractor, emailLanguage, getFtpDir());
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
			emailHelper.sendInitialEmail(newContractor, getFtpDir());
			newContractor.contactByEmail();
		} else if (PERSONAL_EMAIL.equals(contactType)) {
			newContractor.contactByEmail();
		} else
			newContractor.contactByPhone();

		prependToRequestNotes(notes);
		newContractor.setLastContactedBy(new User(permissions.getUserId()));
		newContractor.setLastContactDate(new Date());

		crrDAO.save(newContractor);

		return setUrlForRedirect(urlUtils.getActionUrl("RequestNewContractor", "newContractor", newContractor.getId()));
	}

	public String emailPreview() throws Exception {
		email = buildInitialEmail();

		return "email";
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
				LOG.error("Error running query in RequestNewCon");
				LOG.error("{}", e.getStackTrace());
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

	public List<String> getRightAnswers() {
		return rightAnswers;
	}

	public void setRightAnswers(List<String> rightAnswers) {
		this.rightAnswers = rightAnswers;
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

	public EmailQueue getEmail() {
		return email;
	}

	public Locale getEmailLanguage() {
		return emailLanguage;
	}

	public void setEmailLanguage(Locale emailLanguage) {
		this.emailLanguage = emailLanguage;
	}

	private EmailQueue buildInitialEmail() throws Exception {
		return emailHelper.buildInitialEmail(newContractor, emailLanguage);
	}

	private void checkContactFields() {
		if (Strings.isEmpty(newContractor.getName()))
			addActionError(getText("RequestNewContractor.error.FillContractorName"));

		if (Strings.isEmpty(newContractor.getContact()))
			addActionError(getText("RequestNewContractor.error.FillContactName"));

		if (newContractor.getCountry() == null) {
			addActionError(getText("RequestNewContractor.error.SelectCountry"));
		} else if (newContractor.getCountry().isHasCountrySubdivisions() && (newContractor.getCountrySubdivision() == null
				|| Strings.isEmpty(newContractor.getCountrySubdivision().getIsoCode()))) {
			addActionError(getText("RequestNewContractor.error.SelectCountrySubdivision"));
		}

		if (Strings.isEmpty(newContractor.getPhone()))
			addActionError(getText("RequestNewContractor.error.FillPhoneNumber"));

		if (Strings.isEmpty(newContractor.getEmail()) || !EmailAddressUtils.isValidEmail(newContractor.getEmail()))
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

	private void transferTagsToContractor() {
		if (newContractor.getContractor() != null && !Strings.isEmpty(newContractor.getOperatorTags())) {
			for (String tagID : newContractor.getOperatorTags().split(",")) {
				try {
					OperatorTag tag = operatorTagDAO.find(Integer.parseInt(tagID));

					if (!contractorHasTag(tag)
							&& newContractor.getContractor().getOperatorAccounts().contains(tag.getOperator())) {
						ContractorTag contractorTag = new ContractorTag();
						contractorTag.setContractor(newContractor.getContractor());
						contractorTag.setTag(tag);
						contractorTag.setAuditColumns(permissions);

						dao.save(contractorTag);
					}
				} catch (Exception exception) {
					LOG.error("Error with transfering tag {} from request {} to contractor {}\n{}", new Object[]{
							tagID, newContractor.getId(), newContractor.getContractor().getId(), exception});
				}
			}
		}
	}

	private boolean contractorHasTag(OperatorTag tag) {
		if (newContractor.getContractor() != null) {
			for (ContractorTag contractorTag : newContractor.getContractor().getOperatorTags()) {
				if (contractorTag.getTag().equals(tag)) {
					return true;
				}
			}
		}

		return false;
	}
}
