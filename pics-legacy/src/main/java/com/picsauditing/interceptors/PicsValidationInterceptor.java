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
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.validator.Validator;

public class PicsValidationInterceptor extends JSONValidationInterceptor {

	// These are request parameters that would be present if the
	private static final String VALIDATE_ONLY_PARAM = "struts.validateOnly";
	private static final String VALIDATE_JSON_PARAM = "struts.enableJSONValidation";
	private static final String NO_ENCODING_SET_PARAM = "struts.JSONValidation.no.encoding";

	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final String TRUE_STRING = Boolean.TRUE.toString();

	private static final long serialVersionUID = 8116134323066887470L;

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();

		if (isPicsCustomValidation(request, invocation.getAction())) {
			ValidatorContext validatorContext = doValidation(invocation);
			return buildJsonResponse(invocation, validatorContext);
		} else if (isOldJsonValidation(request)) {
			return super.doIntercept(invocation);
		}

		return invocation.invoke();
	}

	private String buildJsonResponse(ActionInvocation invocation, ValidatorContext validatorContext) throws IOException {
		HttpServletResponse response = ServletActionContext.getResponse();
		setResponseHeaders(response);

		if (validatorContext.hasErrors()) {
			response.getWriter().print(super.buildResponse((ValidationAware) invocation.getAction()));
		} else {
			response.getWriter().print("{}");
		}

		return Action.NONE;
	}

	private void setResponseHeaders(HttpServletResponse response) {
		response.setContentType("application/json");
		response.setCharacterEncoding(DEFAULT_ENCODING);
	}

	private boolean isPicsCustomValidation(HttpServletRequest request, Object action) {
		return (action instanceof AjaxValidator) && isOldJsonValidation(request) && AjaxUtils.isAjax(request);
	}

	private ValidatorContext doValidation(ActionInvocation invocation) {
		Object action = invocation.getAction();
		AjaxValidator ajaxValidatorAction = (AjaxValidator) action;
		ValidatorContext validatorContext = new DelegatingValidatorContext(action);

		Validator customValidator = ajaxValidatorAction.getCustomValidator();
		customValidator.validate(invocation.getStack(), validatorContext);

		return validatorContext;
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
