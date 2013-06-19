package com.picsauditing.model.i18n;

import com.picsauditing.service.i18n.TranslationServiceFactory;

import java.util.Locale;

public class TranslatableString {

	private String key;

	public TranslatableString(String key) {
		this.key = key;
	}

	public String toTranslatedString() {
		return toTranslatedString(TranslationServiceFactory.getLocale());
	}

    public String toTranslatedString(Locale locale) {
        return TranslationServiceFactory.getTranslationService().getText(key, locale);
    }

    @Override
	public String toString() {
		return key;
	}

}