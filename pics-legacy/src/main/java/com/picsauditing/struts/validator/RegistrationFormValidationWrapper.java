package com.picsauditing.struts.validator;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

import com.picsauditing.struts.controller.Registration;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.validator.Validator;
import org.apache.commons.collections.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RegistrationFormValidationWrapper implements Validator {

    private final javax.validation.Validator validator;
    private final RegistrationForm regform;
    private final RegistrationLocaleForm localeForm;

    public RegistrationFormValidationWrapper(Registration regInstance, javax.validation.Validator validator) {
        this.regform = regInstance.getRegistrationForm();
        this.localeForm = regInstance.getLocaleForm();
        this.validator = validator;
    }

    public RegistrationFormValidationWrapper(RegistrationForm regform, RegistrationLocaleForm localeForm, javax.validation.Validator validator) {
        this.regform = regform;
        this.localeForm = localeForm;
        this.validator = validator;
    }

    @Override
    public void validate(ValueStack valueStack, ValidatorContext validatorContext) {
        assert(validatorContext != null);

        final HttpServletRequest request = (HttpServletRequest) valueStack.getContext().get(HTTP_REQUEST);
        if (onlyRequestingLanguage(request)) return;

        @SuppressWarnings("unchecked") // The docs say this is what's returned...
        final Set<String> params = request.getParameterMap().keySet();

        validate(valueStack, validatorContext, params);
    }

    public void validate(ValueStack valueStack, ValidatorContext validatorContext, Set<String> params) {
        assert(validatorContext != null);

        final HttpServletRequest request = (HttpServletRequest) valueStack.getContext().get(HTTP_REQUEST);
        if (onlyRequestingLanguage(request)) return;

        final Locale locale = validatorContext.getLocale();

        runValidation(validatorContext, params, locale);
    }

    private void runValidation(ValidatorContext validatorContext, Set<String> params, Locale locale) {
        final DiscriminatingValidationRunner processor = new DiscriminatingValidationRunner(validatorContext, locale, params, validator);
        processor.processByField("registrationForm", regform);
        processor.processByField("localeForm", localeForm);
        processor.processWholeObject(regform.getPasswordPair());
        processor.processWholeObject(regform.getVATPairing());
        processor.processWholeObject(regform.getCountrySubdivisionPairing());
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

}
