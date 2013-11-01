package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.models.database.TranslationUsage;

import java.util.List;

public class TranslateRestClient {
    // private static final Logger logger = LoggerFactory.getLogger(TranslateRestClient.class);

    // for test injection only
    private TranslateCommand translateCommand;
    private TranslateWildcardCommand translateWildcardCommand;
    private SaveTranslationCommand saveTranslationCommand;
    private AllLocalesForKeyCommand allLocalesForKeyCommand;
    private UpdateTranslationUsageLogCommand updateTranslationUsageLogCommand;

    public TranslationWrapper translationFromWebResource(String key, String requestedLocale) {
        TranslateCommand command = translateCommand(key, requestedLocale);
        return command.execute();
    }

    private TranslateCommand translateCommand(String key, String requestedLocale) {
        if (translateCommand != null) {
            return translateCommand;
        } else {
            return new TranslateCommand(key, requestedLocale);
        }
    }

    public List<TranslationWrapper> translationsFromWebResourceByWildcard(String key, String requestedLocale) {
        TranslateWildcardCommand command = translateWildcardCommand(key, requestedLocale);
        return command.execute();
    }

    private TranslateWildcardCommand translateWildcardCommand(String key, String requestedLocale) {
        if (translateWildcardCommand != null) {
            return translateWildcardCommand;
        } else {
            return new TranslateWildcardCommand(key, requestedLocale);
        }
    }

    public Boolean saveTranslation(String key, String translation, List<String> requiredLanguages) {
        SaveTranslationCommand command = saveTranslationCommand(key, translation, requiredLanguages);
        return command.execute();
    }

    private SaveTranslationCommand saveTranslationCommand(String key, String requestedLocale, List<String> requiredLanguages) {
        if (saveTranslationCommand != null) {
            return saveTranslationCommand;
        } else {
            return new SaveTranslationCommand(key, requestedLocale, requiredLanguages);
        }
    }

    public Boolean updateTranslationLog(List<TranslationUsage> lookupData) {
        UpdateTranslationUsageLogCommand command = updateTranslationUsageLogCommand(lookupData);
        return command.execute();
    }

    private UpdateTranslationUsageLogCommand updateTranslationUsageLogCommand(List<TranslationUsage> lookupData) {
        if (updateTranslationUsageLogCommand != null ) {
            return updateTranslationUsageLogCommand;
        } else {
            return new UpdateTranslationUsageLogCommand(lookupData);
        }
    }

    public List<String> allLocalesForKey(String key) {
        AllLocalesForKeyCommand command = allLocalesForKeyCommand(key);
        return command.execute();
    }

    private AllLocalesForKeyCommand allLocalesForKeyCommand(String key) {
        if (allLocalesForKeyCommand != null) {
            return allLocalesForKeyCommand;
        } else {
            return new AllLocalesForKeyCommand(key);
        }
    }

}
