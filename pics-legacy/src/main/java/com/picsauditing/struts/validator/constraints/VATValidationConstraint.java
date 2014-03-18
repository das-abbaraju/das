package com.picsauditing.struts.validator.constraints;

import com.picsauditing.struts.controller.forms.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class VATValidationConstraint implements ConstraintValidator<VatValidation, RegistrationForm.VATPair>{
    @Override
    public void initialize(VatValidation constraintAnnotation) {

    }

    @Override
    public boolean isValid(RegistrationForm.VATPair value, ConstraintValidatorContext context) {
        return false;
    }
}
