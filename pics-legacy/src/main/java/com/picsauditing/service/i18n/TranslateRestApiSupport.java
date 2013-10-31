package com.picsauditing.service.i18n;

import com.netflix.hystrix.*;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Map;

public abstract class TranslateRestApiSupport<R extends HystrixCommand<R>> {

    private static final Logger logger = LoggerFactory.getLogger(TranslateRestApiSupport.class);
    private static final int WEB_CONNECT_TIMEOUT_MS = 1000;
    private static final int WEB_READ_TIMEOUT_MS = 1000;
    private static final String TRANSLATION_URL =
            ((System.getProperty("translation.server") == null) ? "http://translate.picsorganizer.com" : System.getProperty("translation.server")) + "/api/";

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


    public TranslateRestApiSupport() {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TranslateApi"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)
                )
        );

    }

    ClientResponse makeServiceApiCall(String path) throws Exception {
        webResource.path(path);
        return webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
    }

    StringBuilder pathBase(String locale) {
        StringBuilder url = new StringBuilder(locale).append("/");
        return url;
    }

    TranslationWrapper failedResponseTranslation(String key, String locale, String responseStatus) {
        logger.error("Failed : HTTP error code : {}", responseStatus);
        return new TranslationWrapper.Builder()
                .key(key)
                .locale(locale)
                .translation(TranslationService.ERROR_STRING)
                .qualityRating(TranslationQualityRating.Bad)
                .build();
    }

    JSONObject parseJson(String jsonString) {
        return (JSONObject) JSONValue.parse(jsonString);
    }

    String actualLocaleFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return (String)((JSONObject)best).get("locale");
        } else {
            return (String) json.get("locale");
        }
    }

    String translationTextFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return (String)((JSONObject)best).get("value");
        } else {
            return (String) json.get("value");
        }
    }

    TranslationQualityRating qualityRatingFromJson(JSONObject json) {
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
