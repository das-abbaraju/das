package com.picsauditing.model.i18n.translation.strategy;

import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.util.Strings;

public class EmptyTranslationStrategy implements TranslationStrategy {

    @Override
    public TranslationWrapper transformTranslation(TranslationWrapper translation) {
        if (shouldTransform(translation)) {
            return new TranslationWrapper.Builder(translation).translation(Strings.EMPTY_STRING).build();
        } else {
            return translation;
        }
    }

    private boolean shouldTransform(TranslationWrapper translation) {
        return Strings.isEmpty(translation.getTranslation()) ||
                translation.getKey().equals(translation.getTranslation()) ||
                TranslationQualityRating.Bad.equals(translation.getQualityRating());
    }

}
