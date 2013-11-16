package com.picsauditing.service.i18n;

import java.util.*;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.jdbc.JdbcFeatureToggleProvider;
import com.picsauditing.i18n.service.TranslationServiceProperties;
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
            return new TranslationServiceAdapter(new TranslationKeyDoNothingLogger());
        }

        return I18nCache.getInstance();
    }

    public static TranslationService getTranslationService() {
        if (translationService != null) {
            return translationService;
        }

        if (useTranslationServiceAdapter()) {
            if (logTranslationUsage()) {
                return new TranslationServiceAdapter(new TranslationKeyAggregateUsageLogger());
            } else {
                return new TranslationServiceAdapter(new TranslationKeyDoNothingLogger());
            }
        }

        return I18nCache.getInstance();
    }

	public static com.picsauditing.i18n.service.TranslationServiceAdapter getTranslationService2() {
        ActionUsageContext context = new ActionUsageContext();
        TranslationServiceProperties.Builder propertyBuilder = new TranslationServiceProperties.Builder().context(context);

        if (logTranslationUsage()) {
            propertyBuilder.translationUsageLogger(new TranslationKeyAggregateUsageLogger());
        }
        return new com.picsauditing.i18n.service.TranslationServiceAdapter(propertyBuilder.build());
	}

    // if we have to parameterize the command group name for more than TranslateCommand, this will have to be a more
    // sophisticated configuration object. For now, though, let's stay simple
    public static TranslationService getTranslationService(String translateCommandKey) {
        if (translationService != null) {
            return translationService;
        }

        if (useTranslationServiceAdapter()) {
            if (logTranslationUsage()) {
                return new TranslationServiceAdapter(new TranslationKeyAggregateUsageLogger(), translateCommandKey);
            } else {
                return new TranslationServiceAdapter(new TranslationKeyDoNothingLogger(), translateCommandKey);
            }
        }

        return I18nCache.getInstance();
    }

    private static boolean useTranslationServiceAdapter() {
		return featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER);
	}

    private static boolean logTranslationUsage() {
        // note that this is a negative toggle
        return !featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_DISABLE_LOG_TRANSLATION_USAGE);
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
