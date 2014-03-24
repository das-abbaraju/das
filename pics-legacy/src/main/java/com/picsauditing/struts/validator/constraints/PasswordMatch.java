package com.picsauditing.struts.validator.constraints;

import com.picsauditing.struts.controller.forms.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatch implements ConstraintValidator<PasswordsMatch, RegistrationForm.PasswordPair> {
    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {

    }

    @Override
    public boolean isValid(RegistrationForm.PasswordPair value, ConstraintValidatorContext context) {
        if (value.getFirstPassword() == null || value.getSecondPassword() == null)
            return true;
        else
            return value.getFirstPassword().equals(value.getSecondPassword());
    }
}