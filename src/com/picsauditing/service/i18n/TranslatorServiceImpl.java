package com.picsauditing.service.i18n;

import java.util.Locale;

import com.picsauditing.PICS.I18nCache;

public class TranslatorServiceImpl implements TranslatorService {

	private static I18nCache i18nCache;

	@Override
	public String translate(String key, Locale locale) {
		return getI18nCache().getText(key, locale);
	}

	private static I18nCache getI18nCache() {
		if (i18nCache == null) {
			return I18nCache.getInstance();
		}

		return i18nCache;
	}

}
