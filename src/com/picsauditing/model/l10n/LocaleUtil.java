package com.picsauditing.model.l10n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleUtil {
	// From: http://www.coderanch.com/t/463412/java/java/Locale-country-code-lang-code
	private static final Map<String, Locale> COUNTRY_TO_LOCALE_MAP = new HashMap<String, Locale>();

	static {
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale l : locales) {
			COUNTRY_TO_LOCALE_MAP.put(l.getCountry(), l);
		}
	}

	public static Locale getLocaleFromCountry(String country) {
		return COUNTRY_TO_LOCALE_MAP.get(country);
	}


}
