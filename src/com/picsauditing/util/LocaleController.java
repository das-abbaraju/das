package com.picsauditing.util;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;

public class LocaleController {

	public static boolean isLocaleSupported(Permissions permissions) {
		for (Locale locale : TranslationActionSupport.getSupportedLocales()) {
			if (StringUtils.startsWith(permissions.getLocale().toString(), locale.getLanguage()))
				return true;
		}
		
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
