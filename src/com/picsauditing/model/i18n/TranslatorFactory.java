package com.picsauditing.model.i18n;

import java.util.Locale;

import com.picsauditing.service.i18n.TranslatorService;
import com.picsauditing.service.i18n.TranslatorServiceImpl;
import com.spun.util.ObjectUtils;
import com.spun.util.persistence.Loader;

public class TranslatorFactory {

	private static TranslatorService translatorService = new TranslatorServiceImpl();
	private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;

	public static TranslatorService getTranslator() {
		return translatorService;
	}

	public static Locale getLocale() {
		try {
			return localeProvider.load();
		} catch (Exception e) {
			throw ObjectUtils.throwAsError(e);
		}
	}

	public static void registerTranslatorService(TranslatorService translator) {
		translatorService = translator;
	}

}
