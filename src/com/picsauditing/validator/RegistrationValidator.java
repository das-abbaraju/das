package com.picsauditing.validator;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.Strings;

public class RegistrationValidator implements Validator {

	private static final String CONFIRM_PASSWORD_KEY = "confirmPassword";

	@Autowired
	private InputValidator inputValidator;
	@Autowired
	private PasswordValidator passwordValidator;
	@Autowired
	private VATValidator vatValidator;

	private Map<String, String> errors = new HashMap<String, String>();
	private static I18nCache i18nCache;

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

		Map<String, String> errors = validate2((User) valueStack.findValue("user"), valueStack.findString("language"),
				valueStack.findString("dialect"), (ContractorAccount) valueStack.findValue("contractor"),
				valueStack.findString("countrySubdivision"),
				(LanguageModel) valueStack.findValue("supportedLanguages"), valueStack.findString(CONFIRM_PASSWORD_KEY));
		for (String key : errors.keySet()) {
			validatorContext.addFieldError(key, getText(errors.get(key), validatorContext.getLocale()));
		}
	}

	private Map<String, String> validate2(User user, String language, String dialect, ContractorAccount contractor,
			String countrySubdivision, LanguageModel languageModel, String confirmPassword) {
		errors = new HashMap<String, String>();

		user = (user == null) ? new User() : user;

		errors.putAll(validateContractor(language, dialect, contractor, countrySubdivision, languageModel,
				inputValidator, vatValidator));
		validateUser(user, confirmPassword);

		return errors;
	}

	public static Map<String, String> validateContractor(String language, String dialect, ContractorAccount contractor,
			String countrySubdivision, LanguageModel languageModel, InputValidator inputValidator,
			VATValidator vatValidator) {
		Map<String, String> errors = new HashMap<String, String>();
		contractor = (contractor == null) ? new ContractorAccount() : contractor;
		String errorMessageKey = inputValidator.validateCompanyName(contractor.getName());
		errors.put("contractor.name", errorMessageKey);

		Country country = contractor.getCountry();
		String countryIso = Strings.EMPTY_STRING;
		if (country != null) {
			countryIso = country.getIsoCode();
		}

		if (Strings.isEmpty(countrySubdivision)) {
			if ((country != null && country.isHasCountrySubdivisions())
					|| Country.COUNTRIES_WITH_SUBDIVISIONS.contains(countryIso)) {
				errors.put("countrySubdivision", InputValidator.REQUIRED_KEY);
			}
		}

		if (!inputValidator.isLanguageValid(language, languageModel)) {
			language = Locale.ENGLISH.getLanguage();
		}

		if (isDialectInvalid(language, dialect, languageModel, inputValidator)) {
			errors.put("contractor.dialect", InputValidator.REQUIRED_KEY);
		}

		errorMessageKey = inputValidator.validateTimezone(contractor.getTimezone());
		errors.put("contractor.timezone", errorMessageKey);

		errorMessageKey = inputValidator.validateName(countryIso);
		errors.put("contractor.country.isoCode", errorMessageKey);

		errorMessageKey = inputValidator.validateName(contractor.getAddress());
		errors.put("contractor.address", errorMessageKey);

		errorMessageKey = inputValidator.validateName(contractor.getCity());
		errors.put("contractor.city", errorMessageKey);

		if (shouldRequireZipCode(contractor.getCountry())) {
			errorMessageKey = inputValidator.validateName(contractor.getZip());
			errors.put("contractor.zip", errorMessageKey);
		}

		if (!isValidVAT(contractor.getVatId(), contractor.getCountry(), vatValidator, inputValidator)) {
			errors.put("contractor.vatId", InputValidator.INVALID_VAT_ID_KEY);
		}

		removeEmptyValues(errors);
		return errors;
	}

	private static boolean isDialectInvalid(String language, String dialect, LanguageModel languageModel, InputValidator inputValidator) {
		if (!languageModel.getDialectCountriesBasedOn(language).isEmpty()) {

			return StringUtils.isEmpty(dialect) || !inputValidator.containsOnlySafeCharacters(dialect);
		}

		return !StringUtils.isEmpty(dialect);
	}

	private static <K> Map<K, String> removeEmptyValues(Map<K, String> map) {
		for (Iterator<K> iterator = map.keySet().iterator(); iterator.hasNext();) {
			K k = iterator.next();
			if (StringUtils.isEmpty(map.get(k))) {
				iterator.remove();
			}
		}

		return map;
	}

	private void validateUser(User user, String confirmPassword) {
		String errorMessageKey;
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

		Vector<String> passwordErrors = passwordValidator.validatePassword(user, user.getPassword());
		if (CollectionUtils.isNotEmpty(passwordErrors)) {
			addFieldErrorIfMessage("user.password", passwordErrors.get(0));
		}

		if (!StringUtils.equals(user.getPassword(), confirmPassword)) {
			addFieldErrorIfMessage(CONFIRM_PASSWORD_KEY, InputValidator.PASSWORDS_MUST_MATCH_KEY);
		}
	}

	private static boolean isValidVAT(String vat, Country country, VATValidator vatValidator,
			InputValidator inputValidator) {
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

	private static boolean shouldRequireZipCode(Country country) {
		if (country == null) {
			return false;
		}

		// Carryover from existing XML validation
		if (StringUtils.equals(Country.UAE_ISO_CODE, country.getIsoCode())) {
			return false;
		}

		return true;
	}

	private void addFieldErrorIfMessage(String fieldName, String errorMessageKey) {
		if (StringUtils.isNotEmpty(errorMessageKey)) {
			errors.put(fieldName, errorMessageKey);
		}
	}

	private String getText(String key, Locale locale) {
		return getI18nCache().getText(key, locale);
	}

	// the purpose of this is for testing
	private I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}

}
