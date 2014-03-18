package com.picsauditing.struts.validator.constraints;

import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocaleValidation implements ConstraintValidator<ValidateLocale, RegistrationLocaleForm> {

    @Autowired
    private LanguageModel languageModel;

    @Override
    public void initialize(ValidateLocale constraintAnnotation) {

    }

    @Override
    public boolean isValid(RegistrationLocaleForm value, ConstraintValidatorContext context) {

    }
}
