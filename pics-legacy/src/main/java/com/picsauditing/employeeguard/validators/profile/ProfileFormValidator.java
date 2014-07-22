package com.picsauditing.employeeguard.validators.profile;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.validator.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileFormValidator extends AbstractBasicValidator<ProfileForm> {

	public static final String PROFILE_FORM = "profileForm";

	@Autowired
	private AppUserService appUserService;
	@Autowired
	private InputValidator inputValidator;

	@Override
	protected ProfileForm getFormFromValueStack(ValueStack valueStack) {
		return (ProfileForm) valueStack.findValue(PROFILE_FORM, ProfileForm.class);
	}

	@Override
	protected void doFormValidation(ProfileForm profileForm) {
		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.FIRST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "firstName"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.FIRST_NAME"));
		}

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.LAST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "lastName"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.LAST_NAME"));
		}

		if (!profileForm.isTos()) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "tos"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.TERMS_OF_SERVICE"));
		}

		performValidationOnEmail(profileForm);

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.PASSWORD)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "password"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.PASSWORD"));
		}
	}

	private void performValidationOnEmail(ProfileForm profileForm) {
		boolean emailValidationFails = false;

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.EMAIL)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMAIL"));
			emailValidationFails = true;
		}

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.EMAIL_RETYPE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "emailRetype"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.REENTER_EMAIL"));
			emailValidationFails = true;
		}

		if (!InputValidator.NO_ERROR.equals(inputValidator.validateEmail(profileForm.getEmail()))) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), EGI18n.getTextFromResourceBundle("VALIDATION.INVALID.EMAIL"));
			emailValidationFails = true;
		}

		if (!emailValidationFails && !appUserService.isUserNameAvailable(profileForm.getEmail())) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), EGI18n.getTextFromResourceBundle("VALIDATION.EMAIL.ALREADY_USED"));
		}
	}
}
