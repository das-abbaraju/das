package com.picsauditing.service.i18n;

import java.util.*;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.jdbc.JdbcAppPropertyProvider;
import com.picsauditing.dao.jdbc.JdbcFeatureToggleProvider;
import com.picsauditing.i18n.model.UsageContext;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.i18n.model.logging.TranslationKeyDoNothingLogger;
import com.picsauditing.i18n.model.strategies.EmptyTranslationStrategy;
import com.picsauditing.i18n.model.strategies.ReturnKeyTranslationStrategy;
import com.picsauditing.i18n.model.strategies.TranslationStrategy;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.i18n.service.TranslationServiceAdapter;
import com.picsauditing.i18n.service.TranslationServiceProperties;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.model.i18n.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.spun.util.ObjectUtils;
import com.spun.util.persistence.Loader;

public class TranslationServiceFactory {
	private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;
    private static final String environment = System.getProperty("pics.env");
    private static final String APP_PROPERTY_TRANSLATION_STRATEGY_NAME = "TranslationTransformStrategy";
    private static final String STRATEGY_RETURN_KEY = "ReturnKeyOnEmptyTranslation";

    private static AppPropertyProvider appPropertyProvider;

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
            TranslationServiceProperties.Builder propertyBuilder = translationServiceProperties().translationUsageLogger(new TranslationKeyDoNothingLogger());
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
        TranslationServiceProperties.Builder propertyBuilder = new TranslationServiceProperties.Builder()
                .context(context)
                .translationStrategy(translationTransformStrategy());

        if (logTranslationUsage()) {
            propertyBuilder.translationUsageLogger(new TranslationKeyAggregateUsageLogger());
        } else {
            propertyBuilder.translationUsageLogger(new TranslationKeyDoNothingLogger());
        }
        return propertyBuilder;
    }

    private static boolean useTranslationServiceAdapter() {
	    // return featureToggle().isFeatureEnabled(FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER);
        try {
            return Features.USE_TRANSLATION_SERVICE_ADAPTER.isActive();
        } catch (Exception e) {
            return true;
        }
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

    protected static TranslationStrategy translationTransformStrategy() {
        AppPropertyProvider appPropertyProvider = appPropertyProvider();
        String translationStrategyName = appPropertyProvider.findAppProperty(APP_PROPERTY_TRANSLATION_STRATEGY_NAME);
        if (STRATEGY_RETURN_KEY.equalsIgnoreCase(translationStrategyName)) {
            return new ReturnKeyTranslationStrategy();
        } else {
            return new EmptyTranslationStrategy();
        }
    }

    private static AppPropertyProvider appPropertyProvider() {
        if (appPropertyProvider == null) {
            appPropertyProvider = new JdbcAppPropertyProvider();
        }
        return appPropertyProvider;
    }

}
