package com.picsauditing.interceptors;

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
			SecurityAware action = (SecurityAware) invocation.getAction();
			boolean anonymous = action.getClass().getMethod(invocation.getProxy().getMethod())
					.isAnnotationPresent(Anonymous.class);
			if (!action.isLoggedIn(anonymous)) {

				if (AjaxUtils.isAjax(ServletActionContext.getRequest())) {
					throw new AjaxNotLoggedInException();
				} else {
					throw new NotLoggedInException();
				}
			}

			boolean requiresPermissions = action.getClass().getMethod(invocation.getProxy().getMethod())
					.isAnnotationPresent(RequiredPermission.class);
			if (requiresPermissions) {
				RequiredPermission requiredPermission = action.getClass().getMethod(invocation.getProxy().getMethod())
						.getAnnotation(RequiredPermission.class);
				action.tryPermissions(requiredPermission.value(), requiredPermission.type());
			}
		}

		return invocation.invoke();
	}
}
