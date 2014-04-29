package com.picsauditing.employeeguard.validators.profile;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.validator.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileFormValidator extends AbstractBasicValidator<ProfileForm> {

	public static final String PROFILE_FORM = "profileForm";

	@Autowired
	private AppUserService appUserService;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private InputValidator inputValidator;

	@Override
	protected ProfileForm getFormFromValueStack(ValueStack valueStack) {
		return (ProfileForm) valueStack.findValue(PROFILE_FORM, ProfileForm.class);
	}

	@Override
	protected void doFormValidation(ProfileForm profileForm) {
		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.FIRST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "firstName"), "First name is missing");
		}

		if (!ProfileValidationUtil.valid(profileForm, ProfileValidationUtil.ProfileField.LAST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "lastName"), "Last name is missing");
		}

		if (!profileForm.isTos()) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "tos"), "You must agree to the Terms of Service to Signup for EmployeeGUARD.");
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

		if (!InputValidator.NO_ERROR.equals(inputValidator.validateEmail(profileForm.getEmail()))) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), "Invalid email format");
			emailValidationFails = true;
		}

//		if (!emailValidationFails && !appUserService.isUserNameAvailable(profileForm.getEmail())) {
//			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), "Email is already used");
//		}

		if (!emailValidationFails && authenticationService.isDuplicateUserName(profileForm.getEmail())) {
			addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_FORM, "email"), "Email is already used");
		}
	}
}
