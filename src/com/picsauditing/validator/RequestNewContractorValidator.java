package com.picsauditing.validator;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.contractors.RequestNewContractorAccount;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class RequestNewContractorValidator implements Validator {

	@Autowired
	private InputValidator inputValidator;

	private ValidatorContext validatorContext;

	// For testing
	private I18nCache i18nCache;

	@Override
	public void validate(ValueStack valueStack, ValidatorContext validatorContext) {
		if (validatorContext == null) {
			throw new IllegalStateException("You must set the ValidatorContext to use this validator.");
		}

		this.validatorContext = validatorContext;

		ContractorAccount contractor = (ContractorAccount) valueStack.findValue("contractor");

		AccountStatus status = contractor.getStatus();

		if (status.isDeclined() || status.isDeactivated() || status.isDeleted()) {
			return;
		}

		String errorMessageKey = inputValidator.validateName(contractor.getName());
		addFieldErrorIfMessage("contractor.name", errorMessageKey);

		User primaryContact = (User) valueStack.findValue("primaryContact");

		errorMessageKey = inputValidator.validateFirstName(primaryContact.getFirstName());
		addFieldErrorIfMessage("primaryContact.firstName", errorMessageKey);

		errorMessageKey = inputValidator.validateLastName(primaryContact.getLastName());
		addFieldErrorIfMessage("primaryContact.lastName", errorMessageKey);

		errorMessageKey = inputValidator.validatePhoneNumber(primaryContact.getPhone());
		addFieldErrorIfMessage("primaryContact.phone", errorMessageKey);

		errorMessageKey = inputValidator.validateEmail(primaryContact.getEmail());
		addFieldErrorIfMessage("primaryContact.email", errorMessageKey);

		Country country = contractor.getCountry();

		if (country == null || StringUtils.isEmpty(country.getIsoCode())) {
			addFieldError("contractor.country", getText("RequestNewContractor.error.SelectCountry"));
		}

		if (country != null
				&& country.isHasCountrySubdivisions()
				&& contractor.getCountrySubdivision() == null) {
			addFieldError("contractor.countrySubdivision", getText("RequestNewContractor.error.SelectCountrySubdivision"));
		}

		ContractorOperator requestRelationship = (ContractorOperator) valueStack.findValue("requestRelationship");

		if (requestRelationship.getOperatorAccount() == null || requestRelationship.getOperatorAccount().getId() == 0) {
			addFieldError("requestRelationship.operatorAccount", getText("RequestNewContractor.error.SelectRequestedByAccount"));
		}

		if (requestRelationship.getRequestedByName() == null) {
			addFieldError("requestRelationship.requestedBy", getText("RequestNewContractor.error.SelectRequestedUser"));
		}

		Date deadline = requestRelationship.getDeadline();

		if (deadline == null) {
			addFieldError("requestRelationship.deadline", getText("RequestNewContractor.error.SelectDeadline"));
		} else if (deadline.before(new Date())) {
			addFieldError("requestRelationship.deadline", getText("RequestNewContractor.error.PastDeadline"));
		}

		if (StringUtils.isEmpty(requestRelationship.getReasonForRegistration())) {
			addFieldError("requestRelationship.reasonForRegistration", getText("RequestNewContractor.error.EnterRegistrationReason"));
		}

		RequestNewContractorAccount.RequestContactType contactType = (RequestNewContractorAccount.RequestContactType) valueStack.findValue("contactType");
		String contactNote = valueStack.findString("contactNote");

		if (contactType != null && StringUtils.isEmpty(contactNote)) {
			validatorContext.addActionError(getText("RequestNewContractor.error.EnterAdditionalNotes"));
		}
	}

	private void addFieldError(String fieldName, String errorMessage) {
		validatorContext.addFieldError(fieldName, errorMessage);
	}

	private void addFieldErrorIfMessage(String fieldName, String errorMessageKey) {
		if (StringUtils.isNotEmpty(errorMessageKey)) {
			addFieldError(fieldName, getText(errorMessageKey));
		}
	}

	private String getText(String key) {
		return getI18nCache().getText(key, validatorContext.getLocale());
	}

	// the purpose of this is for testing
	private I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}
}
