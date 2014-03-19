package com.picsauditing.struts.validator.constraints;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageValidationConstraint implements ConstraintValidator<ValidateLanguage, RegistrationLocaleForm> {

    @Autowired
    private LanguageModel languageModel;

    @Override
    public void initialize(ValidateLanguage constraintAnnotation) {  }

    @Override
    public boolean isValid(RegistrationLocaleForm value, ConstraintValidatorContext context) {
        final String language = value.getLanguage();
        if (Strings.isEmpty(language)) return false;

        for (Language lang : languageModel.getVisibleLanguages()) {
            if (lang.getLanguage().equals(language)) return true;
        }
        return false;
    }
}
