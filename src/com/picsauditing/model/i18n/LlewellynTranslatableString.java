package com.picsauditing.model.i18n;


public class LlewellynTranslatableString {

	private String key;

	public LlewellynTranslatableString(String key) {
		this.key = key;
	}

	public String toTranslatedString() {
		return TranslatorFactory.getTranslator().translate(key, TranslatorFactory.getLocale());
	}

	@Override
	public String toString() {
		return key;
	}

}