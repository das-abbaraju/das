package com.picsauditing.i18n.service.commands;

import com.netflix.hystrix.*;
import com.picsauditing.i18n.model.TranslationQualityRating;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.picsauditing.i18n.service.TranslationService;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class TranslateRestApiSupport<R> extends HystrixCommand<R> {
    private static final Logger logger = LoggerFactory.getLogger(TranslateRestApiSupport.class);
    private static final int WEB_CONNECT_TIMEOUT_MS = 1000;
    private static final int WEB_READ_TIMEOUT_MS = 1000;
    private static final String TRANSLATION_URL =
            ((System.getProperty("translation.server") == null) ? "http://translate.picsorganizer.com" : System.getProperty("translation.server")) + "/api/";

    private static Client client;
    static WebResource webResource;
    static final String APPLICATION_JSON_VALUE = "application/json";

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

    /*
        I have chosen to use semaphore isolation for this command group despite it being a network call because this will be
        a very copious call, there's really no useful fallback at this time (though if we ever have a distributed cache to
        fall back on, that could be enough to change this)
     */
    public TranslateRestApiSupport(String commandGroup, String commandKey) {
        super(hystrixSetter(commandGroup, commandKey));
    }

    public TranslateRestApiSupport(String commandGroup) {
        super(hystrixSetter(commandGroup, null));

    }

    private static Setter hystrixSetter(String commandGroup, String commandKey) {
        Setter setter = HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroup))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)
                );
        // set the commandKey if we're specifying, otherwise just use the default (currently classname of the command)
        if (commandKey != null) {
            setter.andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
        }
        return setter;
    }
    ClientResponse makeServiceApiCall(String path) throws Exception {
        return webResource.path(path).accept(APPLICATION_JSON_VALUE).get(ClientResponse.class);
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
                .requestedLocale(locale)
                .translation(TranslationService.ERROR_STRING)
                .qualityRating(TranslationQualityRating.Bad)
                .build();
    }

    JSONObject parseJson(String jsonString) {
        return (JSONObject) JSONValue.parse(jsonString);
    }

    JSONArray parseJsonArray(String jsonString) {
        return (JSONArray) JSONValue.parse(jsonString);
    }

    String keyFromJson(JSONObject json) {
        Object best = json.get("best");
        if (best instanceof JSONObject) {
            return (String)((JSONObject)best).get("key");
        } else {
            return (String) json.get("key");
        }
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

    public static void registerWebClient(Client webclient) {
        client = webclient;
        webResource = client.resource(TRANSLATION_URL);
    }

}
