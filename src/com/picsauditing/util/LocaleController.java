package com.picsauditing.util;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.access.Permissions;

public class LocaleController {

	public static boolean isLocaleSupported(Permissions permissions) {
		if (StringUtils.startsWith(permissions.getLocale().toString(), "en")
				|| StringUtils.startsWith(permissions.getLocale().toString(), "fr")
				|| StringUtils.startsWith(permissions.getLocale().toString(), "es")
				|| StringUtils.startsWith(permissions.getLocale().toString(), "de"))
			return true;

		return false;
	}

	public static Locale setLocaleOfNearestSupported(Permissions permissions) {
		Locale locale = permissions.getLocale();
		if (!isLocaleSupported(permissions)) {
			locale = Locale.ENGLISH;
			permissions.setLocale(locale);
		}
		
		return locale;
	}
}
