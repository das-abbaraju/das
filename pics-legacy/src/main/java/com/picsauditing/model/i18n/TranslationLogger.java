package com.picsauditing.model.i18n;

import com.picsauditing.service.i18n.TranslateRestClient;
import org.springframework.beans.factory.annotation.Autowired;

public class TranslationLogger {
    // private Logger logger = LoggerFactory.getLogger(TranslationLogger.class);

    @Autowired
    private TranslateRestClient translateRestClient;

    public void handle(TranslationLookupData lookupData) throws Exception {
        if (!translateRestClient.updateTranslationLog(lookupData)) {
            throw new Exception("Unable to update log");
        };
    }

}