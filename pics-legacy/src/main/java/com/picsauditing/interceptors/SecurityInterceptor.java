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
        checkMethodAllowsAnonymousUse(securityState);
        checkValidUserOfApiMethod(securityState);
        checkUserAbleToLogin(securityState);
        checkUserHasRequiredPermissionIfAnyRequired(securityState);
        securityState.resolveFindings();
        return securityState.rejectionReason;
    }

    private void validateAllowedCombinationOfSecurityAnnotations(CheckSecurityState securityState) {
        if (anonymousInInvalidCombination(securityState)) {
            securityState.invokable = Boolean.FALSE;
            securityState.rejectionReason = new SecurityException("Invalid combination of security annotations defined. An Action cannot be" +
                    " both Anonymous and either API or RequiresPermissions or ApiAllowed.");
        }
    }

    private boolean anonymousInInvalidCombination(CheckSecurityState securityState) {
        return securityState.anonymousAllowed &&
                (securityState.requiresPermissions || securityState.requiresApi || securityState.apiAllowed);
    }

    private void checkValidUserOfApiMethod(CheckSecurityState securityState) {
        if (securityState.invokable == KEEP_LOOKING && (securityState.requiresApi || securityState.apiAllowed)) {
            try {
                // isApiUser() attempts a "soft" login of the user associated with the API key
                if (securityState.action.isApiUser() && securityState.apiAllowed) {
                    securityState.invokable = Boolean.TRUE;
                } else if (!securityState.action.isApiUser()) {
                    if (securityState.requiresApi) {
                        securityState.invokable = Boolean.FALSE;
                    }
                    determineNotLoggedInException(securityState);
                }
            } catch (AjaxNotLoggedInException e) {
                securityState.rejectionReason = e;
            }
        }
    }

    private void checkMethodAllowsAnonymousUse(CheckSecurityState securityState) {
        if (securityState.invokable == KEEP_LOOKING && securityState.anonymousAllowed) {
            securityState.invokable = Boolean.TRUE;
        }
    }

    private void checkUserAbleToLogin(CheckSecurityState securityState) {
        if (securityState.invokable == KEEP_LOOKING) {
            // Note: securityState.anonymousAllowed will always be false here, but isLoggedIn() expects the parameter and
            // "anonymousAllowed" is a better explainer than "False".
            boolean loginSuccessful = securityState.action.isLoggedIn(securityState.anonymousAllowed);
            if (!loginSuccessful) {
                securityState.invokable = Boolean.FALSE;
                if (securityState.rejectionReason == null) {
                    determineNotLoggedInException(securityState);
                }
            }
        }
    }

    private void determineNotLoggedInException(CheckSecurityState securityState) {
        if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
            securityState.rejectionReason = new AjaxNotLoggedInException();
        } else {
            securityState.rejectionReason = new NotLoggedInException();
        }
    }

    private void checkUserHasRequiredPermissionIfAnyRequired(CheckSecurityState securityState) {
        if (securityState.invokable == KEEP_LOOKING &&
                (securityState.requiresPermissions || securityState.apiAllowed)) {
            try {
                AccessPermission requiredPermission = requiredPermission(securityState);
                securityState.action.tryPermissions(requiredPermission.getOpPerm(), requiredPermission.getOpType());
                securityState.invokable = Boolean.TRUE;
            } catch (NoRightsException e) {
                securityState.invokable = Boolean.FALSE;
                securityState.rejectionReason = e;
            }
        }

    }

    private AccessPermission requiredPermission(CheckSecurityState securityState) {
        if (securityState.requiresPermissions) {
            RequiredPermission permission =  securityState.method.getAnnotation(RequiredPermission.class);
            return new AccessPermission(permission.value(), permission.type());
        }
        return new AccessPermission();
    }

    private class CheckSecurityState {
        public Boolean invokable = KEEP_LOOKING;
        public Exception rejectionReason = null;
        public Method method = null;
        public SecurityAware action = null;
        public boolean anonymousAllowed;
        public boolean requiresPermissions;
        public boolean apiAllowed;
        public boolean requiresApi;

        public CheckSecurityState(SecurityAware action, Method method) {
            this.action = action;
            this.method = method;
            anonymousAllowed = method.isAnnotationPresent(Anonymous.class);
            requiresPermissions = method.isAnnotationPresent(RequiredPermission.class);
            apiAllowed = method.isAnnotationPresent(ApiAllowed.class);
            requiresApi = method.isAnnotationPresent(ApiRequired.class);
        }

        public void resolveFindings() {
            if (invokable == Boolean.TRUE) {
                rejectionReason = null;
            }
        }

    }
}
