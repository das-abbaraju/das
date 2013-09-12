package com.picsauditing.model.i18n.translation.strategy;

import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.util.Strings;

public class EmptyTranslationStrategy implements TranslationStrategy {

    @Override
    public TranslationWrapper transformTranslation(TranslationWrapper translation) {
        if (Strings.isEmpty(translation.getTranslation()) || translation.getKey().equals(translation.getTranslation())) {
            return new TranslationWrapper.Builder(translation).translation(Strings.EMPTY_STRING).build();
        } else {
            return translation;
        }
    }

}
