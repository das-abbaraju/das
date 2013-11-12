package com.picsauditing.service.i18n;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.dao.jdbc.JdbcAppPropertyProvider;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.model.i18n.TranslationLookupData;
import com.picsauditing.model.i18n.TranslationUsageLogger;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.model.i18n.translation.strategy.*;
import com.picsauditing.util.Strings;
import net.sf.ehcache.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class TranslationServiceAdapter implements TranslationService {

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_TRANSLATION = Strings.EMPTY_STRING;
    public static final String DEFAULT_PAGENAME = "UNKNOWN";
    public static final String DEFAULT_REFERRER = "REFERRER_UNKNOWN";
    private static final String APP_PROPERTY_TRANSLATION_STRATEGY_NAME = "TranslationTransformStrategy";
    private static final String STRATEGY_RETURN_KEY = "ReturnKeyOnEmptyTranslation";
    private static final String environment = System.getProperty("pics.env");
    private static AppPropertyProvider appPropertyProvider;

    private static TranslationStrategy translationStrategy;
    private static TranslationKeyValidator translationKeyValidator = new TranslationKeyValidator();
    private static Date dateLastCleared;

    private final Logger logger = LoggerFactory.getLogger(TranslationServiceAdapter.class);

    private TranslateRestClient translateRestClient;
    private TranslationCache cache = new TranslationCache();
    private TranslationWildcardCache wildcardCache = new TranslationWildcardCache();
    private TranslationUsageLogger translationUsageLogger;
    private String translateCommandKey;

	public TranslationServiceAdapter(TranslationUsageLogger usageLogger) {
        translationUsageLogger = usageLogger;
        registerTranslationTransformStrategy();
	}

    public TranslationServiceAdapter(TranslationUsageLogger usageLogger, String translateCommandKey) {
        this.translateCommandKey = translateCommandKey;
        translationUsageLogger = usageLogger;
        registerTranslationTransformStrategy();
    }

    protected static void registerTranslationTransformStrategy() {
        AppPropertyProvider appPropertyProvider = appPropertyProvider();
        String translationStrategyName = appPropertyProvider.findAppProperty(APP_PROPERTY_TRANSLATION_STRATEGY_NAME);
        if (STRATEGY_RETURN_KEY.equalsIgnoreCase(translationStrategyName)) {
            TranslationServiceAdapter.registerTranslationStrategy(new ReturnKeyTranslationStrategy());
        } else {
            TranslationServiceAdapter.registerTranslationStrategy(new EmptyTranslationStrategy());
        }
    }

    @Override
	public String getText(String key, Locale locale) {
        return getText(key, locale.toString());
    }

	@Override
	public String getText(String key, String locale) {
        TranslationWrapper translation = getTextForKey(key, locale);
        logTranslationLookupIfReturned(locale, translation);
        return (translation == null) ? DEFAULT_TRANSLATION : translation.getTranslation();
	}

    TranslationWrapper getTextForKey(String key, String locale) {
        TranslationWrapper translation = doTranslation(key, locale);
        return (translation != null)
            ? translationStrategy.transformTranslation(translation)
            : null;
    }

    private TranslationWrapper doTranslation(String key, String requestedLocale) {
        TranslationWrapper translation;
        if (translationKeyValidator.validateKey(key)) {
            translation = cache.get(key, requestedLocale);
            if (translation == null) {
                translation = translateRestClient().translationFromWebResource(key, requestedLocale);
                cacheTranslationIfReturned(translation);
            }
        } else {
            logger.error("The key {} is invalid", key);
            translation = translationForInvalidKey();
        }
        return translation;
    }

    private TranslationWrapper translationForInvalidKey() {
        return new TranslationWrapper.Builder().key(ERROR_STRING).translation(ERROR_STRING).build();
    }

    private boolean translationReturned(TranslationWrapper translation) {
        return translation != null && !ERROR_STRING.equals(translation.getTranslation()) && !DEFAULT_PAGENAME.equals(pageName());
    }

    private void cacheTranslationIfReturned(TranslationWrapper translation) {
        if (translationReturned(translation)) {
            cache.put(translation);
        }
    }

    private String referrer() {
        try {
            return ServletActionContext.getRequest().getRequestURL().toString();
        } catch (Exception e) {
            logger.warn("No ServletActionContext Request available");
            return DEFAULT_REFERRER;
        }
    }

    private void logTranslationLookupIfReturned(String locale, TranslationWrapper translation) {
        if (translationUsageLogger != null && translationReturned(translation)) {
            TranslationLookupData data = createPublishData(locale, translation);
            translationUsageLogger.logTranslationUsage(data);
        }
    }

    private TranslationLookupData createPublishData(String localeRequested, TranslationWrapper translation) {
        TranslationLookupData data = new TranslationLookupData();
        data.setLocaleRequest(localeRequested);
        data.setLocaleResponse(translation.getLocale());
        data.setReferrer(referrer());
        data.setRequestDate(new Date());
        data.setPageName(pageName());
        data.setMsgKey(translation.getKey());
        data.setEnvironment(environment());
        data.setRetrievedByWildcard(translation.isRetrievedByWildcard());
        return data;
    }

    private String environment() {
        if (environment == null) {
            return "UNKNOWN";
        } else {
            return environment;
        }
    }

    private String pageName() {
        try {
            String pageName = ServletActionContext.getContext().getName();
            if (Strings.isEmpty(pageName )) {
                pageName = DEFAULT_PAGENAME;
            }
            return pageName;
        } catch (Exception e) {
            logger.warn("No ServletActionContext Request available");
            return DEFAULT_PAGENAME;
        }
    }

    @Override
	public String getText(String key, Locale locale, Object... args) {
        String translationText = getText(key, locale);
        // TODO: sanity check the translation text?
        if (args != null && args.length > 0) {
            MessageFormat message = new MessageFormat(fixFormatCharacters(translationText), locale);
            StringBuffer buffer = new StringBuffer();
            message.format(args, buffer, null);
            return buffer.toString();
        } else {
            return translationText;
        }

	}

    private String fixFormatCharacters(String text) {
        return text.replaceAll("'", "''");
    }

    @Override
	public String getText(String key, String locale, Object... args) {
        return getText(key, Strings.parseLocale(locale), args);
    }

    /* This method is returning a map of key,translationValue for all locale translations for the requested
       key. It does this by asking the service for all the locales it has for a key (always one service call at
       least). It then sees if we have a local cache for the key in these locales and those that are not in
       local cache are called for from the service and added to the cache.
    */
	@Override
	public Map<String, String> getText(String key) {
        List<String> locales = translateRestClient().allLocalesForKey(key);

        Map<String, String> translationsToReturn = new HashMap();
        Map<String, String> cachedTranslations = updateCacheWithLocalesNotPreviouslyRequested(key, locales);

        for (String locale : cachedTranslations.keySet()) {
            if (locales.contains(locale)) {
                translationsToReturn.put(locale, cachedTranslations.get(locale));
            }
        }

        return translationsToReturn;
	}

    @Override
    public Map<String, String> getTextLike(String key, String locale) {
        Map<String, String> translationsForJS = new HashMap<>();
        translationsForJS.putAll(populateTranslationsForJSByWildCardAndPublishUse(key, locale));
        return translationsForJS;
    }

    private Map<String, String> updateCacheWithLocalesNotPreviouslyRequested(String key, List<String> locales) {
        Map<String, String> cachedTranslations = new HashMap<>();

        for (String locale : locales) {
            getText(key, locale);
        }

        Table<String, String, String> requestedlocaleToReturnedLocaleToText = cache.get(key);
        if (requestedlocaleToReturnedLocaleToText != null) {
            for (String locale : locales) {
                cachedTranslations.putAll(requestedlocaleToReturnedLocaleToText.row(locale));
            }
        }
        return cachedTranslations;
    }

    @Override
	public boolean hasKey(String key, Locale locale) {
        return translationKeyValidator.validateKey(key) ? true : false;
	}

    @Override
    public boolean hasKeyInLocale(String key, String requestedLocale) {
        if (Strings.isEmpty(requestedLocale)) {
            return false;
        } else {
            TranslationWrapper translation = doTranslation(key, requestedLocale);
            return (translation != null && requestedLocale.equals(translation.getLocale()));
        }
    }

    @Override
	public void saveTranslation(String key, String translation, List<String> requiredLanguages) {
        boolean ok = translateRestClient().saveTranslation(key, translation, requiredLanguages);
        if (ok) {
            // let's just decache all locales
            cache.remove(key);
        }
	}

    @Override
    public void saveTranslation(String key, String translation) throws Exception {
        String language = DEFAULT_LANGUAGE;

        Locale locale = TranslationServiceFactory.getLocale();
        if (locale != null) {
            language = locale.toString();
        }

        saveTranslation(key, translation, Arrays.asList(language));
    }

    @Override
	public void removeTranslations(List<String> keys) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTranslation(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Map<String, String>> getTranslationsForJS(String actionName, String methodName, Set<String> locales) {
        if (CollectionUtils.isEmpty(locales) || Strings.isEmpty(actionName) || Strings.isEmpty(methodName)) {
            return Collections.emptyList();
        }
        Map<String, String> translationsForJS = new HashMap<>();
        List<Map<String, String>> localeListOfTranslationMaps = new ArrayList<>();

        for (String locale : locales) {
            translationsForJS.putAll(populateTranslationsForJSByWildCardAndPublishUse(actionName + "." + methodName, locale));
            translationsForJS.putAll(populateTranslationsForJSByWildCardAndPublishUse(actionName + "." + ACTION_TRANSLATION_KEYWORD, locale));
        }

        localeListOfTranslationMaps.add(translationsForJS);
        return localeListOfTranslationMaps;
    }

    private Map<String, String> populateTranslationsForJSByWildCardAndPublishUse(String key, String locale) {
        Map<String, String> translationsForJS = new HashMap<>();
        List<TranslationWrapper> translations = wildCardTranslations(key, locale);
        for (TranslationWrapper translation : translations) {
            translationsForJS.put(translation.getKey(), translation.getTranslation());
            logTranslationLookupIfReturned(locale, translation);
        }
        return translationsForJS;
    }

    private List<TranslationWrapper> wildCardTranslations(String key, String locale) {
        List<TranslationWrapper> translations = wildcardCache.get(key, locale);
        if ((translations == null || translations.isEmpty())) {
            translations = translateRestClient().translationsFromWebResourceByWildcard(key, locale);
            wildcardCache.put(key, locale, translations);
        }
        return translations;
    }


    @Override
	public void clear() {
		synchronized (this) {
            cache.clear();
            dateLastCleared = new Date();
        }
	}

	@Override
	public Date getLastCleared() {
		return dateLastCleared;
	}

    private static AppPropertyProvider appPropertyProvider() {
        if (appPropertyProvider == null) {
            appPropertyProvider = new JdbcAppPropertyProvider();
        }
        return appPropertyProvider;
    }

    public static void registerTranslationStrategy(TranslationStrategy strategy) {
        translationStrategy = strategy;
    }

    private TranslateRestClient translateRestClient() {
        if (translateRestClient == null) {
            if (translateCommandKey != null) {
                translateRestClient = new TranslateRestClient(translateCommandKey);
            } else {
                translateRestClient = new TranslateRestClient();
            }
        }
        return translateRestClient;
    }
}
