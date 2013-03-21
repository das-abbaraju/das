package com.picsauditing.interceptors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.json.JSONValidationInterceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.Validator;

public class PicsValidationInterceptor extends JSONValidationInterceptor {

	// These are request parameters that would be present if the
	private static final String VALIDATE_ONLY_PARAM = "struts.validateOnly";
    private static final String VALIDATE_JSON_PARAM = "struts.enableJSONValidation";
    private static final String NO_ENCODING_SET_PARAM = "struts.JSONValidation.no.encoding";

    private static final String TRUE_STRING = Boolean.TRUE.toString();

	private static final long serialVersionUID = 8116134323066887470L;

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();

		if (isOldJsonValidation(request)) {
			return super.doIntercept(invocation);
		} else if (isPicsCustomValidation(invocation.getAction())) {
			String jsonResponse = doValidation(invocation);
			if (Strings.isEmpty(jsonResponse)) {
				response.getWriter().print("{}");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				return Action.NONE;
			}
		}

		return invocation.invoke();
	}

	private void setValidationResponse() throws IOException {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.getWriter().print("{}");
		response.setContentType("application/json");
	}

	private boolean isPicsCustomValidation(Object action) {
		return action instanceof AjaxValidator;
	}

	private String doValidation(ActionInvocation invocation) {
		Object action = invocation.getAction();
		AjaxValidator ajaxValidatorAction = (AjaxValidator) action;
		ValidationAware validationAware = (ValidationAware) action;
		ValidatorContext validatorConext = new DelegatingValidatorContext(action);
		Validator customValidator = ajaxValidatorAction.getCustomValidator();
		customValidator.setAction(ajaxValidatorAction);
		customValidator.setValidatorContext(validatorConext);
		customValidator.validate();

		HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
		if (validatorConext.hasActionErrors()) {
			return super.buildResponse(validationAware);
		}

		return Strings.EMPTY_STRING;
	}

	private boolean isOldJsonValidation(HttpServletRequest request) {
		return isJsonEnabled(request) || isValidateOnly(request) || isSetEncoding(request);
	}

    private boolean isJsonEnabled(HttpServletRequest request) {
        return TRUE_STRING.equals(request.getParameter(VALIDATE_JSON_PARAM));
    }

    private boolean isValidateOnly(HttpServletRequest request) {
        return TRUE_STRING.equals(request.getParameter(VALIDATE_ONLY_PARAM));
    }

    private boolean isSetEncoding(HttpServletRequest request) {
        return TRUE_STRING.equals(request.getParameter(NO_ENCODING_SET_PARAM));
    }
}
