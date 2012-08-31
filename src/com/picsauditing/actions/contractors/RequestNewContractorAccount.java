package com.picsauditing.actions.contractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.util.StrutsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.io.Files;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
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
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractorAccount extends ContractorActionSupport {
	private static Logger logger = LoggerFactory.getLogger(RequestNewContractorAccount.class);

	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private EmailAttachmentDAO attachmentDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private UserSwitchDAO userSwitchDAO;

	private final int EMAIL_TEMPLATE = 259;

	private ContractorAccount requestedContractor = new ContractorAccount();
	private ContractorOperator requestRelationship = new ContractorOperator();
	private User primaryContact = new User();

	private List<OperatorTag> operatorTags;
	private List<OperatorTag> requestedTags;

	private OperatorTag[] remainingOperatorTags;
	private OperatorTag[] selectedRequestedTags;

	// Email
	private EmailBuilder emailBuilder = new EmailBuilder();

	@Override
	public String execute() throws Exception {
		checkPermission();
		initializeRequest();
		setRequestedBy();
		loadTags();

		primaryContact = requestedContractor.getPrimaryContact();

		return SUCCESS;
	}

	public String save() throws Exception {
		boolean newRequest = requestedContractor.getId() == 0;

		OperatorAccount requestedBy = findOperator();
		requestedContractor.setRequestedBy(requestedBy);
		requestRelationship.setOperatorAccount(requestedBy);

		setRequiredFieldsAndSaveEntities(newRequest);
		ContractorRegistrationRequest legacyRequest = saveLegacyRequest(requestedBy);

		if (newRequest) {
			EmailQueue email = buildEmail();
			email.setContractorAccount(requestedContractor);
			emailSender.send(email);
			requestedContractor.getFirstRegistrationRequest().contactByEmail();
			requestedContractor.getFirstRegistrationRequest().setLastContactedByAutomatedEmailDate(new Date());

			addContractorLetterAttachmentTo(email);
			addNote(legacyRequest);
		}

		setOperatorTags();
		requestedContractor = (ContractorAccount) contractorAccountDao.save(requestedContractor);

		addActionMessage(getText("RequestNewContractor.SuccessfullySaved"));
		return setUrlForRedirect("RequestNewContractorAccount.action?requestedContractor="
				+ requestedContractor.getId());
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

	public List<OperatorTag> getOperatorTags() {
		if (operatorTags == null) {
			loadTags();
		}

		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	public List<OperatorTag> getRequestedTags() {
		if (requestedTags == null) {
			loadTags();
		}

		return requestedTags;
	}

	public void setRequestedTags(List<OperatorTag> requestedTags) {
		this.requestedTags = requestedTags;
	}

	public OperatorTag[] getRemainingOperatorTags() {
		return remainingOperatorTags;
	}

	public void setRemainingOperatorTags(OperatorTag[] remainingOperatorTags) {
		this.remainingOperatorTags = remainingOperatorTags;
	}

	public OperatorTag[] getSelectedRequestedTags() {
		return selectedRequestedTags;
	}

	public void setSelectedRequestedTags(OperatorTag[] selectedRequestedTags) {
		this.selectedRequestedTags = selectedRequestedTags;
	}

	public String getEmailPreview() {
		return null;
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
		OperatorAccount operator = findOperator();
		List<User> usersAndSwitchTos = userDAO.findByAccountID(operator.getId(), "Yes", "No");
		List<User> switchTos = userSwitchDAO.findUsersBySwitchToAccount(operator.getId());

		usersAndSwitchTos.addAll(switchTos);
		return usersAndSwitchTos;
	}

	public Date getToday() {
		return new Date();
	}

	private void checkPermission() throws NoRightsException {
		if (!permissions.isOperatorCorporate() && !permissions.isPicsEmployee()) {
			throw new NoRightsException(getText("global.Operators"));
		}

		if (permissions.isOperatorCorporate() && requestedContractor.getRequestedBy() != null
				&& !permissions.getVisibleAccounts().contains(requestedContractor.getRequestedBy().getId())) {
			throw new NoRightsException(getText("AccountType.Admin"));
		}
	}

	private void initializeRequest() {
		if (requestedContractor.getId() == 0) {
			requestedContractor.setStatus(AccountStatus.Requested);

			ContractorRegistrationRequest request = new ContractorRegistrationRequest();
			requestedContractor.getRegistrationRequests().add(request);

			requestedContractor.setRequestedBy(new OperatorAccount());

			requestedContractor.setCountry(new Country());
			requestedContractor.getCountry().setIsoCode(permissions.getCountry());
		}
	}

	private void setRequestedBy() {
		if (permissions.isOperatorCorporate()) {
			OperatorAccount requestedBy = operatorDAO.find(permissions.getAccountId());

			if (permissions.isOperator() && requestedContractor.getId() == 0
					&& requestedContractor.getRequestedBy() == null) {
				requestedContractor.setRequestedBy(requestedBy);
			}
		}

		for (ContractorOperator contractorOperator : requestedContractor.getOperators()) {
			if (contractorOperator.getOperatorAccount().equals(requestedContractor.getRequestedBy())) {
				requestRelationship = contractorOperator;
			}
		}
	}

	private void loadTags() {
		OperatorAccount operator = findOperator();

		if (requestedTags == null) {
			requestedTags = new ArrayList<OperatorTag>();
		}

		operatorTags = getOperatorViewableTags(operator.getId());
		for (ContractorTag tag : getViewableExistingContractorTags(operatorTags)) {
			requestedTags.add(tag.getTag());
		}

		operatorTags.removeAll(requestedTags);
	}

	private ContractorRegistrationRequest saveLegacyRequest(OperatorAccount requestedBy) {
		ContractorRegistrationRequest legacyRequest = new ContractorRegistrationRequest();

		if (requestedContractor.getRegistrationRequests().size() > 0) {
			legacyRequest = requestedContractor.getFirstRegistrationRequest();
		}

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
		legacyRequest.setRequestedBy(requestedBy);
		legacyRequest.setRequestedByUser(requestRelationship.getRequestedBy());
		legacyRequest.setRequestedByUserOther(requestRelationship.getRequestedByOther());
		legacyRequest.setContractor(requestedContractor);
		legacyRequest.generateHash();
		legacyRequest.setAuditColumns(permissions);

		legacyRequest = requestDAO.save(legacyRequest);

		if (requestedContractor.getFirstRegistrationRequest() == null) {
			requestedContractor.getRegistrationRequests().add(legacyRequest);
		}

		return legacyRequest;
	}

	private OperatorForm getContractorLetter() {
		return null;
	}

	private OperatorAccount findOperator() {
		if (requestRelationship.getOperatorAccount() != null) {
			return requestRelationship.getOperatorAccount();
		}

		if (requestedContractor.getRequestedBy() != null) {
			return requestedContractor.getRequestedBy();
		}

		if (permissions.isOperator()) {
			return operatorDAO.find(permissions.getAccountId());
		}

		return new OperatorAccount();
	}

	private void setRequiredFieldsAndSaveEntities(boolean newRequest) {
		// NAICS is required for accounts
		if (newRequest) {
			requestedContractor.setNaics(new Naics());
			requestedContractor.getNaics().setCode("0");
		}

		requestedContractor.setAuditColumns(permissions);
		requestedContractor = (ContractorAccount) contractorAccountDao.save(requestedContractor);

		// Username and isGroup is required
		if (newRequest) {
			primaryContact.setAccount(requestedContractor);
			primaryContact.setIsGroup(YesNo.No);
			primaryContact.setUsername(String.format("%s-%tF-%d", primaryContact.getEmail(), new Date(),
					requestedContractor.getId()));
			primaryContact.setAuditColumns(permissions);

			requestedContractor.setPrimaryContact(primaryContact);
			requestedContractor.getUsers().add(primaryContact);
		}

		primaryContact = (User) dao.save(primaryContact);

		// Flag is required for contractorOperator
		if (newRequest) {
			requestRelationship.setFlagColor(FlagColor.Clear);
		}

		requestRelationship.setContractorAccount(requestedContractor);
		requestRelationship.setAuditColumns(permissions);
		requestRelationship = (ContractorOperator) contractorOperatorDAO.save(requestRelationship);
	}

	private EmailQueue buildEmail() throws IOException {
		User info = userDAO.find(User.INFO_AT_PICSAUDITING);

		emailBuilder.setTemplate(EMAIL_TEMPLATE);
		emailBuilder.setToAddresses(requestedContractor.getPrimaryContact().getEmail());
		emailBuilder.setFromAddress(info);
		emailBuilder.addToken("requestedContractor", requestedContractor);
		emailBuilder.addToken("requestRelationship", requestRelationship);

		return emailBuilder.build();
	}

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

	private void addNote(ContractorRegistrationRequest legacyRequest) {
		// Save notes to RR note field and a new Note entity
		String note = "Sent initial contact email.";
		legacyRequest.addToNotes(note, userDAO.find(permissions.getUserId()));
		addNote(requestedContractor, note);
	}

	private void setOperatorTags() {
		List<ContractorTag> existing = getVisibleExistingTags();

		removeUnneededTags(existing);
		removeExistingTagsFromSelected(existing);
		addRemainingTags();
	}

	private List<ContractorTag> getVisibleExistingTags() {
		List<ContractorTag> existing = new ArrayList<ContractorTag>(requestedContractor.getOperatorTags());

		if (permissions.isOperatorCorporate()) {
			List<OperatorTag> viewable = getOperatorViewableTags(permissions.getAccountId());
			existing = getViewableExistingContractorTags(viewable);
		}

		return existing;
	}

	private List<OperatorTag> getOperatorViewableTags(int accountID) {
		List<OperatorTag> viewable = operatorTagDAO.findByOperator(accountID, true);
		return viewable;
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