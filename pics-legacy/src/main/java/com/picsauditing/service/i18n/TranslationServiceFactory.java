package com.picsauditing.service.i18n;

import java.util.*;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.jdbc.JdbcFeatureToggleProvider;
import com.picsauditing.i18n.model.logging.TranslationKeyDoNothingLogger;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.i18n.service.TranslationServiceAdapter;
import com.picsauditing.i18n.service.TranslationServiceProperties;
import com.picsauditing.model.i18n.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.spun.util.ObjectUtils;
import com.spun.util.persistence.Loader;

public class TranslationServiceFactory {
	private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;
    private static final String environment = System.getProperty("pics.env");

	// for testing
	private static TranslationService translationService;
    private static FeatureToggle featureToggleChecker;
	private static TranslationService nonLoggingTranslationService;
    // for testing ^

    public static TranslationService getNonLoggingTranslationService() {
        if (nonLoggingTranslationService != null) {
            return nonLoggingTranslationService;
        }

        if (useTranslationServiceAdapter()) {
            ActionUsageContext context = new ActionUsageContext();
            TranslationServiceProperties.Builder propertyBuilder = new TranslationServiceProperties.Builder().context(context);
            propertyBuilder.translationUsageLogger(new TranslationKeyDoNothingLogger());
            return new TranslationServiceAdapter(propertyBuilder.build());
        }

        return I18nCache.getInstance();
    }

    public static TranslationService getTranslationService() {
        if (translationService != null) {
            return translationService;
        }

        if (useTranslationServiceAdapter()) {
            TranslationServiceProperties.Builder propertyBuilder = translationServiceProperties();
            return new TranslationServiceAdapter(propertyBuilder.build());
        }

        return I18nCache.getInstance();
    }

    // if we have to parameterize the command group name for more than TranslateCommand, this will have to be a more
    // sophisticated configuration object. For now, though, let's stay simple
    public static TranslationService getTranslationService(String translateCommandKey) {
        if (translationService != null) {
            return translationService;
        }

        if (useTranslationServiceAdapter()) {
            TranslationServiceProperties.Builder propertyBuilder = translationServiceProperties();
            propertyBuilder.translationCommandKey(translateCommandKey);
            return new TranslationServiceAdapter(propertyBuilder.build());
        }

        return I18nCache.getInstance();
    }

    private static TranslationServiceProperties.Builder translationServiceProperties() {
        ActionUsageContext context = new ActionUsageContext();
        TranslationServiceProperties.Builder propertyBuilder = new TranslationServiceProperties.Builder().context(context);

        if (logTranslationUsage()) {
            propertyBuilder.translationUsageLogger(new TranslationKeyAggregateUsageLogger());
        } else {
            propertyBuilder.translationUsageLogger(new TranslationKeyDoNothingLogger());
        }
        return propertyBuilder;
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
        if (featureToggleChecker != null) {
            return featureToggleChecker;
        } else {
            // we're creating a new one each time to get it to re-evaluate "permissions" since MDO changed that to hold
            // the permissions object
            FeatureToggleCheckerGroovy featureToggleChecker = new FeatureToggleCheckerGroovy(new JdbcFeatureToggleProvider(), null);
            featureToggleChecker.addToggleVariable("env", environment());
            return featureToggleChecker;
        }
    }

    private static String environment() {
        if (environment == null) {
            return "UNKNOWN";
        } else {
            return environment;
        }
    }

}
