package com.picsauditing.jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.util.Strings;

public class TranslatableString implements Comparable<TranslatableString>, Serializable {

	private static final long serialVersionUID = 782714396254144725L;

	private Map<String, Translation> translations = new HashMap<String, Translation>();
	private String key;

	public Collection<Translation> getTranslations() {
		return translations.values();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void putTranslation(String locale, String translation, boolean insert) {
		Translation t = new Translation(locale, translation);
		t.insert = insert;
		translations.put(locale, t);
	}

	/**
	 * Insert new or modify existing
	 * 
	 * @param locale
	 * @param value
	 */
	private void postTranslation(String locale, String value) {
		Translation t;
		if (translations.containsKey(locale)) {
			t = translations.get(locale);
			if (value.equals(t.getValue()))
				return;
			t.value = value;
			t.modified = true;
		} else {
			t = new Translation(locale, value);
			t.insert = true;
		}
		translations.put(locale, t);
	}

	private void deleteTranslation(String locale) {
		if (translations.containsKey(locale))
			translations.get(locale).delete = true;
	}

	private boolean hasTranslation(String locale) {
		return translations.containsKey(locale);
	}

	public void handleTranslation(Locale locale, String newValue) {
		// Use these variables to improve readability
		String en_US = locale.toString();
		String en = locale.getLanguage();

		if (en.equals(en_US)) {
			// We aren't trying to save a country specific like "en_US"
			// This is the easiest example, just save the language "en"
			if (Strings.isEmpty(newValue))
				deleteTranslation(en);
			else
				postTranslation(en, newValue);
		} else {
			// When we're saving a country specific language, it's more complex
			if (Strings.isEmpty(newValue)) {
				// If we are clearing out the field, we can just erase the
				// translation value.
				if (hasTranslation(en_US)) {
					deleteTranslation(en_US);
				} else {
					// The only problem here is that if we clear out a French
					// translation, the English will keep coming up
					deleteTranslation(en);
				}
			} else {
				if (hasTranslation(en_US)) {
					if (translations.get(en).getValue().equals(newValue)) {
						// Delete the country specific, since this can now fall
						// back to the base language version
						deleteTranslation(en_US);
					} else {
						postTranslation(en_US, newValue);
					}
				} else {
					postTranslation(en, newValue);
				}
			}
		}
	}

	/**
	 * Using the current Locale, return the best match
	 */
	@Override
	public String toString() {
		return toString(null);
	}

	public String toString(Locale defaultLocale) {
		String localeCodeForBestTranslation = getLocale(defaultLocale);
		if (translations.containsKey(localeCodeForBestTranslation))
			return translations.get(localeCodeForBestTranslation).getValue();
		else
			return key;
	}

	public boolean isExists() {
		String translation = toString();

		if (translation == null) {
			return false;
		}

		translation = translation.trim();

		return translation.length() > 0
				&& !translation.toLowerCase().equals(I18nCache.DEFAULT_TRANSLATION.toLowerCase())
				&& !translation.equals(key);
	}

	/**
	 * Strip Tags
	 * 
	 * Strips HTML/XML tags from translated string
	 * 
	 * @return
	 */
	public String getStripTags() {
		return Jsoup.parse(toString()).body().text();
	}

	@Override
	public int compareTo(TranslatableString o) {
		return this.toString().compareTo(o.toString());
	}

	/**
	 * Determines the most appropriate locale code for the current locale (as
	 * defiend by the ActionContext) with respect to the current set of
	 * translations available for this TranslatableString. For example, if the
	 * current locale is fr_CA, but there is not a Canada-specific translation
	 * available, but there is a general French (fr) translataion available,
	 * then this returns "fr". If there is not a French translataion available,
	 * either, then it checks for English (en). And, if there is not a English
	 * translataion available, then it randomly returns the code for one of
	 * whatever translatsiona re available.
	 * 
	 * TODO Rename this pair of methods. For one thing, methods that are not
	 * get-accessors should not begin with "get". For another, they're not
	 * returning a Locale object, nor even a locale key. They're returning the
	 * index ID for a translation. So, a better name might be
	 * determineBestTranslationForLocale()
	 * 
	 * 
	 */
	public String getLocale() {
		return getLocale(null);
	}

	public String getLocale(Locale defaultLocale) {
		Locale locale;
		try {
			locale = ActionContext.getContext().getLocale();
		} catch (Exception e) {
			if (defaultLocale != null) {
				locale = defaultLocale;
			} else {
				Logger logger = LoggerFactory.getLogger(TranslatableString.class);
				logger.warn("Warning: Failed to get Locale from ActionContext.  Defaulting to Locale.ENGLISH");
				locale = Locale.ENGLISH;
			}
		}

		if (translations.containsKey(locale.toString())) {
			return locale.toString();
		}

		if (translations.containsKey(locale.getLanguage())) {
			return locale.getLanguage();
		}

		if (translations.containsKey(Locale.ENGLISH.getLanguage())) {
			return Locale.ENGLISH.getLanguage();
		}

		for (String anyLocale : translations.keySet()) {
			return anyLocale;
		}

		return null;
	}

	public class Translation implements Serializable {

		private static final long serialVersionUID = 4991157182068077703L;

		private String locale;
		private String value;
		private boolean modified = false;
		private boolean insert = false;
		private boolean delete = false;

		public Translation(String locale, String value) {
			this.locale = locale;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public boolean isModified() {
			return modified;
		}

		public boolean isInsert() {
			return insert;
		}

		public boolean isDelete() {
			return delete;
		}

		public String getLocale() {
			return locale;
		}

		public void commit() {
			delete = false;
			modified = false;
			insert = false;
		}
	}

}
