package com.picsauditing.model.i18n;

import java.util.Locale;

public class DefaultLocaleProvider implements LocaleProvider {

	@Override
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

}
