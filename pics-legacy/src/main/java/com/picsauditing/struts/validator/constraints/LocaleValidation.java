package com.picsauditing.struts.validator.constraints;

import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class LocaleValidation implements ConstraintValidator<ValidateLocale, RegistrationLocaleForm> {

    @Autowired
    private LanguageModel languageModel;

    private List<KeyValue<String, String>> availableLanguages;

    @Override
    public void initialize(ValidateLocale constraintAnnotation) {
        availableLanguages = languageModel.getVisibleLanguagesSansDialect();
    }

    @Override
    public boolean isValid(RegistrationLocaleForm value, ConstraintValidatorContext context) {
       for (KeyValue<String, String> key : availableLanguages) {
           if (key.getKey().equals(value.getLanguage())) return true;
       }
       return false;
    }
}
