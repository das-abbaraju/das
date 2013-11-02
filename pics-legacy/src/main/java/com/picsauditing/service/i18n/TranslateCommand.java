package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslateCommand extends TranslateRestApiSupport<TranslationWrapper> {
    private static final Logger logger = LoggerFactory.getLogger(TranslateCommand.class);
    private final String key;
    private final String requestedLocale;

    public TranslateCommand(String key, String requestedLocale) {
        super("TranslateKey");
        this.key = key;
        this.requestedLocale = requestedLocale;
    }

    @Override
    protected TranslationWrapper run() throws Exception {
        return translationFromWebResource();
    }

    @Override
    protected TranslationWrapper getFallback() {
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
