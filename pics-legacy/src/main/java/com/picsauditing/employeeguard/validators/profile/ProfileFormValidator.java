package com.picsauditing.employeeguard.validators.profile;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileFormValidator extends AbstractValidator<ProfileForm> {
	public static final String PROFILE_FORM = "personalInfo";

	@Autowired
	private AppUserService appUserService;

	@Override
	protected ProfileForm getFormFromValueStack(ValueStack valueStack) {
		return (ProfileForm) valueStack.findValue(PROFILE_FORM, ProfileForm.class);
	}

	@Override
	protected void performValidation(ProfileForm profileForm) {
		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.FIRST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "firstName"), "First name is missing");
		}

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.LAST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "lastName"), "Last name is missing");
		}

		performValidationOnEmail(profileForm);

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.PASSWORD)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "password"), "Password is missing");
		}
	}

	private void performValidationOnEmail(ProfileForm profileForm) {
		boolean emailValidationFails = false;
		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.EMAIL)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), "Email is missing or does not match");
			emailValidationFails = true;
		}

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.EMAIL_RETYPE)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "emailRetype"), "Re-entered email is missing or does not match");
			emailValidationFails = true;
		}

		if (!emailValidationFails && !appUserService.isUserNameAvailable(profileForm.getEmail())) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), "Email is already used");
		}
	}
}