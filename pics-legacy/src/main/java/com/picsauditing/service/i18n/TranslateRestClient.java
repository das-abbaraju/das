package com.picsauditing.service.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import com.picsauditing.models.database.TranslationUsage;

import java.util.*;

public class TranslateRestClient {
    private static final Logger logger = LoggerFactory.getLogger(TranslateRestClient.class);
    private static final ObjectMapper mapper = new ObjectMapper(); // thread-safe as long as we don't modify the configuration
    private static final String TRANSLATION_URL =
            ((System.getProperty("translation.server") == null) ? "http://translate.picsorganizer.com" : System.getProperty("translation.server")) + "/api/";
    private static final String LOCALES_URL = TRANSLATION_URL + "locales/";
    private static final String UPDATE_URL = TRANSLATION_URL + "saveOrUpdate/";
    private static final String LOG_URL = TRANSLATION_URL + "logTranslationUsage/";

    private static final int WEB_CONNECT_TIMEOUT_MS = 1000;
    private static final int WEB_READ_TIMEOUT_MS = 1000;

    private static Client client;
    private static WebResource webResource;

    /*
        The jersey client is threadsafe as long as you don't attempt to change the configuration after creation.
        Also, using getEntity (get(String)), it will close its own connections/resources/streams.
     */
    static {
        resetWebClient();
    }

    public static void resetWebClient() {
        ClientConfig cc = new DefaultClientConfig();
        Map<String, Object> props = cc.getProperties();
        props.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, WEB_CONNECT_TIMEOUT_MS);
        props.put(ClientConfig.PROPERTY_READ_TIMEOUT, WEB_READ_TIMEOUT_MS);
        client = Client.create(cc);
        webResource = client.resource(TRANSLATION_URL);
    }

    public TranslationWrapper translationFromWebResource(String key, String requestedLocale) {
        TranslateCommand command = new TranslateCommand(key, requestedLocale);
        return command.execute();
    }

    public List<TranslationWrapper> translationsFromWebResourceByWildcard(String key, String locale) {
        List<TranslationWrapper> translations = new ArrayList<>();
        ClientResponse response = makeServiceApiCall(getTranslationLikePath(key, locale));

        if (response.getStatus() != 200) {
            translations.add(failedResponseTranslation(key, locale, response));
        } else {
            JSONArray json = parseJsonArray(response.getEntity(String.class));
            for (Object jsonObject : json) {
                translations.add(new TranslationWrapper.Builder()
                        .key(keyFromJson(((JSONObject) jsonObject)))
                        .locale(actualLocaleFromJson(((JSONObject) jsonObject)))
                        .requestedLocale(locale)
                        .translation(translationTextFromJson(((JSONObject) jsonObject)))
                        .qualityRating(qualityRatingFromJson(((JSONObject) jsonObject)))
                        .retrievedByWildcard(true)
                        .build());
            }
        }
        return translations;
    }

    private TranslationWrapper failedResponseTranslation(String key, String locale, ClientResponse response) {
        logger.error("Failed : HTTP error code : {}", response.getStatus());
        return new TranslationWrapper.Builder()
                .key(key)
                .locale(locale)
                .translation(TranslationService.ERROR_STRING)
                .qualityRating(TranslationQualityRating.Bad)
                .build();
    }

    public boolean saveTranslation(String key, String translation, List<String> requiredLanguages) {
        webResource.path(UPDATE_URL);
        JSONObject formData = new JSONObject();
        formData.put("key", key);
        formData.put("value", translation);
        formData.put("qualityRating", TranslationService.QUALITY_GOOD);
        for (String locale : requiredLanguages) {
            formData.remove("locale");
            formData.put("locale", locale);
            ClientResponse response = webResource
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .type(MediaType.APPLICATION_JSON_VALUE)
                    .post(ClientResponse.class, formData.toJSONString());
            if (response.getStatus() != 200) {
                return false;
            }
        }
        return true;
    }

    public boolean updateTranslationLog(List<TranslationUsage> lookupData) {
        return doUpdateTranslationLog(lookupData);
    }

    public boolean updateTranslationLog(TranslationUsage lookupData) {
        return doUpdateTranslationLog(lookupData);
    }

    private boolean doUpdateTranslationLog(Object lookupData) {
        webResource.path(LOG_URL);
        try {
            String jsonString = mapper.writeValueAsString(lookupData);
            ClientResponse response = webResource
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .type(MediaType.APPLICATION_JSON_VALUE)
                    .post(ClientResponse.class, jsonString);
            if (response.getStatus() == 200) {
                return true;
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to create json payload {}", e);
        }
        return false;
    }

    public List<String> allLocalesForKey(String key) {
        JSONArray locales = new JSONArray();
        ClientResponse response = makeServiceApiCall(getLocalesPath(key));
        if (response.getStatus() != 200) {
            logger.error("Failed : HTTP error code : {}", response.getStatus());
            // locales.add("en_US");
        } else {
            JSONObject json = parseJson(response.getEntity(String.class));
            locales = (JSONArray) json.get("locales");
        }
        return locales;
    }

    private ClientResponse makeServiceApiCall(String path) {
        ClientResponse response = null;
        webResource.path(path);
        logger.debug("getting ClientResponse from {}", webResource);
        // TODO wrap in try for unknown host exceptions, etc.
        response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
        logger.debug("received response {}", response);
        return response;
    }


    private String getTranslationPath(String key, String locale) {
        return pathBase(locale).append(key).toString();
    }

    private String getTranslationLikePath(String key, String locale) {
        return pathBase(locale).append("like/").append(key).append("%25").toString();
    }

    private String getLocalesPath(String key) {
        return new StringBuilder(LOCALES_URL).append(key).toString();
    }

    private StringBuilder pathBase(String locale) {
        StringBuilder url = new StringBuilder(locale).append("/");
        return url;
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

    // for injecting test client for unit tests
    public static void registerWebClient(Client webclient) {
        client = webclient;
        webResource = client.resource(TRANSLATION_URL);
    }

}
