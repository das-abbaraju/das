package com.picsauditing.struts.controller;

import com.google.gson.Gson;
import com.picsauditing.access.Anonymous;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class TranslationJsonController extends JsonActionSupport {

    private String translationKey;
    private String language;
    private String isoCode;

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

    @Anonymous
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
        Locale locale = createLocale(language, isoCode);

        for (List<String> translationKeys : requestTranslations.values()) {
            for (String translationKey : translationKeys) {
                String translationValue = getTextWithLocaleIfProvided(translationKey, locale);
                translationsMap.put(translationKey, translationValue == null ? "" : translationValue);
            }
        }
        return translationsMap;
    }

    private Locale createLocale(String language, String isoCode) {
        if(Strings.isNotEmpty(language)) {
            if (Strings.isNotEmpty(isoCode)) {
                return new Locale(language, isoCode);
            }
            return new Locale(language);
        }
        return null;
    }

    private String getTextWithLocaleIfProvided(String translationKey, Locale locale) {
        if (locale == null) {
           return getText(translationKey);
        }
        return getText(locale, translationKey);
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

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
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
