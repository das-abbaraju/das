package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.DataConversionRequestAccount;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.service.RequestNewContractorService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import com.picsauditing.validator.RequestNewContractorValidator;
import com.picsauditing.validator.Validator;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("serial")
public class RequestNewContractorAccount extends ContractorActionSupport implements AjaxValidator {
    public static final String DUPLICATE_CONTRACTOR_NAME = "See duplicate account #";
    public static final String DUPLICATE_CONTRACTOR_FIELD_NAME = "duplicateContractor";
    public static final String DUPLICATE_ID_MISSING_ERROR_MESSAGE = "RequestNewContractor.error.DuplicatedContractorId";
    public static final String SAME_DUPLICATED_CONTRACTOR_ID_ERROR_MESSAGE = "RequestNewContractor.error.SameDuplicatedContractorId";
    @Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private FeatureToggle featureToggle;
	@Autowired
	private OperatorAccountDAO operatorDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private RegistrationRequestEmailHelper emailHelper;
	@Autowired
	private UserSwitchDAO userSwitchDAO;
	@Autowired
	private RequestNewContractorValidator validator;
	@Autowired
	private UserManagementService userManagementService;
    @Autowired
    private RequestNewContractorService requestNewContractorService;

	private ContractorOperator requestRelationship = new ContractorOperator();
	private User primaryContact = new User();
	// Logged in as corporate or PICS user
	private List<ContractorOperator> visibleRelationships = new ArrayList<ContractorOperator>();
	// Contacting Information
	private RequestContactType contactType;
	private String contactNote;
	// Tags
	private List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	private List<OperatorTag> requestedTags = new ArrayList<OperatorTag>();
	// Email
	private EmailQueue email;
	// Links
	private URLUtils urlUtil = new URLUtils();

	private ContractorAccount duplicateContractor;

	public static final String REASON_REQUEST_DECLINED = "Request Declined";

	public enum RequestContactType {
		EMAIL("RequestNewContractor.button.ContactedByEmail", "User.email"),
		PHONE("RequestNewContractor.button.ContactedByPhone", "User.phone"),
		DECLINED("ContractorRegistrationRequest.reasonForDecline", "AccountStatus.Declined");

		private String note;
		private String button;

		private RequestContactType(String note, String button) {
			this.note = note;
			this.button = button;
		}

		public String getNote() {
			return note;
		}

		public String getButton() {
			return button;
		}

		public boolean isDeclined() {
			return this == DECLINED;
		}
	}

	public RequestNewContractorAccount() {
		contractor = new ContractorAccount();
		noteCategory = NoteCategory.Registration;
	}

	@Override
	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String execute() throws Exception {
		if (permissions.isOperatorCorporate()
				&& featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_REQUESTNEWCONTRACTORACCOUNT)) {
			DataConversionRequestAccount justInTimeConversion = new DataConversionRequestAccount(dao, permissions);
			justInTimeConversion.upgrade();
		}

		id = contractor.getId();
		account = contractor;

		loadRelationships();
		initializeRequest();
		loadTags();

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String save() throws Exception {
		boolean newRequest = requestRelationship.getId() == 0;

		saveRequestComponentsAndEmailIfNew(newRequest);
		addActionMessage(getText("RequestNewContractor.SuccessfullySaved"));

		String url = urlUtil.getActionUrl("RequestNewContractorAccount", new HashMap<String, Object>() {
			{
				put("contractor", contractor.getId());

				OperatorAccount clientSiteAccount = requestRelationship.getOperatorAccount();
				int operatorID = (clientSiteAccount != null) ? clientSiteAccount.getId() : 0;

				if (operatorID > 0) {
					put("requestRelationship.operatorAccount", operatorID);
				}
			}
		});

		return setUrlForRedirect(url);
	}

	@SkipValidation
	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String resolveDuplicate() {
		if (duplicateContractor == null) {
			addFieldError(DUPLICATE_CONTRACTOR_FIELD_NAME, getText(DUPLICATE_ID_MISSING_ERROR_MESSAGE));
			return INPUT;
		}
        if (duplicateContractor.getId() == contractor.getId()) {
            addFieldError(DUPLICATE_CONTRACTOR_FIELD_NAME, getText(SAME_DUPLICATED_CONTRACTOR_ID_ERROR_MESSAGE));
            return INPUT;
        }

		ContractorAccount oldContractor = requestRelationship.getContractorAccount();

		contractor = duplicateContractor;
		primaryContact = duplicateContractor.getPrimaryContact();
		requestRelationship.setContractorAccount(duplicateContractor);
		dao.save(requestRelationship);

		oldContractor.setReason(REASON_REQUEST_DECLINED);
		oldContractor.setName(DUPLICATE_CONTRACTOR_NAME + duplicateContractor.getId());
		oldContractor.setStatus(AccountStatus.Deleted);
		dao.save(oldContractor);

		return SUCCESS;
	}

	@SkipValidation
	public String emailPreview() throws Exception {
		email = buildEmail();

		return "email";
	}

	@SkipValidation
	public String load() {
		return SUCCESS;
	}

	public ContractorOperator getRequestRelationship() {
		return requestRelationship;
	}

	public void setRequestRelationship(ContractorOperator requestRelationship) {
		this.requestRelationship = requestRelationship;
	}

	public User getPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(User primaryContact) {
		this.primaryContact = primaryContact;
	}

	public List<ContractorOperator> getVisibleRelationships() {
		return visibleRelationships;
	}

	public RequestContactType getContactType() {
		return contactType;
	}

	public void setContactType(RequestContactType contactType) {
		this.contactType = contactType;
	}

	public String getContactNote() {
		return contactNote;
	}

	public void setContactNote(String contactNote) {
		this.contactNote = contactNote;
	}

	public List<OperatorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	public List<OperatorTag> getRequestedTags() {
		return requestedTags;
	}

	public void setRequestedTags(List<OperatorTag> requestedTags) {
		this.requestedTags = requestedTags;
	}

	public EmailQueue getEmail() {
		return email;
	}

	public void setDuplicateContractor(ContractorAccount duplicateContractor) {
		this.duplicateContractor = duplicateContractor;
	}

	public boolean isContactable() {
		if (permissions.isOperatorCorporate()) {
			return false;
		}

		if (contractor.getId() == 0) {
			return false;
		}

		if (contractor.getPrimaryContact() == null) {
			return false;
		}

		if (Strings.isEmpty(contractor.getPrimaryContact().getPhone())) {
			return false;
		}

		return true;
	}

	public List<User> getOperatorUsers() {
		int operatorID = requestRelationship.getOperatorAccount().getId();

		List<User> usersAndSwitchTos = userDAO.findByAccountID(operatorID, "Yes", "No");
		List<User> switchTos = userSwitchDAO.findUsersBySwitchToAccount(operatorID);

		usersAndSwitchTos.addAll(switchTos);
		return usersAndSwitchTos;
	}

	public RequestContactType[] getContactTypes() {
		return RequestContactType.values();
	}

	public boolean isContactNoteMissing() {
		if (contactType != null) {
			return Strings.isEmpty(contactNote);
		}

		return false;
	}

	private void initializeRequest() {
		if (contractor.getId() == 0) {
			contractor.setStatus(AccountStatus.Requested);

			contractor.setRequestedBy(new OperatorAccount());

			contractor.setCountry(new Country());
			contractor.getCountry().setIsoCode(permissions.getCountry());
		} else {
			account = contractor;
			primaryContact = contractor.getPrimaryContact();
		}
	}

	private void loadRelationships() {
		if (permissions.isOperator()) {
			for (ContractorOperator relationship : contractor.getOperators()) {
				if (relationship.getOperatorAccount().getId() == permissions.getAccountId()) {
					requestRelationship = relationship;
				}
			}

			if (requestRelationship.getOperatorAccount() == null) {
				requestRelationship.setOperatorAccount(operatorDAO.find(permissions.getAccountId()));
			}
		} else {
			if (permissions.isCorporate()) {
				for (ContractorOperator relationship : contractor.getOperators()) {
					if (permissions.getOperatorChildren().contains(relationship.getOperatorAccount().getId())) {
						visibleRelationships.add(relationship);
					}
				}
			} else {
				visibleRelationships.addAll(contractor.getOperators());
			}
		}

		if (requestRelationship.getOperatorAccount() == null) {
			requestRelationship.setOperatorAccount(new OperatorAccount());
		} else {
			for (ContractorOperator relationship : contractor.getOperators()) {
				if (relationship.getOperatorAccount().getId() == requestRelationship.getOperatorAccount().getId()) {
					requestRelationship = relationship;
				}
			}
		}
	}

    public List<OperatorAccount> getOperatorList() throws Exception {
        List<OperatorAccount> list;
        if (permissions.isAdmin()) {
            list = operatorDAO.findWhere(false, "a.status in ('Active')");
        } else {
            list = super.getOperatorList();
        }
        return list;
    }

	private void loadTags() {
		int operatorID = 0;

		if (permissions.isOperator()) {
			operatorID = permissions.getAccountId();
		} else if (requestRelationship.getOperatorAccount().getId() > 0) {
			operatorID = requestRelationship.getOperatorAccount().getId();
		}

		if (operatorID > 0) {
			operatorTags = operatorTagDAO.findByOperator(operatorID, true);

			for (ContractorTag tag : getViewableExistingContractorTags(operatorTags)) {
				requestedTags.add(tag.getTag());
			}

			operatorTags.removeAll(requestedTags);
		}
	}

	private List<ContractorTag> getViewableExistingContractorTags(List<OperatorTag> viewable) {
		List<ContractorTag> existingViewable = new ArrayList<ContractorTag>();

		for (ContractorTag contractorTag : contractor.getOperatorTags()) {
			if (viewable.contains(contractorTag.getTag())) {
				existingViewable.add(contractorTag);
			}
		}

		return existingViewable;
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveRequestComponentsAndEmailIfNew(boolean newRequest) throws Exception {
        requestNewContractorService.setPermissions(permissions);
        contractor = requestNewContractorService.saveRequestingContractor(contractor, requestRelationship.getOperatorAccount());
        primaryContact = requestNewContractorService.savePrimaryContact(contractor, primaryContact);
        requestRelationship = requestNewContractorService.saveRelationship(contractor, requestRelationship);

		if (contactType == RequestContactType.DECLINED) {
			contractor.setStatus(AccountStatus.Declined);
			contractor.setReason(contactNote);
		}

		if (newRequest) {
			emailHelper.sendInitialEmail(contractor, primaryContact, requestRelationship, getFtpDir());

			if (requestRelationship.getRequestedBy() != null) {
				contractor.setLastContactedByInsideSales(requestRelationship.getRequestedBy());
			} else {
				contractor.setLastContactedByInsideSales(permissions.getUserId());
			}

			Date now = new Date();

			contractor.contactByEmail();
			contractor.setLastContactedByInsideSalesDate(now);
			contractor.setLastContactedByAutomatedEmailDate(now);

			addNote("Sent initial contact email.","");
		}

		saveNoteIfContacted();
		saveOperatorTags();

		contractor = (ContractorAccount) contractorAccountDAO.save(contractor);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveRequiredFieldsAndSaveEntities() throws Exception {
		// NAICS is required for accounts
		if (contractor.getId() == 0) {
			contractor.setNaics(new Naics());
			contractor.getNaics().setCode("0");
			contractor.setRequestedBy(requestRelationship.getOperatorAccount());
			contractor.generateRegistrationHash();
			if (requestRelationship.getOperatorAccount() != null) {
				if (requestRelationship.getOperatorAccount().getStatus().isActive()
						&& !"No".equals(requestRelationship.getOperatorAccount().getDoContractorsPay())) {
					contractor.setPayingFacilities(1);
				}
			}
		}

		contractor.setAuditColumns(permissions);
		contractor = (ContractorAccount) contractorAccountDAO.save(contractor);

		// Username and isGroup is required
		if (primaryContact.getId() == 0) {
			primaryContact.setAccount(contractor);
			primaryContact.setIsGroup(YesNo.No);

			boolean usernameIsAlreadyTaken = userDAO.duplicateUsername(primaryContact.getEmail(), 0);
			if (usernameIsAlreadyTaken) {
				primaryContact.setUsername(String.format("%s-%d", primaryContact.getEmail(), contractor.getId()));
			} else {
				primaryContact.setUsername(primaryContact.getEmail());
			}
			primaryContact.setName(primaryContact.getFirstName() + " " + primaryContact.getLastName());

			primaryContact.setAuditColumns(permissions);

			contractor.setPrimaryContact(primaryContact);
			contractor.getUsers().add(primaryContact);
		}

		primaryContact.setPhoneIndex(Strings.stripPhoneNumber(primaryContact.getPhone()));
		primaryContact = userManagementService.saveWithAuditColumnsAndRefresh(primaryContact, permissions);

		// Flag is required for contractorOperator
		if (requestRelationship.getId() == 0) {
			requestRelationship.setFlagColor(FlagColor.Clear);
		}

		requestRelationship.setContractorAccount(contractor);
		requestRelationship.setAuditColumns(permissions);
		requestRelationship = (ContractorOperator) contractorOperatorDAO.save(requestRelationship);
	}

	private EmailQueue buildEmail() throws Exception {
		return emailHelper.buildInitialEmail(contractor, primaryContact, requestRelationship);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void addNote(String summary, String additionalNote) {
		// Save notes to RR note field and a new Note entity
		OperatorAccount clientSiteAccount = requestRelationship.getOperatorAccount();
		int clientSiteId = (clientSiteAccount != null) ? clientSiteAccount.getId() : 1;

		addNote(contractor, summary, additionalNote, NoteCategory.Registration, LowMedHigh.Low, true, clientSiteId);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveNoteIfContacted() {
		if (contactType != null) {
			addNote(getText(contactType.getNote()) + ": ", contactNote);

			Date now = new Date();

			if (RequestContactType.EMAIL == contactType) {
				contractor.contactByEmail();
			} else if (RequestContactType.PHONE == contactType) {
				contractor.contactByPhone();
			}

			contractor.setLastContactedByInsideSales(permissions.getUserId());
			contractor.setLastContactedByInsideSalesDate(now);
		}
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveOperatorTags() {
		List<ContractorTag> existing = getVisibleExistingTags();

		removeUnneededTags(existing);
		removeExistingTagsFromSelected(existing);
		addRemainingTags();
	}

	private List<ContractorTag> getVisibleExistingTags() {
		List<ContractorTag> existing = new ArrayList<ContractorTag>(contractor.getOperatorTags());

		if (permissions.isOperatorCorporate()) {
			List<OperatorTag> viewable = operatorTagDAO.findByOperator(permissions.getAccountId(), true);
			existing = getViewableExistingContractorTags(viewable);
		}

		return existing;
	}

	private void removeUnneededTags(List<ContractorTag> existingViewable) {
		Iterator<ContractorTag> tagIterator = existingViewable.iterator();
		while (tagIterator.hasNext()) {
			ContractorTag existingTag = tagIterator.next();

			if (!requestedTags.contains(existingTag.getTag())) {
				tagIterator.remove();
				contractorTagDAO.remove(existingTag);
				contractor.getOperatorTags().remove(existingTag);
			}
		}
	}

	private void removeExistingTagsFromSelected(List<ContractorTag> existingViewable) {
		for (ContractorTag existing : existingViewable) {
			requestedTags.remove(existing.getTag());
		}
	}

	private void addRemainingTags() {
		for (OperatorTag operatorTag : requestedTags) {
			ContractorTag tag = new ContractorTag();
			tag.setContractor(contractor);
			tag.setTag(operatorTag);
			tag.setAuditColumns(permissions);

			contractorTagDAO.save(tag);

			contractor.getOperatorTags().add(tag);
		}
	}

	// For the Ajax Validation
	public Validator getCustomValidator() {
		return validator;
	}

	// For server-side validation
	@Override
	public void validate() {
		validator.validate(ActionContext.getContext().getValueStack(), new DelegatingValidatorContext(this));
	}

	public boolean isShowDeclinedButton() {
		AccountStatus accountStatus = getContractor().getStatus();
		return accountStatus.allowRegistrationToBeMarkedDeclined();
	}

	public boolean isShowDuplicatedButton() {
		AccountStatus accountStatus = getContractor().getStatus();
		return accountStatus.allowRegistrationToBeMarkedDuplicated();
	}
}