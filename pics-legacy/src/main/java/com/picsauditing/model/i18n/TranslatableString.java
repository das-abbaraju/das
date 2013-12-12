package com.picsauditing.model.i18n;

import java.util.Locale;

import com.picsauditing.i18n.model.UsageContext;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.i18n.service.TranslationServiceProperties;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public final class TranslatableString {
    private static final String COMMAND_KEY = "TranslatableString";
	private String key;
    private TranslationService translationService;

	public TranslatableString(String key) {
		this.key = key;
        this.translationService = translationService();
	}

    public TranslatableString(String key, UsageContext context) {
        this.key = key;
        this.translationService = translationService(context);
    }

    public final String toTranslatedString() {
        return toTranslatedString(TranslationServiceFactory.getLocale());
	}

	public final String toTranslatedString(Locale locale) {
        return translationService.getText(key, locale);
	}

	@Override
	public final String toString() {
		return toTranslatedString();
	}

    private TranslationService translationService() {
        if (translationService == null) {
            TranslationServiceProperties.Builder propertyBuilder = propertyBuilder();
            translationService = TranslationServiceFactory.getTranslationService(propertyBuilder.build());
        }
        return translationService;
    }

    private TranslationService translationService(UsageContext context) {
        if (translationService == null) {
            TranslationServiceProperties.Builder propertyBuilder = propertyBuilder();
            propertyBuilder.context(context);
            translationService = TranslationServiceFactory.getTranslationService(propertyBuilder.build());
        }
        return translationService;
    }

    private TranslationServiceProperties.Builder propertyBuilder() {
        TranslationServiceProperties.Builder propertyBuilder = TranslationServiceFactory.baseTranslationServiceProperties();
        propertyBuilder.translationCommandKey(COMMAND_KEY);
        return propertyBuilder;
    }
}