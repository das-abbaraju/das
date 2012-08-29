package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractorAccount extends ContractorActionSupport {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private UserSwitchDAO userSwitchDAO;

	private final int EMAIL_TEMPLATE = 259;

	private ContractorAccount requestedContractor = new ContractorAccount();
	private ContractorOperator requestRelationship = new ContractorOperator();
	// Email
	private EmailBuilder emailBuilder = new EmailBuilder();

	@Override
	public String execute() throws Exception {
		checkPermission();

		initializeRequest();
		setRequestedBy();

		return SUCCESS;
	}

	public String save() throws Exception {
		// Fields are already validated
		// See RequestNewContractorAccount-validation.xml

		// Set operator on both contractor and contractorOperator
		OperatorAccount requestedBy = findOperator();
		requestedContractor.setRequestedBy(requestedBy);
		requestRelationship.setOperatorAccount(requestedBy);

		// NAICS is required for accounts
		requestedContractor.setNaics(new Naics());
		requestedContractor.getNaics().setCode("0");
		requestedContractor.setAuditColumns(permissions);
		requestedContractor = (ContractorAccount) contractorAccountDao.save(requestedContractor);

		// Flag is required for contractorOperator
		requestRelationship.setContractorAccount(requestedContractor);
		requestRelationship.setFlagColor(FlagColor.Clear);
		requestRelationship.setAuditColumns(permissions);
		requestRelationship = (ContractorOperator) contractorOperatorDAO.save(requestRelationship);

		// Copy information to new request object (full copy for now)
		// TODO: update request to just the fields needed after this release
		// makes it to beta
		saveLegacyRequest(requestedBy);
		requestedContractor = (ContractorAccount) contractorAccountDao.save(requestedContractor);

		// Find contractor letter, if any
		OperatorForm contractorLetter = getContractorLetter();
		// Make email
		User info = userDAO.find(User.INFO_AT_PICSAUDITING);

		emailBuilder.setTemplate(EMAIL_TEMPLATE);
		emailBuilder.setToAddresses(requestedContractor.getPrimaryContact().getEmail());
		emailBuilder.setFromAddress(info);
		emailBuilder.addToken("requestedContractor", requestedContractor);
		emailBuilder.addToken("requestRelationship", requestRelationship);
		EmailQueue email = emailBuilder.build();
		// Add attachment
		// Send it off to email sender

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
		int operatorID = requestRelationship.getOperatorAccount().getId();
		List<User> usersAndSwitchTos = userDAO.findByAccountID(operatorID, "Yes", "No");
		List<User> switchTos = userSwitchDAO.findUsersBySwitchToAccount(operatorID);

		usersAndSwitchTos.addAll(switchTos);
		return usersAndSwitchTos;
	}

	private void initializeRequest() {
		if (requestedContractor.getId() == 0) {
			requestedContractor.setStatus(AccountStatus.Requested);

			ContractorRegistrationRequest request = new ContractorRegistrationRequest();
			requestedContractor.getRegistrationRequests().add(request);

			requestedContractor.setRequestedBy(new OperatorAccount());
			requestedContractor.setPrimaryContact(new User());
		}
	}

	private void setRequestedBy() {
		if (permissions.isOperatorCorporate()) {
			OperatorAccount requestedBy = operatorDAO.find(permissions.getAccountId());

			if (permissions.isOperator() && requestedContractor.getId() == 0
					&& requestedContractor.getRequestedBy() == null) {
				requestedContractor.setRequestedBy(requestedBy);
			}

			for (ContractorOperator contractorOperator : requestedContractor.getOperators()) {
				if (contractorOperator.getOperatorAccount().equals(requestedContractor.getRequestedBy())) {
					requestRelationship = contractorOperator;
				}
			}
		}
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
}
