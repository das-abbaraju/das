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

	private static TranslationService INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(TranslationServiceAdapter.class);

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_TRANSLATION = Strings.EMPTY_STRING;
    public static final String DEFAULT_PAGENAME = "UNKNOWN";
    public static final String DEFAULT_REFERRER = "REFERRER_UNKNOWN";

    private static final String CACHE_NAME = "i18n";
    private static final String WILDCARD_CACHE_NAME = "i18n-wildcards";
    private static final String APP_PROPERTY_TRANSLATION_STRATEGY_NAME = "TranslationTransformStrategy";
    private static final String STRATEGY_RETURN_KEY = "ReturnKeyOnEmptyTranslation";
    private static Cache cache;
    private static Cache wildcardCache;
    private static final String environment = System.getProperty("pics.env");
    private static AppPropertyProvider appPropertyProvider;
    private static TranslationStrategy translationStrategy;
    private static TranslationKeyValidator translationKeyValidator = new TranslationKeyValidator();
    private static Date dateLastCleared;
    private static TranslationUsageLogger translationUsageLogger;

    private TranslateRestClient translateRestClient;

    TranslationServiceAdapter() {
        // CacheManager.create returns the existing singleton if it already exists
        CacheManager manager = CacheManager.create();
        cache = manager.getCache(CACHE_NAME);
        wildcardCache = manager.getCache(WILDCARD_CACHE_NAME);
    }

	public static TranslationService getInstance(TranslationUsageLogger usageLogger) {
        TranslationService service = INSTANCE;
        if (service == null) {
            synchronized (TranslationServiceAdapter.class) {
                service = INSTANCE;
                if (service == null) {
                    translationUsageLogger = usageLogger;
                    registerTranslationTransformStrategy();
                    INSTANCE = new TranslationServiceAdapter();
                }
            }
        }
		return INSTANCE;
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
            Element element = cache.get(key);
            if (element == null) {
                translation = translateRestClient().translationFromWebResource(key, requestedLocale);
                cacheTranslationIfReturned(key, requestedLocale, translation);
            } else {
                translation = translationFromCacheOrWebResourceIfLocaleCacheMiss(key, requestedLocale, element);
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

    private TranslationWrapper translationFromCacheOrWebResourceIfLocaleCacheMiss(String key, String requestedLocale, Element element) {
        TranslationWrapper translation;
        Table<String, String, String> requestedlocaleToTextToReturnedLocale = (Table<String, String, String>) element.getObjectValue();
        if (!requestedlocaleToTextToReturnedLocale.containsRow(requestedLocale)) {
            translation = cacheMiss(key, requestedLocale, requestedlocaleToTextToReturnedLocale);
        } else {
            translation = translationFrom(key, requestedLocale, requestedlocaleToTextToReturnedLocale);
        }
        return translation;
    }

    private TranslationWrapper cacheMiss(String key, String requestedLocale, Table<String, String, String> requestedlocaleToReturnedLocaleToText) {
        TranslationWrapper translation = translateRestClient().translationFromWebResource(key, requestedLocale);
        if (translation != null) {
            requestedlocaleToReturnedLocaleToText.put(requestedLocale, translation.getLocale(), translation.getTranslation());
        }
        return translation;
    }

    private boolean translationReturned(TranslationWrapper translation) {
        return translation != null && !ERROR_STRING.equals(translation.getTranslation()) && !DEFAULT_PAGENAME.equals(pageName());
    }

    private void cacheTranslationIfReturned(String key, String requestedLocale, TranslationWrapper translation) {
        if (translationReturned(translation)) {
            Table<String, String, String> requestedlocaleToReturnedLocaleToText = TreeBasedTable.create();
            requestedlocaleToReturnedLocaleToText.put(requestedLocale, translation.getLocale(), translation.getTranslation());
            Element element = new Element(key, requestedlocaleToReturnedLocaleToText);
            cache.put(element);
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

    private TranslationWrapper translationFrom(String key, String requestedLocale, Table<String, String, String> requestedlocaleToReturnedLocaleToText) {
        Map<String,String> returnedLocaleToText = requestedlocaleToReturnedLocaleToText.row(requestedLocale);
        String returnedLocale = returnedLocaleToText.keySet().iterator().next();
        String translation = returnedLocaleToText.get(returnedLocale);
        return new TranslationWrapper.Builder()
            .key(key)
            .locale(returnedLocale)
            .requestedLocale(requestedLocale)
            .translation(translation)
            .build();
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

        Table<String, String, String> requestedlocaleToReturnedLocaleToText = cachedTranslationsForKey(key);
        for (String locale : locales) {
            cachedTranslations.putAll(requestedlocaleToReturnedLocaleToText.row(locale));
        }
        return cachedTranslations;
    }

    private Table<String, String, String> cachedTranslationsForKey(String key) {
        Element element = cache.get(key);
        Table<String, String, String> requestedlocaleToReturnedLocaleToText = TreeBasedTable.create();
        if (element != null && element.getObjectValue() != null) {
            requestedlocaleToReturnedLocaleToText =  (Table<String, String, String>) element.getObjectValue();
        }
        return requestedlocaleToReturnedLocaleToText;
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
        List<TranslationWrapper> translations = translationsFromWildCardCache(key, locale);
        if ((translations == null || translations.isEmpty())) {
            translations = translateRestClient().translationsFromWebResourceByWildcard(key, locale);
            cacheWildcardTranslation(key, locale, translations);
        }
        return translations;
    }

    private List<TranslationWrapper> translationsFromWildCardCache(String key, String locale) {
        List<TranslationWrapper> translations = new ArrayList<>();
        Element element = wildcardCache.get(key);
        if (element != null) {
            Table<String, String, String> localeToKeyToValue = (Table<String, String, String>)element.getObjectValue();
            Map<String, String> keyToTranslation = Collections.unmodifiableMap(localeToKeyToValue.row(locale));
            if (keyToTranslation != null) {
                for (String msgKey : keyToTranslation.keySet()) {
                    translations.add(new TranslationWrapper.Builder().key(msgKey).locale(locale).translation(keyToTranslation.get(msgKey)).build());
                }
            }
        }
        return translations;
    }

    private void cacheWildcardTranslation(String wildcardKey, String requestedLocale, List<TranslationWrapper> translations) {
        Table<String, String, String> localeToKeyToValue;
        Element element = wildcardCache.get(wildcardKey);
        if (element != null) {
            localeToKeyToValue = (Table<String, String, String>)element.getObjectValue();
        } else {
            localeToKeyToValue = TreeBasedTable.create();
        }

        if (translations == null || translations.isEmpty()) {
            localeToKeyToValue.put(requestedLocale, "", "");
        } else {
            for (TranslationWrapper translation : translations) {
                localeToKeyToValue.put(requestedLocale, translation.getKey(), translation.getTranslation());
            }
        }
        wildcardCache.put(new Element(wildcardKey, localeToKeyToValue));
    }

    @Override
	public void clear() {
		synchronized (this) {
            INSTANCE = null;
            cache.removeAll();
            wildcardCache.removeAll();
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
            translateRestClient = new TranslateRestClient();
        }
        return translateRestClient;
    }
}
