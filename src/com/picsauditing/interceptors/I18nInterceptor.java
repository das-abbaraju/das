package com.picsauditing.interceptors;

import java.util.Locale;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.Permissions;

@SuppressWarnings("serial")
public class I18nInterceptor extends com.opensymphony.xwork2.interceptor.I18nInterceptor {

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		Permissions permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");

		String result;

		if (permissions != null) {
			Locale locale = permissions.getLocale();
			saveLocale(invocation, locale);
			result = invocation.invoke();
		} else {
			result = super.intercept(invocation);
		}

		return result;
	}

}
