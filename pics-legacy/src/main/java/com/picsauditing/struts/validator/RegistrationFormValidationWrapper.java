package com.picsauditing.struts.validator;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.struts.controller.Registration;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.validator.Validator;
import org.apache.commons.collections.MapUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class RegistrationFormValidationWrapper implements Validator {

    private final javax.validation.Validator validator;
    private final RegistrationForm regform;

    public RegistrationFormValidationWrapper(Registration regInstance, javax.validation.Validator validator) {
        this.regform = regInstance.getRegistrationForm();
        this.validator = validator;
    }

    @Override
    public void validate(ValueStack valueStack, final ValidatorContext validatorContext) {
        assert(validatorContext != null);

        final HttpServletRequest request = (HttpServletRequest) valueStack.getContext().get(HTTP_REQUEST);
        if (onlyRequestingLanguage(request)) return;

        @SuppressWarnings("unchecked") // The docs say this is what's returned...
        final Set<String> params = request.getParameterMap().keySet();
        final Locale locale = validatorContext.getLocale();

        for (final String param : params) {
            if (param.startsWith("registrationForm.")) {
                final String[] formInput = param.split("\\.");
                if (formInput.length < 2) continue;
                validator.validateProperty(regform, formInput[1]).forEach(new Consumer<ConstraintViolation<RegistrationForm>>() {
                    @Override
                    public void accept(ConstraintViolation<RegistrationForm> violation) {
                        validatorContext.addFieldError(param, getText(violation.getMessage(), locale));
                    }
                });
            }
        }
    }

    private boolean onlyRequestingLanguage(HttpServletRequest request) {
        Map parameterMap;
        return  request != null
                && AjaxUtils.isAjax(request)
                && MapUtils.isNotEmpty(parameterMap = request.getParameterMap())
                && parameterMap.size() == 1
                && parameterMap.containsKey("language")
                ;
    }

    private String getText(String key, Locale locale) {
        return TranslationServiceFactory.getTranslationService().getText(key, locale);
    }
}
