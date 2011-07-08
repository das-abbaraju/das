package com.picsauditing.interceptors;

import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.picsauditing.access.Permissions;

@SuppressWarnings("serial")
public class I18nInterceptor extends com.opensymphony.xwork2.interceptor.I18nInterceptor {

	private final String COOKIE_NAME = "locale";

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String result;

		Permissions permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
		Locale cookieLocale = getLocaleFromCookie();
		if (permissions != null && permissions.isLoggedIn()) {
			Locale locale = permissions.getLocale();
			saveLocale(invocation, locale);
			result = invocation.invoke();
			if (!locale.equals(cookieLocale))
				setLocaleToCookie(locale);
		} else if (cookieLocale != null) {
			saveLocale(invocation, cookieLocale);
			result = invocation.invoke();
		} else {
			result = super.intercept(invocation);
		}

		return result;
	}

	private Locale getLocaleFromCookie() {
		Cookie[] cookies = ServletActionContext.getRequest().getCookies();
		if (cookies == null)
			return null;

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(COOKIE_NAME)) {
				String[] values = cookie.getValue().split("_");
				if (values.length == 1)
					return new Locale(values[0]);
				else if (values.length == 2)
					return new Locale(values[0], values[1]);
				else
					return new Locale(values[0], values[1], values[2]);
			}
		}
		return null;
	}

	private void setLocaleToCookie(Locale locale) {
		Cookie cookie = new Cookie(COOKIE_NAME, locale.toString());
		cookie.setMaxAge(3600 * 24 * 365); // One Year
		ServletActionContext.getResponse().addCookie(cookie);
	}
}
