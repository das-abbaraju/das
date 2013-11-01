package com.picsauditing.service.i18n;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.models.database.TranslationUsage;
import com.sun.jersey.api.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

public class UpdateTranslationUsageLogCommand extends TranslateRestApiSupport<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateTranslationUsageLogCommand.class);
    private static final String LOG_URL = "logTranslationUsage/";
    private static ObjectMapper mapper = new ObjectMapper(); // thread-safe as long as we don't modify the configuration

    private final List<TranslationUsage> lookupData;

    public UpdateTranslationUsageLogCommand(List<TranslationUsage> lookupData) {
        super("TranslationUsageLogToTranslate");
        this.lookupData = lookupData;
    }

    @Override
    protected Boolean run() throws Exception {
        return doUpdateTranslationLog();
    }

    @Override
    protected Boolean getFallback() {
        return Boolean.FALSE;
    }

    private boolean doUpdateTranslationLog() {
        try {
            String jsonString = mapper.writeValueAsString(lookupData);
            ClientResponse response = webResource
                    .path(LOG_URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .type(MediaType.APPLICATION_JSON_VALUE)
                    .post(ClientResponse.class, jsonString);
            if (response == null || response.getStatus() != 200) {
                return false;
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to create json payload {}", e);
            return false;
        }
        return true;
    }

    public static void registerJsonMapper(ObjectMapper jsonMapper) {
        mapper = jsonMapper;
    }
}
