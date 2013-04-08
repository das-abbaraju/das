package com.picsauditing.model.l10n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pschlesinger
 * Date: 4/8/13
 * Time: 9:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocaleUtil {
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
