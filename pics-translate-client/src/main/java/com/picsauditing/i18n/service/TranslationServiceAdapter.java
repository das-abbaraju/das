package com.picsauditing.i18n.service;

import com.picsauditing.i18n.model.*;
import com.picsauditing.i18n.model.logging.TranslationUsageLogger;
import com.picsauditing.i18n.model.strategies.*;
import com.picsauditing.i18n.service.cache.TranslationCache;
import com.picsauditing.i18n.service.cache.TranslationWildcardCache;
import com.picsauditing.i18n.service.validation.TranslationKeyValidator;
import com.picsauditing.i18n.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Table;

import java.text.MessageFormat;
import java.util.*;

public class TranslationServiceAdapter implements TranslationService {

    public static final String DEFAULT_LANGUAGE = "en";

    private static Date dateLastCleared;

    private final Logger logger = LoggerFactory.getLogger(TranslationServiceAdapter.class);

    private TranslateRestClient translateRestClient;
    private TranslationCache cache = new TranslationCache();
    private TranslationWildcardCache wildcardCache = new TranslationWildcardCache();

    // these properties are set through TranslationServiceProperties
    private TranslationStrategy translationStrategy;
    private TranslationUsageLogger translationUsageLogger;
    private String translateCommandKey;
    private TranslationKeyValidator translationKeyValidator;
    // private String pageName;
    private String environment;
    private Locale locale;
    private TranslationServiceProperties properties;

	public TranslationServiceAdapter(TranslationServiceProperties properties) {
        translationStrategy = properties.getTranslationStrategy();
        translationUsageLogger = properties.getTranslationUsageLogger();
        translateCommandKey = properties.getTranslationCommandKey();
        translationKeyValidator = properties.getTranslationKeyValidator();
        //pageName = properties.getContext().pageName();
        environment = properties.getContext().environment();
        locale = properties.getContext().locale();
        this.properties = properties;
	}

    private String pageName() {
        return properties.getContext().pageName();
    }

    @Override
	public String getText(String key, Locale locale) {
        return getText(key, locale.toString());
    }

	@Override
	public String getText(String key, String locale) {
        TranslationWrapper translation = getTextForKey(key, locale);
        logTranslationLookupIfAppropriate(translation);
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
        return translation != null &&
               translationKeyValidator.validateKey(translation.getKey()) &&
               !ERROR_STRING.equals(translation.getTranslation()) && !UsageContext.DEFAULT_PAGENAME.equals(pageName());
    }

    private void cacheTranslationIfReturned(TranslationWrapper translation) {
        if (translationReturned(translation)) {
            cache.put(translation);
        }
    }

    private void logTranslationLookupIfAppropriate(TranslationWrapper translation) {
        if (translationUsageLogger != null && translationReturned(translation) && translationNotLoggedForPage(translation)) {
            TranslationLookupData data = createPublishData(translation);
            translationUsageLogger.logTranslationUsage(data);
            savePageLoggedToCache(translation);
        }
    }

    private void savePageLoggedToCache(TranslationWrapper translation) {
        translation.getUsedOnPages().add(pageName());
        cache.put(translation);
    }

    private void logWildCardTranslationsLookupIfAppropriate(String wildcardKey, String requestedLocale, List<TranslationWrapper> translations) {
        List<TranslationWrapper> translationsToSaveToCache = new ArrayList<>();
        for(TranslationWrapper translation : translations) {
            if (translationUsageLogger != null && translationReturned(translation) && translationNotLoggedForPage(translation)) {
                TranslationLookupData data = createPublishData(translation);
                translationUsageLogger.logTranslationUsage(data);
                translation.getUsedOnPages().add(pageName());
                translationsToSaveToCache.add(translation);
            }
        }
        if (!translationsToSaveToCache.isEmpty()) {
            wildcardCache.put(wildcardKey, requestedLocale, translationsToSaveToCache);
        }
    }

    private boolean translationNotLoggedForPage(TranslationWrapper translation) {
        return !translation.getUsedOnPages().contains(pageName());
    }

    private TranslationLookupData createPublishData(TranslationWrapper translation) {
        TranslationLookupData data = new TranslationLookupData();
        data.setLocaleRequest(translation.getRequestedLocale());
        data.setLocaleResponse(translation.getLocale());
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

        Table<String, String, TinyTranslation> requestedlocaleToReturnedLocaleToText = cache.get(key);
        if (requestedlocaleToReturnedLocaleToText != null) {
            for (String locale : locales) {
                Map<String, TinyTranslation> returnedLocalToTinyTranslation = requestedlocaleToReturnedLocaleToText.row(locale);
                for (String returnedLocale : returnedLocalToTinyTranslation.keySet()) {
                    TinyTranslation translation = returnedLocalToTinyTranslation.get(returnedLocale);
                    cachedTranslations.put(returnedLocale, translation.text);
                }
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
        if (isEmptyCollection(locales) || Strings.isEmpty(actionName) || Strings.isEmpty(methodName)) {
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
        }
        logWildCardTranslationsLookupIfAppropriate(key, locale, translations);
        return translationsForJS;
    }

    private List<TranslationWrapper> wildCardTranslations(String key, String locale) {
        List<TranslationWrapper> translations = wildcardCache.get(key, locale);
        if ((translations == null || translations.isEmpty())) {
            translations = translateRestClient().translationsFromWebResourceByWildcard(key, locale);
            if (translationKeyValidator.validateKey(key)) {
                wildcardCache.put(key, locale, translations);
            }
        }
        return translations;
    }

    private boolean isEmptyCollection(Collection coll) {
        return (coll == null || coll.isEmpty());
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
