package com.picsauditing.validator;

import java.util.Locale;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.contractors.Registration;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;

public class RegistrationValidator implements Validator {

	// This context is required for all validation error messages
	private Registration registrationAction;
	private ValidatorContext validatorContext;

	@Autowired
	private InputValidator inputValidator;
	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private VATValidator vatValidator;

	private static I18nCache i18nCache;

	@Override
	public void validate() {
		if (validatorContext == null) {
			throw new IllegalStateException("You must set the ValidatorContext to use this validator.");
		}

		ContractorAccount contractor = registrationAction.getContractor();
		User user = registrationAction.getUser();
		String language = registrationAction.getLanguage();
		String dialect = registrationAction.getDialect();

		if (contractor == null || user == null || language == null || dialect == null) {
			return;
		}

		// Contractor Account
		String errorMessageKey = inputValidator.validateCompanyName(contractor.getName());
		addFieldErrorIfMessage("contractor.name", errorMessageKey);

		Country country = contractor.getCountry();
		String countryIso = "";
		if (country != null) {
			countryIso = country.getIsoCode();
		}

		if (!inputValidator.isLanguageValid(language, registrationAction.getSupportedLanguages())) {
			language = Locale.ENGLISH.getLanguage();
		}

		if (StringUtils.isEmpty(dialect) || !inputValidator.containsOnlySafeCharacters(dialect)) {
			addFieldError("contractor.dialect", getText(InputValidator.REQUIRED_KEY));
		}

		errorMessageKey = inputValidator.validateName(countryIso);
		addFieldErrorIfMessage("contractor.country.isoCode", errorMessageKey);

		errorMessageKey = inputValidator.validateName(contractor.getAddress());
		addFieldErrorIfMessage("contractor.address", errorMessageKey);

		errorMessageKey = inputValidator.validateName(contractor.getCity());
		addFieldErrorIfMessage("contractor.city", errorMessageKey);

		if (shouldRequireZipCode(contractor.getCountry())) {
			errorMessageKey = inputValidator.validateName(contractor.getZip());
			addFieldErrorIfMessage("contractor.zip", errorMessageKey);
		}

		if (!isValidVAT(contractor.getVatId(), contractor.getCountry())) {
			addFieldErrorIfMessage("contractor.vatId", InputValidator.INVALID_VAT_ID_KEY);
		}

		// User
		errorMessageKey = inputValidator.validateName(user.getName());
		addFieldErrorIfMessage("user.name", errorMessageKey);

		errorMessageKey = inputValidator.validateEmail(user.getEmail());
		addFieldErrorIfMessage("user.email", errorMessageKey);

		errorMessageKey = inputValidator.validatePhoneNumber(user.getPhone());
		addFieldErrorIfMessage("user.phone", errorMessageKey);

		errorMessageKey = inputValidator.validateUsername(user.getUsername());
		addFieldErrorIfMessage("user.username", errorMessageKey);
		errorMessageKey = inputValidator.validateUsernameAvailable(user.getUsername(), user.getId());
		addFieldErrorIfMessage("user.username", errorMessageKey);

		Vector<String> errors = passwordValidator.validatePassword(user, user.getPassword());
		if (CollectionUtils.isNotEmpty(errors)) {
			addFieldError("user.password", errors.get(0));
		}

		if (!StringUtils.equals(user.getPassword(), registrationAction.getConfirmPassword())) {
			addFieldErrorIfMessage("confirmPassword", InputValidator.PASSWORDS_MUST_MATCH_KEY);
		}
	}

	private boolean isValidVAT(String vat, Country country) {
		if (vatValidator.shouldValidate(country)) {
			try {
				vatValidator.validated(vat);
			} catch (Exception e) {
				return false;
			}
		}

		if (StringUtils.isNotEmpty(vat)) {
			return inputValidator.containsOnlySafeCharacters(vat);
		}

		return true;
	}

	private boolean shouldRequireZipCode(Country country) {
		if (country == null) {
			return false;
		}

		// Carryover from existing XML validation
		if (StringUtils.equals(Country.UAE_ISO_CODE, country.getIsoCode())) {
			return false;
		}

		return true;
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
		return getI18nCache().getText(key, registrationAction.getLocale());
	}


	public void setAction(Object action) {
		registrationAction = (Registration) action;
	}

	// the purpose of this is for testing
	private I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}

	@Override
	public void setValidatorContext(ValidatorContext validatorContext) {
		this.validatorContext = validatorContext;
	}

}
