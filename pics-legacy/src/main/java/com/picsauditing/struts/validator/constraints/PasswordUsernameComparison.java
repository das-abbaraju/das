package com.picsauditing.struts.validator.constraints;

import com.picsauditing.struts.controller.forms.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordUsernameComparison implements ConstraintValidator<PasswordNotSameAsUserName, RegistrationForm.PasswordPair> {

    @Override
    public void initialize(PasswordNotSameAsUserName constraintAnnotation) {

    }

    @Override
    public boolean isValid(RegistrationForm.PasswordPair value, ConstraintValidatorContext context) {
        return !value.getFirstPassword().equals(value.getUsername());
    }
}
