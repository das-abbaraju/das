package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.Files;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractorAccount extends ContractorActionSupport {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private EmailAttachmentDAO attachmentDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private UserSwitchDAO userSwitchDAO;

	private static Logger logger = LoggerFactory.getLogger(RequestNewContractorAccount.class);

	private final int INITIAL_CONTACT_EMAIL_TEMPLATE = 259;

	private ContractorAccount requestedContractor = new ContractorAccount();
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
	private EmailQueue email = new EmailQueue();
	private EmailBuilder emailBuilder = new EmailBuilder();
	// Legacy
	private ContractorRegistrationRequest legacyRequest = new ContractorRegistrationRequest();
	private ContractorRegistrationRequestStatus status = ContractorRegistrationRequestStatus.Active;

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

	@Override
	public String execute() throws Exception {
		checkPermissions();
		initializeRequest();
		loadRelationships();
		loadTags();

		return SUCCESS;
	}

	@SkipValidation
	public String contact() {
		return "contactModal";
	}

	public String save() throws Exception {
		checkPermissions();

		boolean newRequest = requestRelationship.getId() == 0;

		saveRequestComponentsAndEmailIfNew(newRequest);

		int operatorID = requestRelationship.getOperatorAccount().getId();

		addActionMessage(getText("RequestNewContractor.SuccessfullySaved"));
		return setUrlForRedirect("RequestNewContractorAccount.action?requestedContractor="
				+ requestedContractor.getId()
				+ (operatorID > 0 ? "&requestRelationship.operatorAccount=" + operatorID : ""));
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

	public ContractorAccount getRequestedContractor() {
		return requestedContractor;
	}

	public void setRequestedContractor(ContractorAccount requestedContractor) {
		this.requestedContractor = requestedContractor;
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

		if (requestedContractor.getId() == 0) {
			return false;
		}

		if (requestedContractor.getPrimaryContact() == null) {
			return false;
		}

		if (Strings.isEmpty(requestedContractor.getPrimaryContact().getPhone())) {
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

	public List<ContractorRegistrationRequestStatus> getApplicableStatuses() {
		List<ContractorRegistrationRequestStatus> applicableStatuses = new ArrayList<ContractorRegistrationRequestStatus>();
		applicableStatuses.add(ContractorRegistrationRequestStatus.Active);
		applicableStatuses.add(ContractorRegistrationRequestStatus.Hold);
		applicableStatuses.add(ContractorRegistrationRequestStatus.ClosedUnsuccessful);

		return applicableStatuses;
	}

	public Date getToday() {
		return new Date();
	}

	private void checkPermissions() throws NoRightsException {
		if (!permissions.isOperatorCorporate() && !permissions.isPicsEmployee()) {
			throw new NoRightsException(getText("global.Operators"));
		}
	}

	private void initializeRequest() {
		if (requestedContractor.getId() == 0) {
			requestedContractor.setStatus(AccountStatus.Requested);

			requestedContractor.setRequestedBy(new OperatorAccount());

			requestedContractor.setCountry(new Country());
			requestedContractor.getCountry().setIsoCode(permissions.getCountry());
		} else {
			id = requestedContractor.getId();
			account = requestedContractor;
			primaryContact = requestedContractor.getPrimaryContact();

			setRequestStatus();
		}
	}

	private void loadRelationships() {
		if (permissions.isOperator()) {
			for (ContractorOperator relationship : requestedContractor.getOperators()) {
				if (relationship.getOperatorAccount().getId() == permissions.getAccountId()) {
					requestRelationship = relationship;
				}
			}

			if (requestRelationship.getOperatorAccount() == null) {
				requestRelationship.setOperatorAccount(operatorDAO.find(permissions.getAccountId()));
			}
		} else {
			if (permissions.isCorporate()) {
				for (ContractorOperator relationship : requestedContractor.getOperators()) {
					if (permissions.getOperatorChildren().contains(relationship.getOperatorAccount().getId())) {
						visibleRelationships.add(relationship);
					}
				}
			} else {
				visibleRelationships.addAll(requestedContractor.getOperators());
			}
		}

		if (requestRelationship.getOperatorAccount() == null) {
			requestRelationship.setOperatorAccount(new OperatorAccount());
		} else {
			for (ContractorOperator relationship : requestedContractor.getOperators()) {
				if (relationship.getOperatorAccount().getId() == requestRelationship.getOperatorAccount().getId()) {
					requestRelationship = relationship;
				}
			}
		}
	}

	private void setRequestStatus() {
		AccountStatus requestedContractorStatus = requestedContractor.getStatus();

		if (requestedContractorStatus.isRequested()) {
			if (requestedContractor.getFollowUpDate() == null) {
				status = ContractorRegistrationRequestStatus.Active;
			} else {
				status = ContractorRegistrationRequestStatus.Hold;
			}
		} else if (requestedContractorStatus.isActive() || requestedContractorStatus.isPending()) {
			if (requestedContractor.getContactCountByPhone() > 0) {
				status = ContractorRegistrationRequestStatus.ClosedContactedSuccessful;
			} else {
				status = ContractorRegistrationRequestStatus.ClosedSuccessful;
			}
		} else {
			status = ContractorRegistrationRequestStatus.ClosedUnsuccessful;
		}
	}

	private void loadTags() {
		if (permissions.isOperator()) {
			operatorTags = operatorTagDAO.findByOperator(permissions.getAccountId(), true);

			for (ContractorTag tag : getViewableExistingContractorTags(operatorTags)) {
				requestedTags.add(tag.getTag());
			}

			operatorTags.removeAll(requestedTags);
		}
	}

	private List<ContractorTag> getViewableExistingContractorTags(List<OperatorTag> viewable) {
		List<ContractorTag> existingViewable = new ArrayList<ContractorTag>();

		for (ContractorTag contractorTag : requestedContractor.getOperatorTags()) {
			if (viewable.contains(contractorTag.getTag())) {
				existingViewable.add(contractorTag);
			}
		}

		return existingViewable;
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveRequestComponentsAndEmailIfNew(boolean newRequest) throws Exception {
		saveRequiredFieldsAndSaveEntities();
		saveLegacyRequest();

		if (newRequest) {
			email = buildEmail();
			email.setContractorAccount(requestedContractor);
			emailSender.send(email);
			legacyRequest.contactByEmail();
			requestedContractor.contactByEmail();
			requestedContractor.setLastContactedByAutomatedEmailDate(new Date());

			addContractorLetterAttachmentTo(email);
			addNote("Sent initial contact email.");
		}

		saveNoteIfContacted();
		saveOperatorTags();

		legacyRequest = (ContractorRegistrationRequest) dao.save(legacyRequest);
		requestedContractor = (ContractorAccount) contractorAccountDao.save(requestedContractor);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveRequiredFieldsAndSaveEntities() throws Exception {
		// NAICS is required for accounts
		if (requestedContractor.getId() == 0) {
			requestedContractor.setNaics(new Naics());
			requestedContractor.getNaics().setCode("0");
			requestedContractor.setRequestedBy(requestRelationship.getOperatorAccount());
		}

		requestedContractor.setAuditColumns(permissions);
		requestedContractor = (ContractorAccount) contractorAccountDao.save(requestedContractor);

		// Username and isGroup is required
		if (primaryContact.getId() == 0) {
			primaryContact.setAccount(requestedContractor);
			primaryContact.setIsGroup(YesNo.No);
			primaryContact.setUsername(String.format("%s-%d", primaryContact.getEmail(), requestedContractor.getId()));
			primaryContact.setAuditColumns(permissions);

			requestedContractor.setPrimaryContact(primaryContact);
			requestedContractor.getUsers().add(primaryContact);
		}

		primaryContact = (User) dao.save(primaryContact);

		// Flag is required for contractorOperator
		if (requestRelationship.getId() == 0) {
			requestRelationship.setFlagColor(FlagColor.Clear);
		}

		requestRelationship.setContractorAccount(requestedContractor);
		requestRelationship.setAuditColumns(permissions);
		requestRelationship = (ContractorOperator) contractorOperatorDAO.save(requestRelationship);
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private ContractorRegistrationRequest saveLegacyRequest() {
		loadLegacyRequest();

		legacyRequest.setName(requestedContractor.getName());
		legacyRequest.setContact(requestedContractor.getPrimaryContact().getName());
		legacyRequest.setPhone(requestedContractor.getPrimaryContact().getPhone());
		legacyRequest.setEmail(requestedContractor.getPrimaryContact().getEmail());
		legacyRequest.setTaxID(requestedContractor.getTaxId());
		legacyRequest.setAddress(requestedContractor.getAddress());
		legacyRequest.setCity(requestedContractor.getCity());
		legacyRequest.setZip(requestedContractor.getZip());
		legacyRequest.setCountry(requestedContractor.getCountry());
		legacyRequest.setCountrySubdivision(requestedContractor.getCountrySubdivision());

		legacyRequest.setReasonForRegistration(requestRelationship.getReasonForRegistration());
		legacyRequest.setDeadline(requestRelationship.getDeadline());
		legacyRequest.setRequestedBy(requestedContractor.getRequestedBy());
		legacyRequest.setRequestedByUser(requestRelationship.getRequestedBy());
		legacyRequest.setRequestedByUserOther(requestRelationship.getRequestedByOther());
		legacyRequest.setContractor(requestedContractor);
		legacyRequest.setAuditColumns(permissions);

		return legacyRequest;
	}

	private void loadLegacyRequest() {
		if (requestedContractor.getId() > 0) {
			List<ContractorRegistrationRequest> requestList = requestDAO.findWhere(ContractorRegistrationRequest.class,
					"t.contractor.id = " + requestedContractor.getId());

			if (requestList != null && !requestList.isEmpty()) {
				legacyRequest = requestList.get(0);
			}
		}
	}

	private EmailQueue buildEmail() throws Exception {
		User info = userDAO.find(User.INFO_AT_PICSAUDITING);

		emailBuilder.setTemplate(INITIAL_CONTACT_EMAIL_TEMPLATE);
		emailBuilder.setToAddresses(primaryContact.getEmail());
		emailBuilder.setFromAddress(info);
		emailBuilder.addToken("requestedContractor", requestedContractor);
		emailBuilder.addToken("requestRelationship", requestRelationship);
		emailBuilder.addToken("primaryContact", primaryContact);

		return emailBuilder.build();
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void addContractorLetterAttachmentTo(EmailQueue email) {
		OperatorForm contractorLetter = getContractorLetter();

		if (contractorLetter != null) {
			try {
				File letter = null;

				if (contractorLetter.getFile().startsWith("form")) {
					letter = new File(getFtpDir() + "/forms/" + contractorLetter.getFile());
				} else {
					letter = new File(getFtpDir() + "/files/" + FileUtils.thousandize(contractorLetter.getId())
							+ contractorLetter.getFile());
				}

				byte[] content = Files.toByteArray(letter);

				// Add attachment
				EmailAttachment attachment = new EmailAttachment();
				attachment.setContent(content);
				attachment.setFileName(letter.getAbsolutePath());
				attachment.setFileSize((int) letter.length());
				attachment.setEmailQueue(email);
				attachmentDAO.save(attachment);
			} catch (Exception e) {
				logger.error("Exception trying to attach operator form: #{} '{}'\n{}",
						new Object[] { contractorLetter.getId(), contractorLetter.getFile(), e });
			}
		}
	}

	private OperatorForm getContractorLetter() {
		Set<OperatorAccount> processed = new HashSet<OperatorAccount>();
		OperatorAccount currentOperator = requestRelationship.getOperatorAccount();

		while (currentOperator != null && !processed.contains(currentOperator)) {
			for (OperatorForm form : currentOperator.getOperatorForms()) {
				if (form.getFormName().contains("*")) {
					return form;
				}
			}

			processed.add(currentOperator);
			currentOperator = currentOperator.getParent();
		}

		return null;
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void addNote(String note) {
		// Save notes to RR note field and a new Note entity
		legacyRequest.addToNotes(note, userDAO.find(permissions.getUserId()));

		addNote(requestedContractor, note, noteCategory, requestRelationship.getOperatorAccount().getId());
	}

	@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
	private void saveNoteIfContacted() {
		if (contactType != null) {
			addNote(getText(contactType.getI18nKey()) + ": " + contactNote);

			if (RequestContactType.EMAIL == contactType) {
				legacyRequest.contactByEmail();
				requestedContractor.contactByEmail();
			} else {
				legacyRequest.contactByPhone();
				requestedContractor.contactByPhone();
			}
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
		List<ContractorTag> existing = new ArrayList<ContractorTag>(requestedContractor.getOperatorTags());

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
				requestedContractor.getOperatorTags().remove(existingTag);
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
			tag.setContractor(requestedContractor);
			tag.setTag(operatorTag);
			tag.setAuditColumns(permissions);

			contractorTagDAO.save(tag);

			requestedContractor.getOperatorTags().add(tag);
		}
	}
}