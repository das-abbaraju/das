package com.picsauditing.model.i18n.translation.strategy;

import java.util.Locale;

import com.picsauditing.service.i18n.TranslationServiceFactory;

public class DefaultTranslationStrategy implements TranslationStrategy {

	@Override
	public String performTranslation(String key, Locale locale) {
		return TranslationServiceFactory.getTranslationService().getText(key, locale);
	}

}
