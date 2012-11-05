package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;

@SuppressWarnings("serial")
public class RequestNewContractorAccount extends ContractorActionSupport {
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private OperatorAccountDAO operatorDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private RegistrationRequestEmailHelper emailHelper;
	@Autowired
	private UserSwitchDAO userSwitchDAO;

	private ContractorOperator requestRelationship = new ContractorOperator();
	private User primaryContact = new User();
	private ContractorRegistrationRequestStatus status = ContractorRegistrationRequestStatus.Active;
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

	public enum RequestContactType implements Translatable {
		EMAIL, PHONE;

		@Override
		public String getI18nKey() {
			return "RequestNewContractor.button.ContactedBy" + this.toString().substring(0, 1).toUpperCase()
					+ this.toString().substring(1).toLowerCase();
		}

		@Override
		public String getI18nKey(String property) {
			return getI18nKey() + "." + property;
		}
	}

	public RequestNewContractorAccount() {
		contractor = new ContractorAccount();
		noteCategory = NoteCategory.Registration;
	}

	@Override
	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String execute() throws Exception {
		id = contractor.getId();
		account = contractor;

		loadRelationships();
		initializeRequest();
		loadTags();

		return SUCCESS;
	}

	@SkipValidation
	public String contact() {
		return "contactModal";
	}

	@RequiredPermission(value = OpPerms.RequestNewContractor)
	public String save() throws Exception {
		boolean newRequest = requestRelationship.getId() == 0;

		saveRequestComponentsAndEmailIfNew(newRequest);
		addActionMessage(getText("RequestNewContractor.SuccessfullySaved"));

		String url = urlUtil.getActionUrl("RequestNewContractorAccount", new HashMap<String, Object>() {
			{
				put("contractor", contractor.getId());

				int operatorID = requestRelationship.getOperatorAccount().getId();
				if (operatorID > 0) {
					put("requestRelationship.operatorAccount", operatorID);
				}
			}
		});
		return setUrlForRedirect(url);
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

	public ContractorRegistrationRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ContractorRegistrationRequestStatus status) {
		this.status = status;
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

	public Date getToday() {
		return new Date();
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

			setRequestStatus();
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

	private void setRequestStatus() {
		AccountStatus requestedContractorStatus = contractor.getStatus();

		if (requestedContractorStatus.isRequested()) {
			if (contractor.getFollowUpDate() == null) {
				status = ContractorRegistrationRequestStatus.Active;
			} else {
				status = ContractorRegistrationRequestStatus.Hold;
			}
		} else if (requestedContractorStatus.isActive() || requestedContractorStatus.isPending()) {
			if (contractor.getContactCountByPhone() > 0) {
				status = ContractorRegistrationRequestStatus.ClosedContactedSuccessful;
			} else {
				status = ContractorRegistrationRequestStatus.ClosedSuccessful;
			}
		} else {
			status = ContractorRegistrationRequestStatus.ClosedUnsuccessful;
		}
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
		saveRequiredFieldsAndSaveEntities();

		if (status.isClosedUnsuccessful()) {
			contractor.setStatus(AccountStatus.Declined);
		} else if (status.isClosedSuccessful() || status.isClosedContactedSuccessful()) {
			contractor.setStatus(AccountStatus.Active);
		} else {
			contractor.setStatus(AccountStatus.Requested);
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

			addNote("Sent initial contact email.");
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

			primaryContact.setAuditColumns(permissions);

			contractor.setPrimaryContact(primaryContact);
			contractor.getUsers().add(primaryContact);
		}

		primaryContact.setPhoneIndex(Strings.stripPhoneNumber(primaryContact.getPhone()));
		primaryContact = (User) dao.save(primaryContact);

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
	private void addNote(String additionalNote) {
		// Save notes to RR note field and a new Note entity
		addNote(contractor, additionalNote, NoteCategory.Registration, LowMedHigh.Low, true, requestRelationship
				.getOperatorAccount().getId(), null);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveNoteIfContacted() {
		if (contactType != null) {
			addNote(getText(contactType.getI18nKey()) + ": " + contactNote);

			Date now = new Date();

			if (RequestContactType.EMAIL == contactType) {
				contractor.contactByEmail();
			} else {
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
}