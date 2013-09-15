/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Originally found at http://glindholm.wordpress.com/2008/07/02/preserving-messages-across-a-redirect-in-struts-2/
 */
package com.picsauditing.interceptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.dispatcher.ServletRedirectResult;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.picsauditing.strutsutil.AdvancedValidationAware;

/**
 * An Interceptor to preserve an actions ValidationAware messages and their
 * headers across a redirect result.
 *
 * It makes the assumption that you always want to preserve messages and their
 * headers across a redirect and restore them to the next action if they exist.
 *
 * The way this works is it looks at the result type after a action has executed
 * and if the result was a redirect (ServletRedirectResult) or a redirectAction
 * (ServletActionRedirectResult) and there were any errors, messages, or
 * fieldErrors they are stored in the session. Before the next action executes
 * it will check if there are any messages stored in the session and add them to
 * the next action.
 */
public class RedirectMessageInterceptor extends MethodFilterInterceptor {

	private static final long serialVersionUID = -1847557437429753540L;

	public static final String FIELD_ERRORS_KEY = "RedirectMessageInterceptor_FieldErrors";

	public static final String ACTION_ERRORS_KEY = "RedirectMessageInterceptor_ActionErrors";
	public static final String ACTION_ERROR_HEADER_KEY = "RedirectMessageInterceptor_ActionErrorHeader";

	public static final String ACTION_MESSAGES_KEY = "RedirectMessageInterceptor_ActionMessages";
	public static final String ACTION_MESSAGE_HEADER_KEY = "RedirectMessageInterceptor_ActionMessageHeader";

	public static final String ALERT_MESSAGES_KEY = "RedirectMessageInterceptor_AlertMessages";
	public static final String ALERT_MESSAGE_HEADER_KEY = "RedirectMessageInterceptor_AlertMessageHeader";

	public RedirectMessageInterceptor() {
	}

	public String doIntercept(ActionInvocation invocation) throws Exception {
		Object action = invocation.getAction();

		if (action instanceof AdvancedValidationAware) {
			restoreFromSession(invocation, (AdvancedValidationAware) action);
		}

		String result = invocation.invoke();

		if (action instanceof AdvancedValidationAware) {
			persistToSession(invocation, (AdvancedValidationAware) action);
		}

		return result;
	}

	/**
	 * Retrieve the errors and messages from the session and add them to the
	 * action.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void restoreFromSession(ActionInvocation invocation, AdvancedValidationAware validationAware) throws Exception {
		Map<String, ?> session = invocation.getInvocationContext().getSession();

		Collection<String> actionErrors = (Collection) session.remove(ACTION_ERRORS_KEY);
		if (CollectionUtils.isNotEmpty(actionErrors)) {
			for (String error : actionErrors) {
				validationAware.addActionError(error);
			}
		}

		Collection<String> actionMessages = (Collection) session.remove(ACTION_MESSAGES_KEY);
		if (CollectionUtils.isNotEmpty(actionMessages)) {
			for (String message : actionMessages) {
				validationAware.addActionMessage(message);
			}
		}

		Map<String, List<String>> fieldErrors = (Map) session.remove(FIELD_ERRORS_KEY);
		if (MapUtils.isNotEmpty(fieldErrors)) {
			for (Map.Entry<String, List<String>> fieldError : fieldErrors.entrySet()) {
				for (String message : fieldError.getValue()) {
					validationAware.addFieldError(fieldError.getKey(), message);
				}
			}
		}

		Collection<String> alertMessages = (Collection) session.remove(ALERT_MESSAGES_KEY);
		if (CollectionUtils.isNotEmpty(alertMessages)) {
			for (String message : alertMessages) {
				validationAware.addAlertMessage(message);
			}
		}

		String actionErrorHeader = (String) session.remove(ACTION_ERROR_HEADER_KEY);
		if (StringUtils.isNotEmpty(actionErrorHeader)) {
			validationAware.setActionErrorHeader(actionErrorHeader);
		}

		String actionMessageHeader = (String) session.remove(ACTION_MESSAGE_HEADER_KEY);
		if (StringUtils.isNotEmpty(actionMessageHeader)) {
			validationAware.setActionMessageHeader(actionMessageHeader);
		}

		String alertMessageHeader = (String) session.remove(ALERT_MESSAGE_HEADER_KEY);
		if (StringUtils.isNotEmpty(alertMessageHeader)) {
			validationAware.setAlertMessageHeader(alertMessageHeader);
		}
	}

	/**
	 * If the result is a redirect then store error and messages in the session.
	 */
	protected void persistToSession(ActionInvocation invocation, AdvancedValidationAware validationAware) throws Exception {
		Result result = invocation.getResult();
		if (result == null)
			return;

		if (result instanceof ServletRedirectResult || result instanceof ServletActionRedirectResult) {
			Map<String, Object> session = invocation.getInvocationContext().getSession();

			Collection<String> actionErrors = validationAware.getActionErrors();
			if (CollectionUtils.isNotEmpty(actionErrors)) {
				session.put(ACTION_ERRORS_KEY, actionErrors);
			}

			Collection<String> actionMessages = validationAware.getActionMessages();
			if (CollectionUtils.isNotEmpty(actionMessages)) {
				session.put(ACTION_MESSAGES_KEY, actionMessages);
			}

			Map<String, List<String>> fieldErrors = validationAware.getFieldErrors();
			if (MapUtils.isNotEmpty(fieldErrors)) {
				session.put(FIELD_ERRORS_KEY, fieldErrors);
			}

			Collection<String> alertMessages = validationAware.getAlertMessages();
			if (CollectionUtils.isNotEmpty(alertMessages)) {
				session.put(ALERT_MESSAGES_KEY, alertMessages);
			}

			String actionErrorHeader = validationAware.getActionErrorHeader();
			if (StringUtils.isNotEmpty(actionErrorHeader)) {
				session.put(ACTION_ERROR_HEADER_KEY, actionErrorHeader);
			}

			String actionMessageHeader = validationAware.getActionMessageHeader();
			if (StringUtils.isNotEmpty(actionMessageHeader)) {
				session.put(ACTION_MESSAGE_HEADER_KEY, actionMessageHeader);
			}

			String alertMessageHeader = validationAware.getAlertMessageHeader();
			if (StringUtils.isNotEmpty(alertMessageHeader)) {
				session.put(ALERT_MESSAGE_HEADER_KEY, alertMessageHeader);
			}
		}
	}
}