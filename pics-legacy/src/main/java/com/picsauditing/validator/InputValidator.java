package com.picsauditing.validator;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.picsauditing.authentication.dao.AppUserDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.util.DataScrubber;

public class InputValidator {

	@Autowired
	private UserDAO userDao;
	@Autowired
	private AppUserDAO appUserDAO;

	public InputValidator setUserDao(UserDAO userDao) {
		this.userDao = userDao;
		return this;
	}

	public InputValidator setContractorAccountDao(ContractorAccountDAO contractorAccountDao) {
		this.contractorAccountDao = contractorAccountDao;
		return this;
	}

	@Autowired
	private ContractorAccountDAO contractorAccountDao;

	private static final int MIN_STRING_LENGTH_2 = 2;
	private static final int MAX_STRING_LENGTH_50 = 50;
	private static final int MAX_STRING_LENGTH = 100;

	public static final String NO_ERROR = "";
	public static final String REQUIRED_KEY = "JS.Validation.Required";
	public static final String MIN_2_CHARS_KEY = "JS.Validation.Minimum2Characters";
	public static final String MAX_50_CHARS_KEY = "JS.Validation.Maximum50Characters";
	public static final String MAX_100_CHARS_KEY = "JS.Validation.Maximum100Characters";
	public static final String NO_SPECIAL_CHARS_KEY = "JS.Validation.SpecialCharacters";
	public static final String COMPANY_NAME_EXISTS_KEY = "JS.Validation.CompanyNameAlreadyExists";
	public static final String INVALID_VAT_ID_KEY = "JS.Validation.InvalidVAT";
	public static final String INVALID_USERNAME_KEY = "JS.Validation.UsernameInvalid";
	public static final String USERNAME_TAKEN_KEY = "JS.Validation.UsernameIsTaken";
	public static final String INVALID_PHONE_FORMAT_KEY = "JS.Validation.InvalidPhoneFormat";
	public static final String INVALID_EMAIL_FORMAT_KEY = "JS.Validation.ValidEmail";
	public static final String INVALID_DATE_KEY = "AuditData.error.InvalidDate";
	public static final String INVALID_UK_POST_CODE_KEY = "JS.Validation.InvalidPostcode";
	public static final String PASSWORDS_MUST_MATCH_KEY = "JS.Validation.PasswordsMustMatch";

	// (?s) turns on single-line mode, which makes '.' also match line
	// terminators (DOTALL)
	public static final String SPECIAL_CHAR_REGEX = "(?s).*[;<>`\"].*";

	public static final String USERNAME_REGEX = "[\\w+._@-]+";

	// I'd like to keep this simple. The user knows their email address.
	// Our other option is to use this monster:
	// http://www.ex-parrot.com/pdw/Mail-RFC822-Address.html
	public static final String EMAIL_REGEX = ".+@.+\\..+";

	// Allow NANP phones with leadings 1s and potential x1234 extensions as well
	// as international
	// formatted phone numbers. Modified from
	// http://blog.stevenlevithan.com/archives/validate-phone-number
	// and shown in the Regular Expressions Cookbook (O'Reilly, 2009)
	public static final String PHONE_NUMBER_REGEX = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}((?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";
	public static final String PHONE_NUMBER_REGEX_WITH_ASTERISK = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}(\\*|(?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";
	public static final String PHONE_NUMBER_PICS_CSR_EXTENTION = "^[0-9]{4}$";

	public static final String REGEX_FOR_MULTIPLE_SPACES = "\\s+";

	// Regex is from http://webarchive.nationalarchives.gov.uk/+/http://www.cabinetoffice.gov.uk/media/291370/bs7666-v2-0-xsd-PostCodeType.htm and some additions for other territories
	public static final String UK_POST_CODE_REGEX = "(GIR 0AA|STHL 1ZZ|ASCN 1ZZ|AI-2640|TDCU 1ZZ|BBND 1ZZ|BIQQ 1ZZ|FIQQ 1ZZ|GX11 1AA|PCRN 1ZZ|SIQQ 1ZZ|TKCA 1ZZ)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})";

	public enum ValidationType {
		Required,
		MinLengthCheck,
		MaxLengthCheck,
		DangerousCharsCheck,
		UsernameCheck,
		EmailFormatCheck,
		PhoneFormatCheck,
		UKPostcodeCheck,
		UKPostcodeScrub,
		PicsCSRPhoneFormat,
	}

	public boolean isUsernameTaken(String username, int currentUserId) {
		int appUserID = userDao.findAppUserID(currentUserId);
		return appUserDAO.duplicateUsername(username, appUserID);
	}

	public boolean isCompanyNameTaken(String companyName) {
		List<ContractorAccount> accounts = contractorAccountDao.findByCompanyName(companyName);

		if (CollectionUtils.isNotEmpty(accounts)) {
			return true;
		}

		return false;
	}

	public boolean containsOnlySafeCharacters(String str) {
		if (str == null) {
			return false;
		}

		if (StringUtils.isEmpty(str)) {
			return true;
		}

		if (str.matches(SPECIAL_CHAR_REGEX)) {
			return false;
		}

		return true;
	}

	/** Use validateUsername() instead */
	@Deprecated
	public boolean isUsernameValid(String username) {
		if (StringUtils.isEmpty(username)) {
			return false;
		}

		if (!username.matches(USERNAME_REGEX)) {
			return false;
		}

		if (username.length() > MAX_STRING_LENGTH) {
			return false;
		}

		return true;
	}

	public String validateUsernameAvailable(String username, int currentUserId) {
		if (StringUtils.isEmpty(username)) {
			return NO_ERROR;
		}

		if (isUsernameTaken(username, currentUserId)) {
			return USERNAME_TAKEN_KEY;
		}

		return NO_ERROR;
	}

	public String validateDate(Date date) {
		return validateDate(date, true);
	}

	@SuppressWarnings("deprecation")
	public String validateDate(Date date, boolean required) {
		if (date == null) {
			if (required) {
				return REQUIRED_KEY;
			}

			return NO_ERROR;
		}

		int yearsSince1900 = date.getYear();
		if (yearsSince1900 < (1000 - 1900) || yearsSince1900 > (9999 - 1900)) {
			return INVALID_DATE_KEY;
		}

		int zeroIndexedMonth = date.getMonth();
		if (zeroIndexedMonth < 0 || zeroIndexedMonth > 11) {
			return INVALID_DATE_KEY;
		}

		int oneIndexedDayOfMonth = date.getDate();
		if (oneIndexedDayOfMonth < 1 || oneIndexedDayOfMonth > 31) {
			return INVALID_DATE_KEY;
		}

		return NO_ERROR;
	}

	public boolean isLanguageValid(String language, LanguageModel supportedLanguages) {
		if (StringUtils.isEmpty(language)) {
			return false;
		}

		if (supportedLanguages == null) {
			return false;
		}

		List<KeyValue<String, String>> stableLanguages = supportedLanguages.getVisibleLanguagesSansDialect();

		if (stableLanguages == null) {
			return false;
		}

		for (KeyValue<String, String> stableLanguage : stableLanguages) {
			if (StringUtils.equals(stableLanguage.getKey(), language)) {
				return true;
			}
		}

		return false;
	}

	public String validateLocale(Locale locale) {
		return validateLocale(locale, true);
	}

	public String validateLocale(Locale locale, boolean required) {
		if (locale == null) {
			if (required) {
				return REQUIRED_KEY;
			}

			return NO_ERROR;
		}

		if (StringUtils.isEmpty(locale.toString())) {
			return REQUIRED_KEY;
		}

		return NO_ERROR;
	}

	public String validateName(String name) {
		return validateName(name, true);
	}

	public String validateName(String name, boolean required) {
		return validateString(name, ValidationType.MaxLengthCheck, ValidationType.DangerousCharsCheck,
				getRequiredValidationType(required));
	}

	// TODO: Cleanup
	public String validateFirstName(String name) {
		if (StringUtils.isEmpty(name)) {
			return REQUIRED_KEY;
		}

		if (name.length() > MAX_STRING_LENGTH_50) {
			return MAX_50_CHARS_KEY;
		}

		if (!containsOnlySafeCharacters(name)) {
			return NO_SPECIAL_CHARS_KEY;
		}

		return NO_ERROR;
	}

	public String validateLastName(String name) {
		if (StringUtils.isEmpty(name)) {
			return REQUIRED_KEY;
		}

		if (name.length() > MAX_STRING_LENGTH_50) {
			return MAX_50_CHARS_KEY;
		}

		if (!containsOnlySafeCharacters(name)) {
			return NO_SPECIAL_CHARS_KEY;
		}

		return NO_ERROR;
	}

	public String validateTimezone(TimeZone timezone) {
		if (timezone == null) {
			return REQUIRED_KEY;
		}

		return NO_ERROR;
	}

	public String validateCompanyName(String name) {
		return validateCompanyName(name, true);
	}

	public String validateCompanyName(String name, boolean required) {
		if (isCompanyNameTaken(name)) {
			return COMPANY_NAME_EXISTS_KEY;
		}

		return validateString(name, ValidationType.MaxLengthCheck, ValidationType.MinLengthCheck,
				ValidationType.DangerousCharsCheck, getRequiredValidationType(required));
	}

	public String validateUsername(String username) {
		return validateUsername(username, true);
	}

	public String validateUsername(String username, boolean required) {
		return validateString(username, ValidationType.UsernameCheck, ValidationType.MaxLengthCheck,
				ValidationType.DangerousCharsCheck, getRequiredValidationType(required));
	}

	public String validateEmail(String email) {
		return validateEmail(email, true);
	}

	public String validateEmail(String email, boolean required) {
		return validateString(email, ValidationType.EmailFormatCheck, getRequiredValidationType(required));
	}

	public String validatePhoneNumber(String phoneNumber) {
		return validatePhoneNumber(phoneNumber, true);
	}

	public String validatePhoneNumber(String phoneNumber, boolean required) {
		return validateString(phoneNumber, ValidationType.PhoneFormatCheck, getRequiredValidationType(required));
	}

	public String validatePicsCSRPhoneNumber(String phoneNumber) {
		return validatePicsCSRPhoneNumber(phoneNumber, true);
	}

	public String validatePicsCSRPhoneNumber(String phoneNumber, boolean required) {
		return validateString(phoneNumber, ValidationType.PicsCSRPhoneFormat, getRequiredValidationType(required));
	}

	public String validateUkPostcode(String postcode, boolean required, boolean scrubData) {
		ValidationType scrubPostcode = scrubData ? ValidationType.UKPostcodeScrub : null;
		return validateString(postcode, ValidationType.UKPostcodeCheck,  getRequiredValidationType(required), scrubPostcode);
	}

	private ValidationType getRequiredValidationType(boolean required) {
		return required ? ValidationType.Required : null;
	}

	private String validateString(String str, ValidationType... validationType) {
		Set<ValidationType> validationTypes = new HashSet<>(Arrays.asList(validationType));

		if (StringUtils.isEmpty(str)) {
			if (validationTypes.contains(ValidationType.Required)) {
				return REQUIRED_KEY;
			}

			return NO_ERROR; // FIXME possible error here
		}

		if (validationTypes.contains(ValidationType.UKPostcodeScrub)) {
			str = DataScrubber.cleanUKPostcode(str).toUpperCase();
		}

		if (validationTypes.contains(ValidationType.MinLengthCheck) && str.length() < MIN_STRING_LENGTH_2) {
			return MIN_2_CHARS_KEY;
		}

		if (validationTypes.contains(ValidationType.MaxLengthCheck) && str.length() > MAX_STRING_LENGTH) {
			return MAX_100_CHARS_KEY;
		}

		if (validationTypes.contains(ValidationType.DangerousCharsCheck) && !containsOnlySafeCharacters(str)) {
			return NO_SPECIAL_CHARS_KEY;
		}

		if (validationTypes.contains(ValidationType.UsernameCheck) && !str.matches(USERNAME_REGEX)) {
			return INVALID_USERNAME_KEY;
		}

		if (validationTypes.contains(ValidationType.EmailFormatCheck) && !str.matches(EMAIL_REGEX)) {
			return INVALID_EMAIL_FORMAT_KEY;
		}

		if (validationTypes.contains(ValidationType.PhoneFormatCheck) && !str.matches(PHONE_NUMBER_REGEX_WITH_ASTERISK)) {
			return INVALID_PHONE_FORMAT_KEY;
		}

		if (validationTypes.contains(ValidationType.UKPostcodeCheck) && !str.matches(UK_POST_CODE_REGEX)) {
			return INVALID_UK_POST_CODE_KEY;
		}

		if (validationTypes.contains(ValidationType.PicsCSRPhoneFormat) && !str.matches(PHONE_NUMBER_REGEX_WITH_ASTERISK)
				&& !str.matches(PHONE_NUMBER_PICS_CSR_EXTENTION)) {
			return INVALID_PHONE_FORMAT_KEY;
		}

		return NO_ERROR;
	}

}
