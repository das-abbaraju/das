package com.picsauditing.interceptors;

import java.lang.reflect.Method;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.AjaxNotLoggedInException;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.NotLoggedInException;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.access.SecurityAware;
import com.picsauditing.strutsutil.AjaxUtils;

@SuppressWarnings("serial")
public class SecurityInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		if (invocation.getAction() instanceof SecurityAware) {
			// e.g. PicsActionSupport implements SecurityAware
			SecurityAware action = (SecurityAware) invocation.getAction();
			Method method = action.getClass().getMethod(invocation.getProxy().getMethod());

			boolean anonymous = method.isAnnotationPresent(Anonymous.class);
			if (!action.isLoggedIn(anonymous)) {

				if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
					throw new AjaxNotLoggedInException();
				} else {
					throw new NotLoggedInException();
				}
			}

			boolean requiresPermissions = method.isAnnotationPresent(RequiredPermission.class);
			if (requiresPermissions) {
				RequiredPermission requiredPermission = action.getClass().getMethod(invocation.getProxy().getMethod())
						.getAnnotation(RequiredPermission.class);
				action.tryPermissions(requiredPermission.value(), requiredPermission.type());
			}

		}

		return invocation.invoke();
	}
}
