package com.picsauditing.employeeguard.validators.profile;

import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.util.Strings;

public class ProfileValidationUtil {

    public static final String REGEX_ALL_LETTERS = ".*[a-zA-Z].*";
    public static final String REGEX_ALL_NUMBERS = ".*[0-9].*";

	public enum ProfileField {
		FIRST_NAME,
        LAST_NAME,
        EMAIL,
        EMAIL_RETYPE,
        PASSWORD,
        PASSWORD_MATCHES_USERNAME,
        PASSWORD_HAS_INVALID_CHARACTERS
	}

	public static boolean valid(ProfileForm profileForm, ProfileField field) {
		switch (field) {
			case FIRST_NAME:
				return validateFirstName(profileForm);

			case LAST_NAME:
				return validateLastName(profileForm);

			case EMAIL:
				return validateEmail(profileForm);

			case EMAIL_RETYPE:
				return validateEmailRetype(profileForm);

            case PASSWORD:
                return validatePassword(profileForm);

			case PASSWORD_MATCHES_USERNAME:
				return validatePasswordDoesNotMatchUsername(profileForm);

            case PASSWORD_HAS_INVALID_CHARACTERS:
                return validatePasswordContainsValidCharacters(profileForm);

			default:
				throw new IllegalArgumentException("You have not set up validation for that field: " + field);
		}
	}

    private static boolean validateFirstName(ProfileForm profileForm) {
		return Strings.isNotEmpty(profileForm.getFirstName());
	}

	private static boolean validateLastName(ProfileForm profileForm) {
		return Strings.isNotEmpty(profileForm.getLastName());
	}

	private static boolean validateEmail(ProfileForm profileForm) {
		return Strings.isNotEmpty(profileForm.getEmail());
	}

	private static boolean validateEmailRetype(ProfileForm profileForm) {
		return Strings.isNotEmpty(profileForm.getEmail())
				&& Strings.isNotEmpty(profileForm.getEmailRetype())
				&& profileForm.getEmail().equals(profileForm.getEmailRetype());
	}

	private static boolean validatePassword(ProfileForm profileForm) {
        return Strings.isNotEmpty(profileForm.getPassword());
	}

    private static boolean validatePasswordDoesNotMatchUsername(ProfileForm profileForm) {
        return !profileForm.getEmail().equalsIgnoreCase(profileForm.getPassword());
    }

    private static boolean validatePasswordContainsValidCharacters(ProfileForm profileForm) {
        String password = profileForm.getPassword();

        return password.matches(REGEX_ALL_LETTERS) && password.matches(REGEX_ALL_NUMBERS);
    }
}
