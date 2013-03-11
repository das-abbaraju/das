package com.picsauditing.validator;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InputValidator {

    @Autowired
    private UserDAO userDao;
    @Autowired
    private ContractorAccountDAO contractorAccountDao;

    private static final int MIN_STRING_LENGTH = 2;
    private static final int MAX_STRING_LENGTH = 100;

    public static final String NO_ERROR = "";
    public static final String REQUIRED_KEY = "JS.Validation.Required";
    public static final String MIN_2_CHARS_KEY = "JS.Validation.Minimum2Characters";
    public static final String MAX_100_CHARS_KEY = "JS.Validation.Maximum100Characters";
    public static final String NO_SPECIAL_CHARS_KEY = "JS.Validation.SpecialCharacters";
    public static final String COMPANY_NAME_EXISTS_KEY = "JS.Validation.CompanyNameAlreadyExists";
    public static final String INVALID_VAT_ID_KEY = "JS.Validation.InvalidVAT";
    public static final String INVALID_USERNAME_KEY = "JS.Validation.UsernameInvalid";
    public static final String USERNAME_TAKEN_KEY = "JS.Validation.UsernameIsTaken";
    public static final String INVALID_PHONE_FORMAT_KEY = "JS.Validation.InvalidPhoneFormat";
    public static final String INVALID_EMAIL_FORMAT_KEY = "JS.Validation.ValidEmail";
    public static final String INVALID_DATE_KEY = "AuditData.error.InvalidDate";
    public static final String PASSWORD_CANNOT_BE_USERNAME_KEY = "JS.Validation.CannotBeUsername";
    public static final String PASSWORDS_MUST_MATCH_KEY = "JS.Validation.PasswordsMustMatch";

    // (?s) turns on single-line mode, which makes '.' also match line terminators (DOTALL)
    public static final String SPECIAL_CHAR_REGEX = "(?s).*[;<>`\"].*";

    public static final String USERNAME_REGEX = "[\\w+._@-]+";

    // I'd like to keep this simple. The user knows their email address.
    // Our other option is to use this monster: http://www.ex-parrot.com/pdw/Mail-RFC822-Address.html
    public static final String EMAIL_REGEX = ".+@.+\\..+";

    // Allow NANP phones with leadings 1s and potential x1234 extensions as well as international
    // formatted phone numbers. Modified from http://blog.stevenlevithan.com/archives/validate-phone-number
    // and shown in the Regular Expressions Cookbook (O'Reilly, 2009)
    public static final String PHONE_NUMBER_REGEX = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}((?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";
    public static final String PHONE_NUMBER_REGEX_WITH_ASTERISK = "^(\\+?(?:\\(?[0-9]\\)?[-. ]{0,2}){9,14}[0-9])((\\s){0,4}(\\*|(?i)x|(?i)ext)(\\s){0,4}[\\d]{1,5})?$";

    public boolean isUsernameTaken(String username, int currentUserId) {
        return userDao.duplicateUsername(username, currentUserId);
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
    	return validateString(name, required, false, true, true, false, false, false);
    }

    public String validateCompanyName(String name) {
    	return validateCompanyName(name, true);
    }

    public String validateCompanyName(String name, boolean required) {
		if (isCompanyNameTaken(name)) {
			return COMPANY_NAME_EXISTS_KEY;
		}

    	return validateString(name, required, true, true, true, false, false, false);
    }

    public String validateUsername(String username) {
    	return validateUsername(username, true);
    }

    public String validateUsername(String username, boolean required) {
    	return validateString(username, required, false, true, true, true, false, false);
    }

    public String validateEmail(String email) {
    	return validateEmail(email, true);
    }

    public String validateEmail(String email, boolean required) {
    	return validateString(email, required, false, true, true, false, true, false);
    }

    public String validatePhoneNumber(String phoneNumber) {
    	return validatePhoneNumber(phoneNumber, true);
    }

    public String validatePhoneNumber(String phoneNumber, boolean required) {
    	return validateString(phoneNumber, required, false, true, true, false, false, true);
    }

    private String validateString(String str, boolean required, boolean minLengthCheck, boolean maxLengthCheck,
    		boolean dangerousCharsCheck, boolean usernameCheck, boolean emailFormatCheck, boolean phoneFormatCheck) {

    	if (StringUtils.isEmpty(str)) {
    		if (required) {
    			return REQUIRED_KEY;
    		}

    		return NO_ERROR;
    	}

    	if (minLengthCheck && str.length() < MIN_STRING_LENGTH) {
    		return MIN_2_CHARS_KEY;
    	}

    	if (maxLengthCheck && str.length() > MAX_STRING_LENGTH) {
    		return MAX_100_CHARS_KEY;
    	}

    	if (dangerousCharsCheck && !containsOnlySafeCharacters(str)) {
    		return NO_SPECIAL_CHARS_KEY;
    	}

    	if (usernameCheck && !str.matches(USERNAME_REGEX)) {
    		return INVALID_USERNAME_KEY;
    	}

    	if (emailFormatCheck && !str.matches(EMAIL_REGEX)) {
    		return INVALID_EMAIL_FORMAT_KEY;
    	}

    	if (phoneFormatCheck && !str.matches(PHONE_NUMBER_REGEX_WITH_ASTERISK)) {
    		return INVALID_PHONE_FORMAT_KEY;
    	}

    	return NO_ERROR;
    }

}
