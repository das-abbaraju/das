package com.picsauditing.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;

public class LocaleController {

	public static boolean isLocaleSupported(Locale userLocale) {
		for (Locale locale : TranslationActionSupport.getSupportedLocales()) {
			if (StringUtils.startsWith(userLocale.toString(), locale.getLanguage()))
				return true;
		}

		return false;
	}

	public static Locale getNearestSupportedLocale(Locale locale) {
		if (isLocaleSupported(locale)) {
			return locale;
		}
		return Locale.ENGLISH;
	}

	public static Locale setLocaleOfNearestSupported(Permissions permissions) {
		permissions.setLocale(getNearestSupportedLocale(permissions.getLocale()));
		return permissions.getLocale();
	}
}
