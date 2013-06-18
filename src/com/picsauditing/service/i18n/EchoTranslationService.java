package com.picsauditing.service.i18n;

import java.text.MessageFormat;
import java.util.Locale;


public class EchoTranslationService implements TranslatorService {

	@Override
	public String translate(String key, Locale locale) {
		return MessageFormat.format("Translate[{0}=>{1}]", key, locale);
	}

}
