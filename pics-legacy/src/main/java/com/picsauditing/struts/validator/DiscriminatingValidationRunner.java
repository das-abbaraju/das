package com.picsauditing.struts.validator;

import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.service.i18n.TranslationServiceFactory;

import javax.validation.ConstraintViolation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class DiscriminatingValidationRunner {

    private final ValidatorContext errorOutput;
    private final Locale errorLanguage;
    private final Set<String> requestParameters;
    private final javax.validation.Validator validator;

    DiscriminatingValidationRunner(ValidatorContext context, Locale locale, Set<String> requestParams, javax.validation.Validator validator) {
        this.errorOutput = context;
        this.errorLanguage = locale;
        this.requestParameters = requestParams;
        this.validator = validator;
    }

    <T> void processWholeObject(T validatable) {
        processErrors(checkByObject(validatable));

    }

    <T> void processByField(String formParameterHeading, T validatable) {
        processErrors(checkByField(formParameterHeading, validatable));

    }

    private <T> Map<String, Set<ConstraintViolation<T>>> checkByField(String formParameter, T validatable) {
        final Map<String, Set<ConstraintViolation<T>>> violations = new HashMap<>();
        for (String param: requestParameters) {
            if (param.startsWith(formParameter)) {
                final String formEntry = param.substring(param.indexOf(".") + 1);
                violations.put(param, validator.validateProperty(validatable, formEntry));
            }
        }
        return violations;
    }

    private <T> Map<String, Set<ConstraintViolation<T>>> checkByObject(T validatable) {
        final Map<String, Set<ConstraintViolation<T>>> violations = new HashMap<>(1);
        violations.put("violations", validator.validate(validatable));
        return violations;
    }

    private <T> void processErrors(Map<String, Set<ConstraintViolation<T>>> errors) {
        for (String key : errors.keySet()) {
            for (ConstraintViolation violation : errors.get(key)) {
                String message = violation.getMessage();
                if (message.contains("::")) {
                    String[] splitMessage = message.split("::");
                    errorOutput.addFieldError(splitMessage[0], getText(splitMessage[1], errorLanguage));
                } else {
                    errorOutput.addFieldError(key, getText(message, errorLanguage));
                }
            }
        }
    }

    private String getText(String key, Locale locale) {
        return TranslationServiceFactory.getTranslationService().getText(key, locale);
    }
}
