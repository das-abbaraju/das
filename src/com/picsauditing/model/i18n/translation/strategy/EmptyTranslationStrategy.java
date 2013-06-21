package com.picsauditing.model.i18n.translation.strategy;

import java.util.Locale;

import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;

public class EmptyTranslationStrategy implements TranslationStrategy {

	@Override
	public String performTranslation(String key, Locale locale) {
		String translation = TranslationServiceFactory.getTranslationService().getText(key, locale);
		if (Strings.isEmpty(key) || key.equals(translation)) {
			return Strings.EMPTY_STRING;
		}

		return translation;
	}

}
