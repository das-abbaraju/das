package com.picsauditing.model.i18n.translation.strategy;

import java.util.Locale;

public interface TranslationStrategy {

	String performTranslation(String key, Locale locale);

}
