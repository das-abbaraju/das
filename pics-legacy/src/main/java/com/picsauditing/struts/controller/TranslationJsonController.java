package com.picsauditing.struts.controller;

import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationJsonController extends JsonActionSupport {

    private String translationKey;

    public String getSingleTranslationMessage() {
        Map<String, String> translationsMap = new HashedMap();
        String translationMessage = getText(translationKey);
        translationsMap.put(translationKey, translationMessage == null ? "" : translationMessage);

        Translations responseTranslations = new Translations();
        responseTranslations.setTranslationsMap(translationsMap);

        jsonString = new Gson().toJson(responseTranslations);

        return JSON_STRING;
    }

    public String getTranslationMessages() {
        Translations requestTranslations;
        try {
            requestTranslations = getModelFromJsonRequest(Translations.class);

            if (requestTranslations.getTranslationKeys() == null) {
                throw new UnacceptableJsonException();
            }
        } catch (IOException e) {
            return badRequestResponse();
        }

        Map<String, String> translationsMap = generateTranslationsMap(requestTranslations);


        Translations responseTranslations = new Translations();
        responseTranslations.setTranslationsMap(translationsMap);

        jsonString = new Gson().toJson(responseTranslations);
        return JSON_STRING;
    }

    private Map<String, String> generateTranslationsMap(Translations requestTranslations) {
        Map<String, String> translationsMap = new HashMap<>();
        for (String translationKey : requestTranslations.getTranslationKeys()) {
            String translationMessage = getText(translationKey);
            translationsMap.put(translationKey, translationMessage == null ? "" : translationMessage);
        }
        return translationsMap;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    private class Translations {
        List<String> translationKeys;
        private Map<String, String> translationsMap;

        public List<String> getTranslationKeys() {
            return translationKeys;
        }

        public void setTranslationKeys(List<String> translationKeys) {
            this.translationKeys = translationKeys;
        }

        public void setTranslationsMap(Map<String, String> translationsMap) {
            this.translationsMap = translationsMap;
        }

        public Map<String, String> getTranslationsMap() {
            return translationsMap;
        }
    }

}
