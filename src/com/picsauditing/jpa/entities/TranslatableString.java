package com.picsauditing.jpa.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;

public class TranslatableString {

	private Map<String, String> translations = new HashMap<String, String>();
	private Map<String, Boolean> modified = new HashMap<String, Boolean>();

	public void putTranslation(String locale, String translation) {
		this.translations.put(locale, translation);
		this.modified.put(locale, false);
	}

	public void modifyTranslation(String locale, String translation) {
		this.translations.put(locale, translation);
		this.modified.put(locale, true);
	}

	public void putTranslations(Map<String, String> translations) {
		for (Map.Entry<String, String> translation : translations.entrySet()) {
			this.translations.put(translation.getKey(), translation.getValue());
			this.modified.put(translation.getKey(), false);
		}
	}

	public void putTranslations(List<AppTranslation> translations) {
		for (AppTranslation translation : translations) {
			this.translations.put(translation.getLocale(), translation.getValue());
		}
	}

	public boolean isModified(String locale) {
		return modified.get(locale);
	}

	/**
	 * Using the current Locale, return the best match
	 */
	@Override
	public String toString() {
		Locale locale;
		try {
			locale = ActionContext.getContext().getLocale();
		} catch (Exception e) {
			locale = Locale.ENGLISH;
		}

		return translations.get(getLocale(locale));
	}

	private String getLocale(Locale locale) {
		if (translations.containsKey(locale.toString())) {
			return locale.toString();
		}
		if (translations.containsKey(locale.getLanguage())) {
			return locale.getLanguage();
		}
		return Locale.ENGLISH.toString();
	}

	public boolean isSet() {
		return translations.size() > 0;
	}

	public Collection<String> values() {
		return translations.values();
	}
}
