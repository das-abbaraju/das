package com.picsauditing.model.i18n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public static final Locale ENGLISH = Locale.US;

	@Autowired
	private AppPropertyDAO propertyDAO;

	@Autowired
	private CountryDAO countryDAO;

	@Autowired
	private LanguageProvider languageProvider;

	private final Logger logger = LoggerFactory.getLogger(LanguageModel.class);

	private List<Locale> stableLanguageLocales;
	private List<Language> stableLanguages;
	private List<Locale> unifiedLanguageList;

	public Set<String> getLanguagesToEmailInvoicesInBPROCS() {
		return languagesToEmailInvoicesInBPROCS;
	}

	public void setLanguagesToEmailInvoicesInBPROCS(Set<String> languagesToEmailInvoicesInBPROCS) {
		this.languagesToEmailInvoicesInBPROCS = languagesToEmailInvoicesInBPROCS;
	}

	private Set<String> languagesToEmailInvoicesInBPROCS = new HashSet<String>();

	public void reloadListOfLanguagesToEmailInvoicesViaBPROCS() {
		AppProperty appProperty = propertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LANGUAGES_TO_EMAIL_VIA_BPROCS);

		String languagesToEmailInvoicesViaBPROCSCSV = null;
		if (appProperty != null) {
			languagesToEmailInvoicesViaBPROCSCSV = appProperty.getValue();
		}

		if (Strings.isNotEmpty(languagesToEmailInvoicesViaBPROCSCSV)) {
			languagesToEmailInvoicesInBPROCS = new HashSet<String>(Arrays.asList(languagesToEmailInvoicesViaBPROCSCSV.split("\\s*,\\s*")));
		}
	}

	public Boolean invoiceIsToBeEmailedViaBPROCS(ContractorAccount contractor) {

		if ((contractor == null) || (contractor.getLocale() == null)) {
			return false;
		}

		String languageToCheck = contractor.getLocale().getLanguage();

		if (languagesToEmailInvoicesInBPROCS.contains(languageToCheck)) {
			return true;
		} else {
			return false;
		}
	}

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

		return Collections.unmodifiableList(stableLanguages);
	}

	public List<Locale> getStableLanguageLocales() {
		if (stableLanguageLocales == null) {
			List<Language> stableLanguageVariants = getStableLanguages();
			stableLanguageLocales = new ArrayList<Locale>();

			for (Language stableLanguage : stableLanguageVariants) {
				stableLanguageLocales.add(stableLanguage.getLocale());
			}
		}
		//return stableLanguageLocales;
		return Collections.unmodifiableList(stableLanguageLocales);
	}

	public List<Locale> getStableAndBetaLanguageLocales() {
		List<Language> betaLanguages = getLanguagesByStatus(LanguageStatus.Beta);
		List<Locale> stableAndBetaLocales = new ArrayList<>();
		stableAndBetaLocales.addAll(getStableLanguageLocales());

		for (Language betaLanguage : betaLanguages) {
			stableAndBetaLocales.add(betaLanguage.getLocale());
		}

		return stableAndBetaLocales;
	}

	public List<Locale> getUnifiedLanguageList() {
		if (unifiedLanguageList == null) {
			unifiedLanguageList = new ArrayList<>();
			Set<String> languagesWithVariants = new TreeSet<>();

			for (Language stableLanguageWithVariant : getStableLanguages()) {
				if (!unifiedLanguageList.contains(stableLanguageWithVariant.getLocale())) {
					unifiedLanguageList.add(stableLanguageWithVariant.getLocale());
				}

				if (Strings.isNotEmpty(stableLanguageWithVariant.getCountry())) {
					languagesWithVariants.add(stableLanguageWithVariant.getLanguage());
				}
			}

			// Add beta languages
			for (Language betaLanguage : getLanguagesByStatus(LanguageStatus.Beta)) {
				if (!unifiedLanguageList.contains(betaLanguage.getLocale())) {
					unifiedLanguageList.add(betaLanguage.getLocale());
				}

				if (Strings.isNotEmpty(betaLanguage.getCountry())) {
					languagesWithVariants.add(betaLanguage.getLanguage());
				}
			}

			for (String language : languagesWithVariants) {
				Locale localeWithVariant = new Locale(language);
				if (!unifiedLanguageList.contains(localeWithVariant)) {
					unifiedLanguageList.add(localeWithVariant);
				}
			}

			Collections.sort(unifiedLanguageList, new Comparator<Locale>() {
				@Override
				public int compare(Locale first, Locale second) {
					return first.toString().compareTo(second.toString());
				}
			});
		}

		return Collections.unmodifiableList(unifiedLanguageList);
	}

	public Locale getNearestStableAndBetaLocale(Locale locale) {
		return getNearestStableAndBetaLocale(locale, null);
	}

	public Locale getNearestStableAndBetaLocale(Locale locale, String country) {
		Locale nearestStableLocale = getMatchingStableLocale(locale);

		if (nearestStableLocale == null && Strings.isNotEmpty(country)) {
			Locale localeWithCountry = new Locale(locale.getLanguage(), country);
			nearestStableLocale = getMatchingStableLocale(localeWithCountry);
		}

		if (nearestStableLocale == null) {
			nearestStableLocale = ENGLISH;
		}

		return nearestStableLocale;
	}

	public List<Language> getLanguagesByStatus(LanguageStatus languageStatus) {
		List<Language> languagesByStatus = languageProvider.findByStatus(languageStatus);

		if (languagesByStatus == null) {
			languagesByStatus = Collections.emptyList();
		}

		return languagesByStatus;
	}

	public List<Country> getDialectCountriesBasedOn(String language) {
		List<Language> dialects = languageProvider.findDialectsByLanguage(language);
		List<Country> countries = new ArrayList<Country>();

		for (Language dialect : dialects) {
			String countryIsoCode = dialect.getCountry();

			if (countryIsoCode != null) {
				countries.add(countryDAO.findbyISO(countryIsoCode));
			}
		}

		return countries;
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
			supportedLanguageKeys.add(0, "en");
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
		english.setLocale(ENGLISH);
		english.setLanguage(ENGLISH.getLanguage());
		english.setCountry(ENGLISH.getCountry());

		stableLanguages.add(english);
	}

	private Locale getMatchingStableLocale(Locale locale) {
		List<Locale> stableAndBetaLanguageLocales = getStableAndBetaLanguageLocales();
		for (Locale stableLocale : stableAndBetaLanguageLocales) {
			if (stableLocale == null) {
				logger.error("Null locale found in Stable and Beta locales list: "
						+ Strings.implode(stableAndBetaLanguageLocales));
				continue;
			}
			if (stableLocale.equals(locale)) {
				return locale;
			}
		}

		return null;
	}
}
