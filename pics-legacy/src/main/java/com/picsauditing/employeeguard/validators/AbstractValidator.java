package com.picsauditing.employeeguard.validators;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.validator.Validator;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

public abstract class AbstractValidator<T> implements Validator {

	protected HttpServletRequest request;
	protected Map<String, String> errors = new HashMap<>();

	@Override
	public void validate(ValueStack valueStack, ValidatorContext validatorContext) {
		if (validatorContext == null) {
			throw new IllegalStateException("You must set the ValidatorContext to use this validator.");
		}

		request = getRequest(valueStack);
		if (validationNotApplicableToRequestMethod(request)) {
			return;
		}

		T form = getFormFromValueStack(valueStack);

		if (form == null) {
			return;
		}

		performValidation(form);
		addErrorsToValidatorContext(validatorContext);
	}

	protected abstract void performValidation(T form);

    // TODO: Use a constant for "POST" and "PUT"
	protected boolean validationNotApplicableToRequestMethod(HttpServletRequest request) {
		return request != null && !request.getMethod().equals("POST") && !request.getMethod().equals("PUT");
	}

    // TODO: Use a constant for "POST"
	protected boolean isPost() {
		return "POST".equals(request.getMethod());
	}

	protected HttpServletRequest getRequest(ValueStack valueStack) {
		return (HttpServletRequest) valueStack.getContext().get(HTTP_REQUEST);
	}

	protected void addErrorsToValidatorContext(ValidatorContext validatorContext) {
		for (String errorKey : errors.keySet()) {
			validatorContext.addFieldError(errorKey, errors.get(errorKey));
		}
	}

	protected void addFieldErrorIfMessage(String fieldName, String errorMessageKey) {
		if (StringUtils.isNotEmpty(errorMessageKey)) {
			errors.put(fieldName, errorMessageKey);
		}
	}

	protected String fieldKeyBuilder(String formName, String field) {
		return formName + "." + field;
	}

	protected abstract T getFormFromValueStack(ValueStack valueStack);
}
