package com.picsauditing.service.i18n;

public class TranslationProviderFactory {

	private static TranslationProvider translationProvider;

	public static TranslationProvider getTranslationProvider() {
		return translationProvider;
	}

	public static TranslationProvider registerTranslationProvider(TranslationProvider translationProvider) {
		TranslationProviderFactory.translationProvider = translationProvider;
		return translationProvider;
	}

}
