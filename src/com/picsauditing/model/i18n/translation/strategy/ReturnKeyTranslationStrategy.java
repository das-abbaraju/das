package com.picsauditing.model.i18n.translation.strategy;

import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.util.Strings;

public class ReturnKeyTranslationStrategy implements TranslationStrategy {

    @Override
    public TranslationWrapper transformTranslation(TranslationWrapper translation) {
        if (Strings.isEmpty(translation.getTranslation())) {
            return new TranslationWrapper.Builder(translation).translation(translation.getKey()).build();
        } else {
            return translation;
        }
    }

}
