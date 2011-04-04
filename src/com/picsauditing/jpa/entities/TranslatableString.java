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
		// Use these variables to improve readability
		String fr_CA = locale.toString();
		String fr = locale.getLanguage();
		String en = Locale.ENGLISH.toString();

		if (Strings.isEmpty(translation)) {
			// Delete the translation
			if (hasTranslation(fr_CA)) {
				deleteTranslation(fr_CA);
			} else if (hasTranslation(fr)) {
				deleteTranslation(fr);
			} else {
				deleteTranslation(en);
			}
		} else {
			if (translation.equals(translations.get(en).getValue())) {
				// ignore this value
			} else {
				if (hasTranslation(fr_CA)
						&& translation.equals(translations.get(fr_CA).getValue())) {
					// ignore values that are the same
				} else {
					boolean insert = !hasTranslation(fr_CA);
					putTranslation(fr_CA, translation, insert);
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
