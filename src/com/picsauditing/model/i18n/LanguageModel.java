package com.picsauditing.model.i18n;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Glossary:
 * <p/>
 * "Language Code" = ISO 639-1 Code is the 2-letter language code (e.g. en = English, fr = French).
 * <p/>
 * "Country Code" = ISO 3166-1 alpha-2 is the 2-letter country code (e.g. US = United States, GB = Great Britain).
 * <p/>
 * "Language Tag" = Internet Engineering Task Force (IETF) language tag = An IETF best practice,
 * currently specified by RFC 5646 and RFC 4647, for language tags easy to parse by computer.
 * The tag system is extensible to region, dialect, and private designations.
 * (e.g. en_US is the US dialect of English, and en_GB is the British dialect).
 * A valid language tag can be either just a 2-letter Language Code, or a 5-character LanguageCode + "_" + CountryCode
 * <p/>
 * "Locale" = the Java Locale object that corresponds to a language tag
 * <p/>
 * "Languages" = is how we'll refer to a list of languages that distinguishes between the various dialects. For
 * English, that would be "en_US", "en_GB", etc. ("en" alone is specifically excluded)
 * For Finnish, it would be just "fi", which has no dialects.
 * (This is the default behavior, unless we specifically use one of the terms below.)
 * <p/>
 * "Language Sans Dialect" = is how we'll refer to a language without concern for any particular dialect (either
 * because we are unconcerned about the dialect differences, or because there are no dialect differences) e.g. "en"
 * to refer to all dialects of English, and "fi" for Finnish, which has no dialects.
 * <p/>
 * "Unified Language List" = is how we'll refer to a merged list of the previous 2.
 */
public class LanguageModel {
	@Autowired
	private LanguageProvider languageProvider;

	private List<Locale> stableLanguageLocales;
	private List<Language> stableLanguages;
	private List<Locale> unifiedLanguageList;

	public boolean isLanguageStable(Locale locale) {
		for (Language language : getStableLanguages()) {
			if (language.getLanguage().equals(locale.getLanguage())) {
				return true;
			}
		}

		return false;
	}

	public List<KeyValue> getStableLanguagesSansDialect() {
		List<String> supportedLanguageKeys = extractLanguagesFromStableVariants();
		setFirstLanguageAsEnglish(supportedLanguageKeys);

		return languageAndDisplayAsKeyValues(supportedLanguageKeys);
	}

	/**
	 * A list of all languages that are stable, along with all of their variant dialects.
	 * Note: The language code alone will not appear in the list unless the language has no dialects at all.
	 * Note also that stability is a factor of the language alone.
	 * The dialects always follow the language itself af far as being stable is concerned.
	 */
	public List<Language> getStableLanguages() {
		if (stableLanguages == null) {
			stableLanguages = languageProvider.findByStatus(LanguageStatus.Stable);
		}

		if (stableLanguages == null || stableLanguages.isEmpty()) {
			addEnglishAsStableLanguage();
		}

		if (stableLanguages.size() > 0) {
			Collections.sort(stableLanguages);
		}

		return stableLanguages;
	}

	public List<Locale> getStableLanguageLocales() {
		if (stableLanguageLocales == null) {
			List<Language> stableLanguageVariants = getStableLanguages();
			stableLanguageLocales = new ArrayList<Locale>();

			for (Language stableLanguage : stableLanguageVariants) {
				stableLanguageLocales.add(stableLanguage.getLocale());
			}
		}

		return stableLanguageLocales;
	}

	public List<Locale> getStableAndBetaLanguageLocales() {
		List<Language> betaLanguages = getLanguagesByStatus(LanguageStatus.Beta);
		List<Locale> stableAndBetaLocales = getStableLanguageLocales();

		for (Language betaLanguage : betaLanguages) {
			stableAndBetaLocales.add(betaLanguage.getLocale());
		}

		return stableAndBetaLocales;
	}

	public List<Locale> getUnifiedLanguageList() {
		if (unifiedLanguageList == null) {
			unifiedLanguageList = new ArrayList<Locale>();
			Set<String> languagesWithVariants = new TreeSet<String>();

			for (Language stableLanguageWithVariant : getStableLanguages()) {
				unifiedLanguageList.add(stableLanguageWithVariant.getLocale());

				if (Strings.isNotEmpty(stableLanguageWithVariant.getCountry())) {
					languagesWithVariants.add(stableLanguageWithVariant.getLanguage());
				}
			}

			// Add beta languages
			for (Language betaLanguage : getLanguagesByStatus(LanguageStatus.Beta)) {
				unifiedLanguageList.add(betaLanguage.getLocale());

				if (Strings.isNotEmpty(betaLanguage.getCountry())) {
					languagesWithVariants.add(betaLanguage.getLanguage());
				}
			}

			for (String language : languagesWithVariants) {
				unifiedLanguageList.add(new Locale(language));
			}

			Collections.sort(unifiedLanguageList, new Comparator<Locale>() {
				@Override
				public int compare(Locale first, Locale second) {
					return first.toString().compareTo(second.toString());
				}
			});
		}

		return unifiedLanguageList;
	}

	public Locale getNearestStableLocale(Locale locale) {
		// Search if it's in our list of stable locales
		for (Locale stableLocale : getStableLanguageLocales()) {
			if (stableLocale.equals(locale)) {
				return locale;
			}
		}
		// Maybe this specific variant isn't supported?
		for (KeyValue languageValue : getStableLanguagesSansDialect()) {
			if (languageValue.getKey().equals(locale.getLanguage())) {
				return new Locale(languageValue.getKey());
			}
		}

		return Locale.ENGLISH;
	}

	public List<Language> getLanguagesByStatus(LanguageStatus languageStatus) {
		List<Language> languagesByStatus = languageProvider.findByStatus(languageStatus);

		if (languagesByStatus == null) {
			languagesByStatus = Collections.emptyList();
		}

		return languagesByStatus;
	}

	private List<String> extractLanguagesFromStableVariants() {
		List<String> supportedLanguageKeys = new ArrayList<String>();

		for (Language language : getStableLanguages()) {
			if (!supportedLanguageKeys.contains(language.getLanguage())) {
				supportedLanguageKeys.add(language.getLanguage());
			}
		}

		return supportedLanguageKeys;
	}

	private void setFirstLanguageAsEnglish(List<String> supportedLanguageKeys) {
		// TODO: Should English should be listed first? The assumption is yes
		if (supportedLanguageKeys.contains("en") && supportedLanguageKeys.indexOf("en") > 0) {
			supportedLanguageKeys.remove("en");
			supportedLanguageKeys.set(0, "en");
		}
	}

	private List<KeyValue> languageAndDisplayAsKeyValues(List<String> supportedLanguageKeys) {
		List<KeyValue> supportedLanguages = new ArrayList<KeyValue>();

		for (String languageKey : supportedLanguageKeys) {
			Locale locale = new Locale(languageKey);
			String displayLanguage = StringUtils.capitalize(locale.getDisplayLanguage(locale));

			supportedLanguages.add(new KeyValue(languageKey, displayLanguage));
		}

		return supportedLanguages;
	}

	private void addEnglishAsStableLanguage() {
		if (stableLanguages == null) {
			stableLanguages = new ArrayList<Language>();
		}

		Language english = new Language();
		english.setLocale(Locale.ENGLISH);
		english.setLanguage(Locale.ENGLISH.getLanguage());

		stableLanguages.add(english);
	}

	public class KeyValue {
		private String key;
		private String value;

		public KeyValue(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
