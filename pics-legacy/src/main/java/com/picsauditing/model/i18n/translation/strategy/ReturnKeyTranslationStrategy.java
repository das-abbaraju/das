package com.picsauditing.model.i18n.translation.strategy;

import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.util.Strings;

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
