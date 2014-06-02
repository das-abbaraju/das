package com.picsauditing.struts.controller;

import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class TranslationJsonController extends JsonActionSupport {

    private String translationKey;
    private String language;

    @Deprecated // Per Roos, not actually used by the front-end. Confirm and delete.
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
        HashMap<String, List<String>> translationKeys;
        try {
            translationKeys = getModelFromJsonRequest(HashMap.class);

            if (translationKeys.isEmpty()) {
                throw new UnacceptableJsonException();
            }
        } catch (IOException e) {
            return badRequestResponse();
        }

        Map translationsMap = generateTranslationsMap(translationKeys);

        jsonString = new Gson().toJson(translationsMap);
        return JSON_STRING;
    }

    private Map<String, String> generateTranslationsMap(HashMap<String, List<String>> requestTranslations) {
        Map<String, String> translationsMap = new HashMap<>();

        for (List<String> translationKeys : requestTranslations.values()) {
            for (String translationKey : translationKeys) {
                String translationValue = getTextWithLocaleIfProvided(translationKey);
                translationsMap.put(translationKey, translationValue == null ? "" : translationValue);
            }
        }
        return translationsMap;
    }

    private String getTextWithLocaleIfProvided(String translationKey) {
        if (StringUtils.isEmpty(language)) {
           return getText(translationKey);
        }
        return getText(new Locale(language), translationKey);
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
