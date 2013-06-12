package com.picsauditing.model.i18n;

import com.picsauditing.service.i18n.TranslationServiceFactory;

public class TranslatableString {

	private String key;

	public TranslatableString(String key) {
		this.key = key;
	}

	public String toTranslatedString() {
		return TranslationServiceFactory.getTranslationService().getText(key, TranslationServiceFactory.getLocale());
	}

	@Override
	public String toString() {
		return key;
	}

}