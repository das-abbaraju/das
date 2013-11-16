package com.picsauditing.i18n.service.commands;

import com.picsauditing.i18n.model.TranslationWrapper;
import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslateCommand extends TranslateRestApiSupport<TranslationWrapper> {
    private static final Logger logger = LoggerFactory.getLogger(TranslateCommand.class);
    private static final String COMMAND_GROUP = "TranslateKey";
    private final String key;
    private final String requestedLocale;

    public TranslateCommand(String key, String requestedLocale) {
        super(COMMAND_GROUP);
        this.key = key;
        this.requestedLocale = requestedLocale;
    }

    public TranslateCommand(String key, String requestedLocale, String commandKey) {
        super(COMMAND_GROUP, commandKey);
        this.key = key;
        this.requestedLocale = requestedLocale;
    }

    @Override
    protected TranslationWrapper run() throws Exception {
        return translationFromWebResource();
    }

    @Override
    protected TranslationWrapper getFallback() {
        logger.debug("Fallback was triggered");
        return failedResponseTranslation(key, requestedLocale, null);
    }

    private TranslationWrapper translationFromWebResource() throws Exception {
        TranslationWrapper translation;
        ClientResponse response = makeServiceApiCall(getTranslationPath(key, requestedLocale));
        if (response == null) {
            translation = failedResponseTranslation(key, requestedLocale, null);
        } else if (response.getStatus() != 200) {
            translation = failedResponseTranslation(key, requestedLocale, String.valueOf(response.getStatus()));
        } else {
            JSONObject json = parseJson(response.getEntity(String.class));
            translation = new TranslationWrapper.Builder()
                    .key(key)
                    .locale(actualLocaleFromJson(json))
                    .requestedLocale(requestedLocale)
                    .translation(translationTextFromJson(json))
                    .qualityRating(qualityRatingFromJson(json))
                    .build();
        }
        return translation;
    }

    private String getTranslationPath(String key, String locale) {
        return pathBase(locale).append(key).toString();
    }

}
