package com.picsauditing.i18n.service.commands;

import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AllLocalesForKeyCommand extends TranslateRestApiSupport<List<String>> {
    private static final Logger logger = LoggerFactory.getLogger(AllLocalesForKeyCommand.class);
    private static final String LOCALES_URL = "locales/";
    private final String key;

    public AllLocalesForKeyCommand(String key) {
        super("TranslateOtherAPI");
        this.key = key;
    }

    @Override
    protected List<String> run() throws Exception {
        return allLocalesForKey();
    }

    @Override
    protected List<String> getFallback() {
        return new JSONArray();
    }

    private List<String> allLocalesForKey() throws Exception {
        try {
            ClientResponse response = makeServiceApiCall(getLocalesPath(key));
            if (response == null || response.getStatus() != 200) {
                logger.error("Failed : HTTP error code : {}", response.getStatus());
                throw new Exception("failed to get a 200 web response");
            } else {
                JSONObject json = parseJson(response.getEntity(String.class));
                return (JSONArray) json.get("locales");
            }
        } catch (Exception e) {
            // catching so we can log the exception
            logger.error("there was an exception getting all locales for key {}: {}", key, e.getMessage());
            // rethrowing so that the fallback will be triggered
            throw e;
        }
    }

    private String getLocalesPath(String key) {
        return new StringBuilder(LOCALES_URL).append(key).toString();
    }

}
