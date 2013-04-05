package com.picsauditing.model.l10n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocaleUtil {

	public static final List<Locale> SUPPORTED_EUROPEAN_FRENCH_LOCALES = new ArrayList<Locale>() {{
		add(Locale.FRANCE);
		add(new Locale("fr", "BE"));
		add(new Locale("fr", "CH"));
		add(new Locale("fr", "LU"));
	}};

	public static final List<Locale> SUPPORTED_GLOBAL_FRENCH_LOCALES = new ArrayList<Locale>() {{
		addAll(SUPPORTED_EUROPEAN_FRENCH_LOCALES);
		add(Locale.CANADA_FRENCH);
	}};

	public static final Boolean isASupportedEuropeanFrenchLocale(Locale locale) {
		if (SUPPORTED_EUROPEAN_FRENCH_LOCALES.contains(locale)) {
			return true;
		} else {
			return false;
		}
	}

	public static final Boolean isASupportedGlobalFrenchLocale(Locale locale) {
		if (SUPPORTED_GLOBAL_FRENCH_LOCALES.contains(locale)) {
			return true;
		} else {
			return false;
		}
	}
}
