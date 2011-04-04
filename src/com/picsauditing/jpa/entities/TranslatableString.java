package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.util.Strings;

public class TranslatableString {

	private Map<String, Translation> translations = new HashMap<String, Translation>();

	public void putTranslation(String locale, String translation) {
		putTranslation(locale, translation, false);
	}

	public void putTranslation(String locale, String translation, boolean insert) {
		Translation t = new Translation(translation);
		t.setInsert(insert);
		this.translations.put(locale, t);
	}

	public void modifyTranslation(String locale, String translation) {
		Translation t = new Translation(translation);
		t.setModified(true);
		translations.put("locale", t);
	}

	public void putTranslations(Map<String, String> translations) {
		putTranslations(translations, false);
	}

	public void putTranslations(Map<String, String> translations, boolean insert) {
		for (Map.Entry<String, String> translation : translations.entrySet()) {
			putTranslation(translation.getKey(), translation.getValue(), insert);
		}
	}

	public void deleteTranslation(String locale) {
		translations.get(locale).setDelete(true);
	}

	public boolean isModified(String locale) {
		return translations.get(locale).isModified();
	}

	public boolean hasTranslation(String locale) {
		return translations.containsKey(locale);
	}

	public void handleTranslation(Locale locale, String translation) {

		if (Strings.isEmpty(translation)) {
			// Delete the translation
			if (hasTranslation(locale.toString())) {
				// i.e. Delete fr_CA
				deleteTranslation(locale.toString());
			} else if (hasTranslation(locale.getLanguage())) {
				// i.e. Delete fr
				deleteTranslation(locale.getLanguage());
			} else {
				deleteTranslation(Locale.ENGLISH.toString());
			}
		} else {
			if (translation.equals(translations.get(Locale.ENGLISH.toString()).getValue())) {
				// ignore this value
			} else {
				if (hasTranslation(locale.getLanguage())
						&& translation.equals(translations.get(locale.getLanguage()).getValue())) {
					// ignore values that are the same
				} else {
					boolean insert = !hasTranslation(locale.toString());
					putTranslation(locale.toString(), translation, insert);
				}
			}
		}
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

		return translations.get(getLocale(locale)).getValue();
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

	public class Translation {
		private String value;
		private boolean modified = false;
		private boolean insert = false;
		private boolean delete = false;

		public Translation(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isModified() {
			return modified;
		}

		public void setModified(boolean modified) {
			this.modified = modified;
		}

		public boolean isInsert() {
			return insert;
		}

		public void setInsert(boolean insert) {
			this.insert = insert;
		}

		public boolean isDelete() {
			return delete;
		}

		public void setDelete(boolean delete) {
			this.delete = delete;
		}
	}
}
