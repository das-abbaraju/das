package com.picsauditing.i18n.model.strategies;

import com.picsauditing.i18n.model.TranslationWrapper;

public interface TranslationStrategy {

    TranslationWrapper transformTranslation(TranslationWrapper toTransform);

}
