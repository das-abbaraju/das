package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TranslateWildcardCommand extends TranslateRestApiSupport<List<TranslationWrapper>> {
    private static final Logger logger = LoggerFactory.getLogger(TranslateWildcardCommand.class);
    private final String key;
    private final String requestedLocale;

    public TranslateWildcardCommand(String key, String requestedLocale) {
        super("TranslateKey");
        this.key = key;
        this.requestedLocale = requestedLocale;
    }

    @Override
    protected List<TranslationWrapper> run() throws Exception {
        return translationsFromWebResourceByWildcard();
    }

    @Override
    protected List<TranslationWrapper> getFallback() {
        List<TranslationWrapper> translations = new ArrayList<>();
        translations.add(failedResponseTranslation(key, requestedLocale, null));
        return translations;
    }

    private List<TranslationWrapper> translationsFromWebResourceByWildcard() throws Exception {
        List<TranslationWrapper> translations = new ArrayList<>();
        ClientResponse response = makeServiceApiCall(getTranslationLikePath(key, requestedLocale));
        if (response == null) {
            translations.add(failedResponseTranslation(key, requestedLocale, null));
        } else if (response.getStatus() != 200) {
            translations.add(failedResponseTranslation(key, requestedLocale, String.valueOf(response)));
        } else {
            JSONArray json = parseJsonArray(response.getEntity(String.class));
            for (Object jsonObject : json) {
                translations.add(new TranslationWrapper.Builder()
                        .key(keyFromJson(((JSONObject) jsonObject)))
                        .locale(actualLocaleFromJson(((JSONObject) jsonObject)))
                        .requestedLocale(requestedLocale)
                        .translation(translationTextFromJson(((JSONObject) jsonObject)))
                        .qualityRating(qualityRatingFromJson(((JSONObject) jsonObject)))
                        .retrievedByWildcard(true)
                        .build());
            }
        }
        return translations;
    }

    private String getTranslationLikePath(String key, String locale) {
        return pathBase(locale).append("like/").append(key).append("%25").toString();
    }

}
