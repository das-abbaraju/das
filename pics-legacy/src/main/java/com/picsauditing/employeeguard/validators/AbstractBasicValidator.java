package com.picsauditing.employeeguard.validators;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.strutsutil.HttpUtil;
import com.picsauditing.validator.Validator;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

public abstract class AbstractBasicValidator<T> implements Validator {

	protected HttpServletRequest request;
	protected Map<String, String> errors = new HashMap<>();

	@Override
	public void validate(final ValueStack valueStack, final ValidatorContext validatorContext) {
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

		doFormValidation(form);
		addErrorsToValidatorContext(validatorContext);
	}

	protected abstract void doFormValidation(final T form);

	protected boolean validationNotApplicableToRequestMethod(final HttpServletRequest request) {
		return request != null && !request.getMethod().equals(HttpUtil.HTTP_POST_METHOD) &&
                !request.getMethod().equals(HttpUtil.HTTP_PUT_METHOD);
	}

	protected boolean isPost() {
		return HttpUtil.HTTP_POST_METHOD.equals(request.getMethod());
	}

	protected HttpServletRequest getRequest(final ValueStack valueStack) {
		return (HttpServletRequest) valueStack.getContext().get(HTTP_REQUEST);
	}

	protected void addErrorsToValidatorContext(final ValidatorContext validatorContext) {
		for (String errorKey : errors.keySet()) {
			validatorContext.addFieldError(errorKey, errors.get(errorKey));
		}
	}

	protected void addFieldErrorIfMessage(final String fieldName, final String errorMessageKey) {
		if (StringUtils.isNotEmpty(errorMessageKey)) {
			errors.put(fieldName, errorMessageKey);
		}
	}

	protected String fieldKeyBuilder(final String formName, final String field) {
		return formName + "." + field;
	}

	protected abstract T getFormFromValueStack(final ValueStack valueStack);
}
