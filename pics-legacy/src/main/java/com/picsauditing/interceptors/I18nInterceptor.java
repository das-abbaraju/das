package com.picsauditing.interceptors;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.i18n.ThreadLocalLocale;

@SuppressWarnings("serial")
public class I18nInterceptor extends com.opensymphony.xwork2.interceptor.I18nInterceptor {

	private final String COOKIE_NAME = "locale";

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String result;

		Permissions permissions = (Permissions) invocation.getInvocationContext().getSession().get("permissions");

		Locale paramLocale = getLocaleFromParams(invocation);
		Locale cookieLocale = getLocaleFromCookie();
		if (permissions != null && permissions.isLoggedIn()) {
			Locale locale = permissions.getLocale();
			saveLocale(invocation, locale);
			setThreadLocalLocale(locale);
			result = invocation.invoke();
			if (!locale.equals(cookieLocale)) {
				setLocaleToCookie(locale);
			}
		} else if (paramLocale != null) {
			saveLocale(invocation, paramLocale);
			setThreadLocalLocale(paramLocale);
			result = invocation.invoke();
			if (!paramLocale.equals(cookieLocale)) {
				setLocaleToCookie(paramLocale);
			}
		} else if (cookieLocale != null) {
			saveLocale(invocation, cookieLocale);
			setThreadLocalLocale(cookieLocale);
			result = invocation.invoke();
		} else {
			setThreadLocalLocale(LocalizedTextUtil.localeFromString(invocation.getInvocationContext().getLocale()
					.toString(), Locale.ENGLISH));
			result = super.intercept(invocation);
		}

		return result;
	}

	private Locale getLocaleFromParams(ActionInvocation invocation) {
		Map<String, Object> params = invocation.getInvocationContext().getParameters();
		Object requested_locale = params.remove(parameterName);
		if (requested_locale == null) {
			return null;
		}
		if (requested_locale.getClass().isArray()) {
			requested_locale = ((Object[]) requested_locale)[0];
		}

		return LocalizedTextUtil.localeFromString(requested_locale.toString(), Locale.ENGLISH);
	}

	private Locale getLocaleFromCookie() {
		Cookie[] cookies = ServletActionContext.getRequest().getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(COOKIE_NAME)) {
				return LocalizedTextUtil.localeFromString(cookie.getValue(), null);
			}
		}
		return null;
	}

	private void setLocaleToCookie(Locale locale) {
		Cookie cookie = new Cookie(COOKIE_NAME, locale.toString());
		cookie.setMaxAge(3600 * 24 * 365); // One Year
		ServletActionContext.getResponse().addCookie(cookie);
	}

	private void setThreadLocalLocale(Locale locale) {
		ThreadLocalLocale.INSTANCE.set(locale);
	}
}
