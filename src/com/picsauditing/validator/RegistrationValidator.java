package com.picsauditing.validator;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Vector;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

public class RegistrationValidator implements Validator {

	private static final String CONFIRM_PASSWORD_KEY = "confirmPassword";

	@Autowired
	private InputValidator inputValidator;
	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private VATValidator vatValidator;

	private static I18nCache i18nCache;

	private ValidatorContext validatorContext;

	@Override
	public void validate(ValueStack valueStack, ValidatorContext validatorContext) {
		if (validatorContext == null) {
			throw new IllegalStateException("You must set the ValidatorContext to use this validator.");
		}

		HttpServletRequest request = (HttpServletRequest) valueStack.getContext().get(HTTP_REQUEST);
		if (request != null && MapUtils.isNotEmpty(request.getParameterMap())
				&& request.getParameterMap().containsKey("language") && request.getParameterMap().size() == 1
				&& AjaxUtils.isAjax(request)) {
			return;
		}

		this.validatorContext = validatorContext;

		ContractorAccount contractor = (ContractorAccount) valueStack.findValue("contractor");
		User user = (User) valueStack.findValue("user");
		String language = valueStack.findString("language");
		String dialect = valueStack.findString("dialect");

		String countrySubdivision = valueStack.findString("countrySubdivision");

		if (contractor == null) {
			contractor = new ContractorAccount();
		}

		if (user == null) {
			user = new User();
		}

		// Contractor Account
		String errorMessageKey = inputValidator.validateCompanyName(contractor.getName());
		addFieldErrorIfMessage("contractor.name", errorMessageKey);

		Country country = contractor.getCountry();
		String countryIso = Strings.EMPTY_STRING;
		if (country != null) {
			countryIso = country.getIsoCode();
		}

		if (Strings.isEmpty(countrySubdivision)) {
			if ((country != null && country.isHasCountrySubdivisions()) || Country.COUNTRIES_WITH_SUBDIVISIONS
					.contains(countryIso)) {
				addFieldError("countrySubdivision", getText(InputValidator.REQUIRED_KEY));
			}
		}

		if (!inputValidator.isLanguageValid(language, (LanguageModel) valueStack.findValue("supportedLanguages"))) {
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
		errorMessageKey = inputValidator.validateFirstName(user.getFirstName());
		addFieldErrorIfMessage("user.firstName", errorMessageKey);

		errorMessageKey = inputValidator.validateLastName(user.getLastName());
		addFieldErrorIfMessage("user.lastName", errorMessageKey);

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

		if (!StringUtils.equals(user.getPassword(), valueStack.findString(CONFIRM_PASSWORD_KEY))) {
			addFieldErrorIfMessage(CONFIRM_PASSWORD_KEY, InputValidator.PASSWORDS_MUST_MATCH_KEY);
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
