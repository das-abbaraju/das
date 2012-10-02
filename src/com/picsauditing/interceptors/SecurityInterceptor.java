package com.picsauditing.interceptors;

import java.lang.reflect.Method;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.AjaxNotLoggedInException;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.Api;
import com.picsauditing.access.LoginController;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.NotLoggedInException;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.access.SecurityAware;
import com.picsauditing.strutsutil.AjaxUtils;

@SuppressWarnings("serial")
public class SecurityInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		checkMethodLevelSecurity(invocation);
		String securityResult = checkSessionCookieForValidityAndExpiration(invocation);
		if (!Action.SUCCESS.equals(securityResult)) {
			return securityResult;
		}
		return invocation.invoke();
	}

	private void checkMethodLevelSecurity(ActionInvocation invocation) throws NoSuchMethodException,
			AjaxNotLoggedInException, NotLoggedInException, NoRightsException {
		if (invocation.getAction() instanceof SecurityAware) {
			// e.g. PicsActionSupport implements SecurityAware
			SecurityAware action = (SecurityAware) invocation.getAction();
			Method method = action.getClass().getMethod(invocation.getProxy().getMethod());

			boolean apiCall = method.isAnnotationPresent(Api.class);
			if (apiCall) {
				if (!action.isApiUser()) {
					throw new AjaxNotLoggedInException();
				}
			} else {
				boolean anonymousAllowed = method.isAnnotationPresent(Anonymous.class);
				if (!action.isLoggedIn(anonymousAllowed)) {
					if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
						throw new AjaxNotLoggedInException();
					} else {
						throw new NotLoggedInException();
					}
				}
			}

			boolean requiresPermissions = method.isAnnotationPresent(RequiredPermission.class);
			if (requiresPermissions) {
				RequiredPermission requiredPermission = action.getClass().getMethod(invocation.getProxy().getMethod())
						.getAnnotation(RequiredPermission.class);
				action.tryPermissions(requiredPermission.value(), requiredPermission.type());
			}

		}
	}

	private String checkSessionCookieForValidityAndExpiration(ActionInvocation invocation) throws Exception {
		String result = Action.SUCCESS;
		if (invocation.getAction() instanceof SecurityAware && !(invocation.getAction() instanceof LoginController)) {
			SecurityAware action = (SecurityAware) invocation.getAction();
			Method method = action.getClass().getMethod(invocation.getProxy().getMethod());
			if (!method.isAnnotationPresent(Anonymous.class)) {
				if (action.sessionCookieIsValidAndNotExpired()) {
					action.updateClientSessionCookieExpiresTime();
				} else {
					result = action.logoutAndRedirectToLogin();
				}
			}
		}
		return result;
	}
}
