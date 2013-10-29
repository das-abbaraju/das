package com.picsauditing.service.i18n;

import java.util.Locale;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.jdbc.JdbcAppPropertyProvider;
import com.picsauditing.dao.jdbc.JdbcFeatureToggleProvider;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.model.i18n.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.spun.util.ObjectUtils;
import com.spun.util.persistence.Loader;

public class TranslationServiceFactory {

	private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;
    private static FeatureToggle featureToggleChecker;

	// for testing
	private static TranslationService translationService;
    // for testing
	private static TranslationService nonLoggingTranslationService;

    public static TranslationService getNonLoggingTranslationService() {
        if (nonLoggingTranslationService != null) {
            return nonLoggingTranslationService;
        }

        if (useTranslationServiceAdapter()) {
            return TranslationServiceAdapter.getInstance(new TranslationKeyDoNothingLogger());
        }

        return I18nCache.getInstance();
    }

	public static TranslationService getTranslationService() {
		if (translationService != null) {
			return translationService;
		}

		if (useTranslationServiceAdapter()) {
			return TranslationServiceAdapter.getInstance(new TranslationKeyAggregateUsageLogger());
		}

		return I18nCache.getInstance();
	}

	private static boolean useTranslationServiceAdapter() {
		return featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER);
	}

	public static void registerTranslationService(TranslationService translationService) {
		TranslationServiceFactory.translationService = translationService;
	}

    public static void registerNonLoggingTranslationService(TranslationService translationService) {
        TranslationServiceFactory.nonLoggingTranslationService = translationService;
    }

    public static Locale getLocale() {
		try {
			return localeProvider.load();
		} catch (Exception e) {
			throw ObjectUtils.throwAsError(e);
		}
	}

    private static FeatureToggle featureToggle() {
        if (featureToggleChecker == null) {
            featureToggleChecker = new FeatureToggleCheckerGroovy(new JdbcFeatureToggleProvider(), null);
        }
        return featureToggleChecker;
    }

}
