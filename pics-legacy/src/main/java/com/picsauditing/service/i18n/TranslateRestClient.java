package com.picsauditing.service.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.*;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import com.picsauditing.models.database.TranslationUsage;

import java.util.ArrayList;
import java.util.List;

public class TranslateRestClient {
    private static final Logger logger = LoggerFactory.getLogger(TranslateRestClient.class);
    private static final ObjectMapper mapper = new ObjectMapper(); // thread-safe as long as we don't modify the configuration
    private static final String TRANSLATION_URL =
            ((System.getProperty("translation.server") == null) ? "http://translate.picsorganizer.com" : System.getProperty("translation.server")) + "/api/";
    private static final String LOCALES_URL = TRANSLATION_URL + "locales/";
    private static final String UPDATE_URL = TRANSLATION_URL + "saveOrUpdate/";
    private static final String LOG_URL = TRANSLATION_URL + "logTranslationUsage/";

    private Client client;

    private Client client() {
        if (client == null) {
            client = Client.create();
        }
        return client;
    }

    public TranslationWrapper translationFromWebResource(String key, String locale) {
        TranslationWrapper translation;
        ClientResponse response = makeServiceApiCall(getTranslationUrl(key, locale));

        if (response.getStatus() != 200) {
            translation = failedResponseTranslation(key, locale, response);
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

    public List<TranslationWrapper> translationsFromWebResourceByWildcard(String key, String locale) {
        List<TranslationWrapper> translations = new ArrayList<>();
        ClientResponse response = makeServiceApiCall(getTranslationLikeUrl(key, locale));

        if (response.getStatus() != 200) {
            translations.add(failedResponseTranslation(key, locale, response));
        } else {
            JSONArray json = parseJsonArray(response.getEntity(String.class));
            for (Object jsonObject : json) {
                translations.add(new TranslationWrapper.Builder()
                        .key(keyFromJson(((JSONObject) jsonObject)))
                        .locale(actualLocaleFromJson(((JSONObject) jsonObject)))
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
        Client client = client();
        WebResource webResource = client.resource(UPDATE_URL);
        if (webResource != null) {
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
        Client client = client();
        WebResource webResource = client.resource(LOG_URL);
        if (webResource != null) {
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
        }
        return false;
    }

    public List<String> allLocalesForKey(String key) {
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


    private String getTranslationUrl(String key, String locale) {
        return urlBase(locale).append(key).toString();
    }

    private String getTranslationLikeUrl(String key, String locale) {
        return urlBase(locale).append("like/").append(key).append("%25").toString();
    }

    private String getLocalesUrl(String key) {
        return new StringBuilder(LOCALES_URL).append(key).toString();
    }

    private StringBuilder urlBase(String locale) {
        StringBuilder url = new StringBuilder(TRANSLATION_URL).append(locale).append("/");
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


}
