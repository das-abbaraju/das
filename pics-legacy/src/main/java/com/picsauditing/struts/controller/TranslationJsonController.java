package com.picsauditing.struts.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.picsauditing.actions.PicsActionSupport;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.httpclient.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationJsonController extends PicsActionSupport {

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

    public String getTranslationMessages() throws IOException {
        Translations requestTranslations = getModelFromJsonRequest();

        Map<String, String> translationsMap = new HashMap<>();

        for (String translationKey : requestTranslations.getTranslationKeys()) {
            String translationMessage = getText(translationKey);
            translationsMap.put(translationKey, translationMessage == null ? "" : translationMessage);
        }

        Translations responseTranslations = new Translations();
        responseTranslations.setTranslationsMap(translationsMap);

        jsonString = new Gson().toJson(responseTranslations);
        return JSON_STRING;
    }

    protected Translations getModelFromJsonRequest() throws IOException {
        String body = getBodyFromRequest();

        Translations translations = new Translations();

        try {
            translations = new Gson().fromJson(body, Translations.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST) + ": " + body, e);
        }

        return translations;
    }

    private String getBodyFromRequest() throws IOException {
        HttpServletRequest request = getRequest();
        return getBody(request);
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return stringBuilder.toString();
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public class Translations {
        List<String> translationKeys;
        private Map<String, String> translationsMap;

        public List<String> getTranslationKeys() {
            return translationKeys;
        }

        public void setTranslationKeys(List<String> translationKeys) {
            this.translationKeys = translationKeys;
        }

        public void setTranslationsMap(Map<String,String> translationsMap) {
            this.translationsMap = translationsMap;
        }

        public Map<String, String> getTranslationsMap() {
            return translationsMap;
        }
    }

}
