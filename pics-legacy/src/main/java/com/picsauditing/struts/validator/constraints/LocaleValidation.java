package com.picsauditing.struts.validator.constraints;

import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Set;

public class LocaleValidation implements ConstraintValidator<ValidateDialect, RegistrationLocaleForm> {

    @Autowired
    private LanguageModel languageModel;

    private Set<Locale> availableLanguages;

    @Override
    public void initialize(ValidateDialect constraintAnnotation) {
        availableLanguages = languageModel.getUnifiedLanguageList();
    }

    @Override
    public boolean isValid(RegistrationLocaleForm value, ConstraintValidatorContext context) {
        final String language = value.getLanguage();
        final String dialect = value.getDialect();
        final Locale locale = value.getLocale();

        if (Strings.isEmpty(language)) return false;
        if (languageModel.getCountriesBasedOn(language).size() > 0 && Strings.isEmpty(dialect)) return false;

        return availableLanguages.contains(locale);

    }
}
