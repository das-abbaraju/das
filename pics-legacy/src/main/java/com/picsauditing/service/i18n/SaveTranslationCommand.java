package com.picsauditing.service.i18n;

import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;

import java.util.List;

public class SaveTranslationCommand extends TranslateRestApiSupport<Boolean> {
    private static final String UPDATE_URL = "saveOrUpdate/";

    private final String key;
    private final String translation;
    private final List<String> requiredLanguages;

    public SaveTranslationCommand(String key, String translation, List<String> requiredLanguages) {
        super("SaveTranslation");
        this.key = key;
        this.translation = translation;
        this.requiredLanguages = requiredLanguages;
    }

    @Override
    protected Boolean run() throws Exception {
        return saveTranslation();
    }

    @Override
    protected Boolean getFallback() {
        return Boolean.FALSE;
    }

    boolean saveTranslation() {
        JSONObject formData = new JSONObject();
        formData.put("key", key);
        formData.put("value", translation);
        formData.put("qualityRating", TranslationService.QUALITY_GOOD);
        for (String locale : requiredLanguages) {
            formData.remove("locale");
            formData.put("locale", locale);
            ClientResponse response = webResource
                    .path(UPDATE_URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .type(MediaType.APPLICATION_JSON_VALUE)
                    .post(ClientResponse.class, formData.toJSONString());
            if (response == null || response.getStatus() != 200) {
                return false;
            }
        }
        return true;
    }

}
