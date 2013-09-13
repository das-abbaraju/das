package com.picsauditing.model.i18n.translation.strategy;

import com.picsauditing.model.i18n.TranslationWrapper;

public interface TranslationStrategy {

    TranslationWrapper transformTranslation(TranslationWrapper toTransform);

}
