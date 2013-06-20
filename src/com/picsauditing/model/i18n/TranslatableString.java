package com.picsauditing.model.i18n;

import java.util.Locale;

import com.picsauditing.service.i18n.TranslationServiceFactory;

public final class TranslatableString {

	private String key;

	public TranslatableString(String key) {
		this.key = key;
	}

	public final String toTranslatedString() {
		return toTranslatedString(TranslationServiceFactory.getLocale());
	}

	public final String toTranslatedString(Locale locale) {
		return TranslationServiceFactory.getTranslationService().getText(key, locale);
	}

	@Override
	public final String toString() {
		return key;
	}

}