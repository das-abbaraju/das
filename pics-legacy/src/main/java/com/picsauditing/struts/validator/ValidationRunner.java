package com.picsauditing.struts.validator;

import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.service.i18n.TranslationServiceFactory;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class ValidationRunner {

    private final ValidatorContext errorOutput;
    private final Locale errorLanguage;
    private final Set<String> requestParameters;
    private final javax.validation.Validator validator;

    ValidationRunner(ValidatorContext context, Locale locale, Set<String> requestParams, javax.validation.Validator validator) {
        this.errorOutput = context;
        this.errorLanguage = locale;
        this.requestParameters = requestParams;
        this.validator = validator;
    }

    <T> void process(String formParameterHeading, T validatable) {
        processErrors(checkObject(formParameterHeading, validatable));
    }

    private <T> Map<String, Set<ConstraintViolation<T>>> checkObject(String formParameter, T validatable) {
        Map<String, Set<ConstraintViolation<T>>> violations = new HashMap<>();
        for (String param: requestParameters) {
            if (param.startsWith(formParameter)) {
                final String formEntry = param.substring(param.indexOf(".") + 1);
                violations.put(param, validator.validateProperty(validatable, formEntry));
            }
        }
        return violations;
    }

    private <T> void processErrors(Map<String, Set<ConstraintViolation<T>>> errors) {
        for (String key : errors.keySet()) {
            for (ConstraintViolation violation : errors.get(key)) {
                errorOutput.addFieldError(key, getText(violation.getMessage(), errorLanguage));
            }
        }
    }

    private String getText(String key, Locale locale) {
        return TranslationServiceFactory.getTranslationService().getText(key, locale);
    }
}
