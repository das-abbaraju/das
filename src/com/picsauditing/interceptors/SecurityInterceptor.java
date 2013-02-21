package com.picsauditing.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.*;
import com.picsauditing.strutsutil.AjaxUtils;
import org.apache.struts2.ServletActionContext;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class SecurityInterceptor extends AbstractInterceptor {

    public static final Boolean KEEP_LOOKING = null;

    @Override
	public String intercept(ActionInvocation invocation) throws Exception {
		maintainSessionCookieForValidityAndExpiration(invocation);
		checkMethodLevelSecurity(invocation);
		return invocation.invoke();
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
        CheckSecurityState securityState = new CheckSecurityState(action, method);

                validateAllowedCombinationOfSecurityAnnotations(securityState);
        checkValidUserOfApiMethod(securityState);
        checkMethodAllowsAnonymousUse(securityState);
        checkUserAbleToLogin(securityState);
        checkUserHasRequiredPermissionIfAnyRequired(securityState);

        if (securityState.invokable == Boolean.TRUE) {
            securityState.rejectionReason = null;
        }
        return securityState.rejectionReason;
    }

    private void validateAllowedCombinationOfSecurityAnnotations(CheckSecurityState securityState) {
        boolean anonymousAllowed = securityState.method.isAnnotationPresent(Anonymous.class);
        boolean requiresPermissions = securityState.method.isAnnotationPresent(RequiredPermission.class);

        if (anonymousAllowed && requiresPermissions) {
            securityState.invokable = Boolean.FALSE;
            securityState.rejectionReason = new SecurityException("Invalid combination of security annotations defined. An Action cannot be" +
                    " both AnonymousAllowed and RequiresPermissions");
        }
    }

    private void checkValidUserOfApiMethod(CheckSecurityState securityState) {
        boolean apiAllowed = securityState.method.isAnnotationPresent(Api.class);
        if (securityState.invokable == KEEP_LOOKING && apiAllowed) {
            try {
                // isApiUser() attempts a "soft" login of the user associated with the API key
                if (securityState.action.isApiUser()) {
                    securityState.invokable = Boolean.TRUE;
                } else {
                    securityState.rejectionReason = new AjaxNotLoggedInException();
                }
            } catch (AjaxNotLoggedInException e) {
                securityState.rejectionReason = e;
            }
        }
    }

    private void checkMethodAllowsAnonymousUse(CheckSecurityState securityState) {
        boolean anonymousAllowed = securityState.method.isAnnotationPresent(Anonymous.class);
        if (securityState.invokable == KEEP_LOOKING && anonymousAllowed) {
            securityState.invokable = Boolean.TRUE;
        }
    }

    private void checkUserAbleToLogin(CheckSecurityState securityState) {
        if (securityState.invokable == KEEP_LOOKING) {
            boolean anonymousAllowed = securityState.method.isAnnotationPresent(Anonymous.class);
            // Note: anonymousAllowed will always be false here, but isLoggedIn() expects the parameter and
            // "anonymousAllowed" is a better explainer than "False".
            boolean loginSuccessful = securityState.action.isLoggedIn(anonymousAllowed);
            if (!loginSuccessful) {
                securityState.invokable = Boolean.FALSE;
                if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
                    securityState.rejectionReason = new AjaxNotLoggedInException();
                } else {
                    securityState.rejectionReason = new NotLoggedInException();
                }
            }
        }
    }

    private void checkUserHasRequiredPermissionIfAnyRequired(CheckSecurityState securityState) {
        boolean requiresPermissions = securityState.method.isAnnotationPresent(RequiredPermission.class);
        if (securityState.invokable == KEEP_LOOKING && requiresPermissions) {
            try {
                RequiredPermission requiredPermission = securityState.method.getAnnotation(RequiredPermission.class);
                securityState.action.tryPermissions(requiredPermission.value(), requiredPermission.type());
                securityState.invokable = Boolean.TRUE;
            } catch (NoRightsException e) {
                securityState.invokable = Boolean.FALSE;
                securityState.rejectionReason = e;
            }
        }

    }

    private class CheckSecurityState {
        Boolean invokable = KEEP_LOOKING;
        Exception rejectionReason = null;
        Method method = null;
        SecurityAware action = null;

        public CheckSecurityState(SecurityAware action, Method method) {
            this.action = action;
            this.method = method;
        }
    }
}
