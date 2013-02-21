package com.picsauditing.interceptors;

import java.lang.reflect.Method;

import com.picsauditing.jpa.entities.YesNo;
import org.apache.struts2.ServletActionContext;

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

    public static final Boolean KEEP_LOOKING = null;

    @Override
	public String intercept(ActionInvocation invocation) throws Exception {
		maintainSessionCookieForValidityAndExpiration(invocation);
		checkMethodLevelSecurity(invocation);
		return invocation.invoke();
	}

	private void checkMethodLevelSecurity(ActionInvocation invocation) throws Exception {
		if (invocation.getAction() instanceof SecurityAware) {
			// e.g. PicsActionSupport implements SecurityAware
			SecurityAware action = (SecurityAware) invocation.getAction();
			Method method = action.getClass().getMethod(invocation.getProxy().getMethod());

            Exception rejectionReason = checkSecurityAnnotationsLoggingInAsNecessary(action, method);
            if (rejectionReason != null) {
                throw rejectionReason;
            }
        }
	}

    protected Exception checkSecurityAnnotationsLoggingInAsNecessary(SecurityAware action, Method method) {
        Exception rejectionReason = null;
        Boolean invokable = KEEP_LOOKING;
        boolean apiCall = method.isAnnotationPresent(Api.class);
        boolean anonymousAllowed = method.isAnnotationPresent(Anonymous.class);
        boolean requiresPermissions = method.isAnnotationPresent(RequiredPermission.class);

        if (anonymousAllowed && requiresPermissions) {
            invokable = Boolean.FALSE;
            rejectionReason = new SecurityException("Invalid combination of security annotations defined. An Action cannot be" +
                    " both AnonymousAllowed and RequiresPermissions");
        }

        if (invokable == KEEP_LOOKING && apiCall) {
            try {
                // isApiUser() attempts a "soft" login of the user associated with the API key
                if (action.isApiUser()) {
                    invokable = Boolean.TRUE;
                } else {
                    rejectionReason = new AjaxNotLoggedInException();
                }
            } catch (AjaxNotLoggedInException e) {
                rejectionReason = e;
            }
        }
        if (invokable == KEEP_LOOKING && anonymousAllowed) {
            invokable = Boolean.TRUE;
        }

        if (invokable == KEEP_LOOKING) {
            // Note: anonymousAllowed will always be false here, but isLoggedIn() expects the parameter and
            // "anonymousAllowed" is a better explainer than "False".
            boolean loginSuccessful = action.isLoggedIn(anonymousAllowed);
            if (!loginSuccessful) {
                invokable = Boolean.FALSE;
                if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
                    rejectionReason = new AjaxNotLoggedInException();
                } else {
                    rejectionReason = new NotLoggedInException();
                }
            }
        }

        if (invokable == KEEP_LOOKING && requiresPermissions) {
            try {
                RequiredPermission requiredPermission = method.getAnnotation(RequiredPermission.class);
                action.tryPermissions(requiredPermission.value(), requiredPermission.type());
            } catch (NoRightsException e) {
                invokable = Boolean.FALSE;
                rejectionReason = e;
            }
        }

        if (invokable == Boolean.TRUE) {
            rejectionReason = null;
        }
        return rejectionReason;
    }

    private void maintainSessionCookieForValidityAndExpiration(ActionInvocation invocation) throws Exception {
		if (invocation.getAction() instanceof SecurityAware && !(invocation.getAction() instanceof LoginController)) {
			SecurityAware action = (SecurityAware) invocation.getAction();
			Method method = action.getClass().getMethod(invocation.getProxy().getMethod());
			if (!method.isAnnotationPresent(Anonymous.class)) {
				if (action.sessionCookieIsValidAndNotExpired()) {
					action.updateClientSessionCookieExpiresTime();
				} else {
					action.clearPermissionsSessionAndCookie();
				}
			}
		}
	}
}
