package com.picsauditing.model.i18n;

import java.util.Locale;

import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public final class TranslatableString {
    private static final String COMMAND_KEY = "TranslatableString";
	private String key;
    private TranslationService translationService;

	public TranslatableString(String key) {
		this.key = key;
	}

	public final String toTranslatedString() {
        return toTranslatedString(TranslationServiceFactory.getLocale());
	}

	public final String toTranslatedString(Locale locale) {
        return translationService().getText(key, locale);
	}

	@Override
	public final String toString() {
		return toTranslatedString();
	}

    private TranslationService translationService() {
        if (translationService == null) {
            translationService = TranslationServiceFactory.getTranslationService(COMMAND_KEY);
        }
        return translationService;
    }
}