package com.picsauditing.model.i18n;

import java.util.Locale;

import com.picsauditing.model.i18n.translation.strategy.DefaultTranslationStrategy;
import com.picsauditing.model.i18n.translation.strategy.TranslationStrategy;
import com.picsauditing.service.i18n.TranslationServiceFactory;

public final class TranslatableString {

	private String key;
	private TranslationStrategy translationStrategy;

	public TranslatableString(String key) {
		this.key = key;
		this.translationStrategy = new DefaultTranslationStrategy();
	}

	public TranslatableString(String key, TranslationStrategy translationStrategy) {
		this.key = key;
		this.translationStrategy = translationStrategy;
	}

	public final String toTranslatedString() {
		return translationStrategy.performTranslation(key, TranslationServiceFactory.getLocale());
	}

	public final String toTranslatedString(Locale locale) {
		return translationStrategy.performTranslation(key, locale);
	}

	@Override
	public final String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Strategy = ").append(translationStrategy.getClass().getSimpleName()).append(" Key = ")
				.append(key);
		return stringBuilder.toString();
	}

}