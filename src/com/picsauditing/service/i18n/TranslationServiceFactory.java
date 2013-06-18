package com.picsauditing.service.i18n;

import java.util.Locale;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.model.i18n.ThreadLocalLocale;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.spun.util.ObjectUtils;
import com.spun.util.persistence.Loader;

public class TranslationServiceFactory {

	private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;

	// for testing
	private static TranslationService translationService;

	public static TranslationService getTranslationService() {
		if (translationService != null) {
			return translationService;
		}

		if (useTranslationServiceAdapter()) {
			return TranslationServiceAdapter.getInstance();
		}

		return I18nCache.getInstance();
	}

	private static boolean useTranslationServiceAdapter() {
		FeatureToggle featureToggle = SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
		return featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER);
	}

	public static void registerTranslationService(TranslationService translationService) {
		TranslationServiceFactory.translationService = translationService;
	}

	public static Locale getLocale() {
		try {
			return localeProvider.load();
		} catch (Exception e) {
			throw ObjectUtils.throwAsError(e);
		}
	}

}
