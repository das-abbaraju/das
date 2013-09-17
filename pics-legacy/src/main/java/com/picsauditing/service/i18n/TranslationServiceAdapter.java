package com.picsauditing.service.i18n;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.dao.jdbc.JdbcAppPropertyProvider;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.events.TranslationLookupEvent;
import com.picsauditing.model.general.AppPropertyProvider;
import com.picsauditing.model.i18n.TranslationLookupData;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.model.i18n.translation.strategy.*;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.sun.jersey.api.client.*;
import net.sf.ehcache.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.MediaType;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TranslationServiceAdapter implements TranslationService {

	private static TranslationService INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(TranslationServiceAdapter.class);

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_TRANSLATION = Strings.EMPTY_STRING;

    private static final String TRANSLATION_URL =
            ((System.getProperty("translation.server") == null) ? "http://translate.picsorganizer.com" : System.getProperty("translation.server")) + "/api/";
    private static final String LOCALES_URL = TRANSLATION_URL + "locales/";
    private static final String UPDATE_URL = TRANSLATION_URL + "saveOrUpdate/";
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
    private static final String ERROR_STRING = "ERROR";

    private Client client;

    TranslationServiceAdapter() {
        // CacheManager.create returns the existing singleton if it already exists
        CacheManager manager = CacheManager.create();
        cache = manager.getCache(CACHE_NAME);
        wildcardCache = manager.getCache(WILDCARD_CACHE_NAME);
    }

	public static TranslationService getInstance() {
        TranslationService service = INSTANCE;
        if (service == null) {
            synchronized (TranslationServiceAdapter.class) {
                service = INSTANCE;
                if (service == null) {
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
        publishTranslationLookupEventIfReturned(locale, translation);
        return (translation == null) ? DEFAULT_TRANSLATION : translation.getTranslation();
	}

    TranslationWrapper getTextForKey(String key, String locale) {
        TranslationWrapper translation = doTranslation(key, locale);
        return (translation != null)
            ? translationStrategy.transformTranslation(translation)
            : null;
    }

    private TranslationWrapper doTranslation(String key, String locale) {
        TranslationWrapper translation;
        if (translationKeyValidator.validateKey(key)) {
            Element element = cache.get(key);
            if (element == null) {
                translation = translationFromWebResource(key, locale);
                cacheTranslationIfReturned(key, locale, translation);
            } else {
                translation = translationFromCacheOrWebResourceIfLocaleCacheMiss(key, locale, element);
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

    private TranslationWrapper translationFromCacheOrWebResourceIfLocaleCacheMiss(String key, String locale, Element element) {
        TranslationWrapper translation;
        Map<String,String> localeToText = (Map<String,String>) element.getObjectValue();
        if (!localeToText.containsKey(locale)) {
            translation = cacheMiss(key, locale, localeToText);
        } else {
            translation = translationFromMap(key, locale, localeToText);
        }
        return translation;
    }

    private TranslationWrapper cacheMiss(String key, String locale, Map<String, String> localeToText) {
        TranslationWrapper translation = translationFromWebResource(key, locale);
        if (translation != null) {
            localeToText.put(locale, translation.getTranslation());
        }
        return translation;
    }

    private boolean translationReturned(TranslationWrapper translation) {
        return translation != null && !"ERROR".equals(translation.getTranslation());
    }

    // note that we're caching the requested key and locale, not the returned ones
    private void cacheTranslationIfReturned(String key, String locale, TranslationWrapper translation) {
        if (translationReturned(translation)) {
            Map<String,String> localeToText = new HashMap<>();
            localeToText.put(locale, translation.getTranslation());
            Element element = new Element(key, localeToText);
            logger.debug("adding {} {} to cache", key, locale);
            cache.put(element);
        }
    }

    private TranslationWrapper translationFromWebResource(String key, String locale) {
        TranslationWrapper translation;
        ClientResponse response = makeServiceApiCall(getTranslationUrl(key, locale));

        if (response.getStatus() != 200) {
            logger.error("Failed : HTTP error code : {}", response.getStatus());
            translation = new TranslationWrapper.Builder().key(key).locale(locale).translation(ERROR_STRING).build();
        } else {
            JSONObject json = parseJson(response.getEntity(String.class));
            translation = new TranslationWrapper.Builder()
                    .key(key)
                    .locale(actualLocaleFromJson(json))
                    .translation(translationTextFromJson(json))
                    .qualityRating(qualityRatingFromJson(json))
                    .build();
        }
        return translation;
    }

    private List<TranslationWrapper> translationsFromWebResourceByWildcard(String key, String locale) {
        List<TranslationWrapper> translations = new ArrayList<>();
        ClientResponse response = makeServiceApiCall(getTranslationLikeUrl(key, locale));

        if (response.getStatus() != 200) {
            logger.error("Failed : HTTP error code : {}", response.getStatus());
            translations.add(new TranslationWrapper.Builder().key(key).locale(locale).translation(ERROR_STRING).build());
        } else {
            JSONArray json = parseJsonArray(response.getEntity(String.class));
            for (Object jsonObject : json) {
                translations.add(new TranslationWrapper.Builder()
                        .key(keyFromJson(((JSONObject) jsonObject)))
                        .locale(actualLocaleFromJson(((JSONObject) jsonObject)))
                        .translation(translationTextFromJson(((JSONObject) jsonObject)))
                        .retrievedByWildcard(true)
                        .build());
            }
        }
        return translations;
    }

    private JSONArray parseJsonArray(String jsonString) {
        return (JSONArray) JSONValue.parse(jsonString);
    }

    private JSONObject parseJson(String jsonString) {
        return (JSONObject) JSONValue.parse(jsonString);
    }

    private String keyFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return (String)((JSONObject)best).get("key");
        } else {
            return (String) json.get("key");
        }
    }

    private String actualLocaleFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return (String)((JSONObject)best).get("locale");
        } else {
            return (String) json.get("locale");
        }
    }

    private String translationTextFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return (String)((JSONObject)best).get("value");
        } else {
            return (String) json.get("value");
        }
    }

    private TranslationQualityRating qualityRatingFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return TranslationQualityRating.valueOf((String)((JSONObject)best).get("qualityRating"));
        } else {
            if (json.get("qualityRating") != null) {
                return TranslationQualityRating.valueOf((String) json.get("qualityRating"));
            } else {
                return TranslationQualityRating.Good;
            }
        }
    }

    private ClientResponse makeServiceApiCall(String url) {
        Client client = client();
        ClientResponse response = null;
        WebResource webResource = client.resource(url);
        if (webResource != null) {
            logger.debug("getting ClientResponse from {}", webResource);
            // TODO wrap in try for unknown host exceptions, etc.
            response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
            logger.debug("received response {}", response);
        }
        return response;
    }

    private Client client() {
        if (client == null) {
            client = Client.create();
        }
        return client;
    }


    private String getTranslationUrl(String key, String locale) {
        StringBuilder url = urlBase(locale);
        url.append(key);

        // TODO: I don't think we're using this
        StringBuffer referrer = referrer();
        if (referrer != null) {
            url.append("?referrer=").append(referrer);
        }
        return url.toString();
    }

    private StringBuilder urlBase(String locale) {
        StringBuilder url = new StringBuilder(TRANSLATION_URL).append(locale).append("/");
        return url;
    }

    private StringBuffer referrer() {
        try {
            return ServletActionContext.getRequest().getRequestURL();
        } catch (Exception e) {
            logger.warn("No ServletActionContext Request available");
            return new StringBuffer("UNKNOWN");
        }
    }

    private String getTranslationLikeUrl(String key, String locale) {
        return urlBase(locale).append("like/").append(key).append("%25").toString();
    }

    // TODO: is this a good name?
    private TranslationWrapper translationFromMap(String key, String locale, Map<String,String> localeToText) {
        return new TranslationWrapper.Builder()
            .key(key)
            .locale(locale)
            .translation(localeToText.get(locale))
            .build();
    }

    private void publishTranslationLookupEventIfReturned(String locale, TranslationWrapper translation) {
        if (translationReturned(translation)) {
            TranslationLookupData data = createPublishData(locale, translation);
            int notAcceptedCount = 0;
            boolean notAccepted = true;
            while(notAccepted && notAcceptedCount < 8) {
                if (notAcceptedCount > 0) {
                    logger.warn("Retrying publish. Tries: {}", notAcceptedCount);
                }
                try {
                    SpringUtils.publishEvent(new TranslationLookupEvent(data));
                    notAccepted = false;
                } catch (TaskRejectedException e) {
                    try {
                        logger.warn("TaskExecuter rejected this publish: {}", e.getMessage());
                        notAcceptedCount++;
                        Thread.sleep(250);
                    } catch (InterruptedException e1) {
                    }
                }
            }
        }
    }

    private TranslationLookupData createPublishData(String localeRequested, TranslationWrapper translation) {
        TranslationLookupData data = new TranslationLookupData();
        data.setLocaleRequest(localeRequested);
        data.setLocaleResponse(translation.getLocale());
        data.setReferrer(getTranslationUrl(translation.getKey(), localeRequested));
        data.setRequestDate(new Date());
        data.setPageName(pageName());
        data.setMsgKey(translation.getKey());
        data.setEnvironment(environment);
        data.setRetrievedByWildcard(translation.isRetrievedByWildcard());
        return data;
    }

    private String pageName() {
        try {
            String pageName = ServletActionContext.getContext().getName();
            if (Strings.isEmpty(pageName )) {
                pageName = parsePageFromReferrer();
            }
            return pageName;
        } catch (Exception e) {
            logger.warn("No ServletActionContext Request available");
            return "UNKNOWN";
        }
    }

    private String parsePageFromReferrer() {
        String referrer = referrer().toString();
        Pattern ptrn= Pattern.compile(".*/(.*)\\.action$");
        Matcher matcher = ptrn.matcher(referrer);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else {
            return "UNKNOWN";
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

	@Override
	public Map<String, String> getText(String key) {
        JSONArray locales = allLocalesForKey(key);

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
        populateTranslationsForJSByWildCardAndPublishUse(key, translationsForJS, locale);
        return translationsForJS;
    }

    private Map<String, String> cachedTranslationsForKey(String key) {
        Element element = cache.get(key);
        Map<String,String> cachedTranslations =  new HashMap<>();
        if (element != null && element.getObjectValue() != null) {
            cachedTranslations =  (Map<String,String>) element.getObjectValue();
        }
        return cachedTranslations;
    }

    private Map<String, String> updateCacheWithLocalesNotPreviouslyRequested(String key, JSONArray locales) {
        Map<String, String> cachedTranslations = cachedTranslationsForKey(key);

        for (int i = 0; i < locales.size(); i++) {
            String locale = (String) locales.get(i);
            if (!cachedTranslations.containsKey(locale)) {
                getText(key, locale);
            }
        }
        return cachedTranslationsForKey(key);
    }

    private JSONArray allLocalesForKey(String key) {
        JSONArray locales = new JSONArray();
        ClientResponse response = makeServiceApiCall(getLocalesUrl(key));
        if (response.getStatus() != 200) {
            logger.error("Failed : HTTP error code : {}", response.getStatus());
            // locales.add("en_US");
        } else {
            JSONObject json = parseJson(response.getEntity(String.class));
            locales = (JSONArray) json.get("locales");
        }
        return locales;
    }

    private String getLocalesUrl(String key) {
        return new StringBuilder(LOCALES_URL).append(key).toString();
    }

    @Override
	public boolean hasKey(String key, Locale locale) {
        if (Strings.isEmpty(key) || key.contains(" ")) {
            return false;
        }
        String translation = getText(key, locale.toString());
        return !Strings.isEmpty(translation);
	}

    @Override
    public boolean hasKeyInLocale(String key, String locale) {
        // We're caching the requested locale and not the actual returned locale, which means that hasKey
        // will have the fallback translation as a cache for the requested locale, which means we either
        // have to also cache the requested locale, or we have to make a service call and compare the actual locale
        // with the requested. In the spirit of not prematurely optimizing, we'll just make the service call since
        // this call is used in very small, very specific places in the code
        if (Strings.isEmpty(locale)) {
            return false;
        } else {
            TranslationWrapper translation = translationFromWebResource(key, locale);
            return (locale.equals(translation.getLocale()));
        }
    }

    @Override
	public void saveTranslation(String key, String translation, List<String> requiredLanguages) {
        Client client = client();
        ClientResponse response = null;
        WebResource webResource = client.resource(UPDATE_URL);
        if (webResource != null) {
            JSONObject formData = new JSONObject();
            formData.put("key", key);
            formData.put("value", translation);
            formData.put("qualityRating", QUALITY_GOOD);
            for (String locale : requiredLanguages) {
                formData.remove("locale");
                formData.put("locale", locale);
                response = webResource
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .type(MediaType.APPLICATION_JSON_VALUE)
                        .post(ClientResponse.class, formData.toJSONString());
                if (response.getStatus() == 200) {
                    // let's just decache all locales
                    cache.remove(key);
                }
            }
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
            populateTranslationsForJSByWildCardAndPublishUse(actionName + "." + methodName, translationsForJS, locale);
            populateTranslationsForJSByWildCardAndPublishUse(actionName + "." + ACTION_TRANSLATION_KEYWORD, translationsForJS, locale);
        }

        localeListOfTranslationMaps.add(translationsForJS);
        return localeListOfTranslationMaps;
    }

    private void populateTranslationsForJSByWildCardAndPublishUse(String key, Map<String, String> translationsForJS, String locale) {
        List<TranslationWrapper> translations = wildCardTranslations(key, locale);
        for (TranslationWrapper translation : translations) {
            translationsForJS.put(translation.getKey(), translation.getTranslation());
            publishTranslationLookupEventIfReturned(locale, translation);
        }
    }

    private List<TranslationWrapper> wildCardTranslations(String key, String locale) {
        List<TranslationWrapper> translations = translationsFromWildCardCache(key, locale);
        if ((translations == null || translations.isEmpty()) && !wildcardCacheExistsForKey(key)) {
            translations = translationsFromWebResourceByWildcard(key, locale);
            cacheWildcardTranslation(key, locale, translations);
        }
        return translations;
    }

    private boolean wildcardCacheExistsForKey(String key) {
        return wildcardCache.isKeyInCache(key);
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

        for (TranslationWrapper translation : translations) {
            localeToKeyToValue.put(requestedLocale, translation.getKey(), translation.getTranslation());
        }
        wildcardCache.put(new Element(wildcardKey, localeToKeyToValue));
    }

    @Override
	public void clear() {
		synchronized (this) {
            INSTANCE = null;
        }
	}

	@Override
	public Date getLastCleared() {
		// TODO Auto-generated method stub
		return null;
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
}
