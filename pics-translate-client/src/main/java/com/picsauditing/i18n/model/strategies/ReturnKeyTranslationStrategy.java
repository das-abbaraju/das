package com.picsauditing.i18n.model.strategies;

import com.picsauditing.i18n.model.TranslationQualityRating;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.picsauditing.i18n.util.Strings;

public class ReturnKeyTranslationStrategy implements TranslationStrategy {

    @Override
    public TranslationWrapper transformTranslation(TranslationWrapper translation) {
        if (shouldTransform(translation)) {
            return new TranslationWrapper.Builder(translation).translation(translation.getKey()).build();
        } else {
            return translation;
        }
    }

    private boolean shouldTransform(TranslationWrapper translation) {
        return Strings.isEmpty(translation.getTranslation()) ||
                TranslationQualityRating.Bad.equals(translation.getQualityRating());
    }

}
