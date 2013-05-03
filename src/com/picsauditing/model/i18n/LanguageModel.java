package com.picsauditing.model.i18n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.util.system.PicsEnvironment;
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

	private Set<Language> visibleLanguages;
	private Set<Locale> unifiedLanguageList;

	private PicsEnvironment picsEnvironment;

	public Set<String> getLanguagesToEmailInvoicesInBPROCS() {
		return languagesToEmailInvoicesInBPROCS;
	}

	public void setLanguagesToEmailInvoicesInBPROCS(Set<String> languagesToEmailInvoicesInBPROCS) {
		this.languagesToEmailInvoicesInBPROCS = languagesToEmailInvoicesInBPROCS;
	}

	private Set<String> languagesToEmailInvoicesInBPROCS = new HashSet<>();

	public void reloadListOfLanguagesToEmailInvoicesViaBPROCS() {
		AppProperty appProperty = propertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LANGUAGES_TO_EMAIL_VIA_BPROCS);

		String languagesToEmailInvoicesViaBPROCSCSV = null;
		if (appProperty != null) {
			languagesToEmailInvoicesViaBPROCSCSV = appProperty.getValue();
		}

		if (Strings.isNotEmpty(languagesToEmailInvoicesViaBPROCSCSV)) {
			languagesToEmailInvoicesInBPROCS = new HashSet<>(Arrays.asList(languagesToEmailInvoicesViaBPROCSCSV.split("\\s*,\\s*")));
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

	public boolean isLanguageVisible(Locale locale) {
		for (Language language : getVisibleLanguages()) {
			if (language.getLanguage().equals(locale.getLanguage())) {
				return true;
			}
		}

		return false;
	}

	public List<KeyValue> getVisibleLanguagesSansDialect() {
		List<String> supportedLanguageKeys = extractLanguageIsoCodesFrom(getVisibleLanguages());
		setFirstLanguageAsEnglish(supportedLanguageKeys);

		return languageAndDisplayAsKeyValues(supportedLanguageKeys);
	}

	/**
	 * A list of all languages that are stable, along with all of their variant dialects.
	 * Note: The language code alone will not appear in the list unless the language has no dialects at all.
	 * Note also that stability is a factor of the language alone.
	 * The dialects always follow the language itself af far as being stable is concerned.
	 */
	public Set<Language> getVisibleLanguages() {
		if (visibleLanguages == null) {
			visibleLanguages = new TreeSet<>();
			// Always add stable and beta languages
			List<Language> stableAndBetaLanguages = languageProvider.findByStatuses(new LanguageStatus[]{
					LanguageStatus.Stable, LanguageStatus.Beta
			});

			if (stableAndBetaLanguages != null) {
				visibleLanguages.addAll(stableAndBetaLanguages);
			}
		}

		if (visibleLanguages.isEmpty()) {
			addEnglishAsVisibleLanguage();
		}

		if (picsEnvironment().isShowAlphaLanguages()) {
			List<Language> alphaLanguages = languageProvider.findByStatus(LanguageStatus.Alpha);
			if (alphaLanguages != null) {
				visibleLanguages.addAll(alphaLanguages);
			}
		}

		return Collections.unmodifiableSet(visibleLanguages);
	}

	public Set<Locale> getVisibleLocales() {
		Set<Locale> visibleLocales = new TreeSet<>(new ByLanguageTag());

		for (Language visibleLanguage : getVisibleLanguages()) {
			visibleLocales.add(visibleLanguage.getLocale());
		}

		return Collections.unmodifiableSet(visibleLocales);
	}

	public Set<Locale> getUnifiedLanguageList() {
		if (unifiedLanguageList == null) {
			unifiedLanguageList = new TreeSet<>(new ByLanguageTag());
			Set<String> languagesWithVariants = new TreeSet<>();

			for (Language visibleLanguage : getVisibleLanguages()) {
				unifiedLanguageList.add(visibleLanguage.getLocale());

				if (Strings.isNotEmpty(visibleLanguage.getCountry())) {
					languagesWithVariants.add(visibleLanguage.getLanguage());
				}
			}
			for (String language : languagesWithVariants) {
				unifiedLanguageList.add(new Locale(language));
			}
		}

		return Collections.unmodifiableSet(unifiedLanguageList);
	}

	public Locale getClosestVisibleLocale(Locale locale) {
		return getClosestVisibleLocale(locale, null);
	}

	public Locale getClosestVisibleLocale(Locale locale, String country) {
		Locale closestVisibleLocale = getMatchingVisibleLanguageLocale(locale);

		if (closestVisibleLocale == null && Strings.isNotEmpty(country)
				&& !country.equals(locale.getCountry())) {
			Locale localeWithCountry = new Locale(locale.getLanguage(), country);
			closestVisibleLocale = getMatchingVisibleLanguageLocale(localeWithCountry);
		}

		if (closestVisibleLocale == null) {
			closestVisibleLocale = ENGLISH;
		}

		return closestVisibleLocale;
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

	private List<String> extractLanguageIsoCodesFrom(Collection<Language> visibleLanguages) {
		List<String> supportedLanguageKeys = new ArrayList<>();

		for (Language language : visibleLanguages) {
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
		List<KeyValue> supportedLanguages = new ArrayList<>();

		for (String languageKey : supportedLanguageKeys) {
			Locale locale = new Locale(languageKey);
			String displayLanguage = StringUtils.capitalize(locale.getDisplayLanguage(locale));

			supportedLanguages.add(new KeyValue(languageKey, displayLanguage));
		}

		return supportedLanguages;
	}

	private void addEnglishAsVisibleLanguage() {
		Language english = new Language();
		english.setLocale(ENGLISH);
		english.setLanguage(ENGLISH.getLanguage());
		english.setCountry(ENGLISH.getCountry());

		visibleLanguages.add(english);
	}

	private Locale getMatchingVisibleLanguageLocale(Locale locale) {
		Set<Locale> visibleLocales = getVisibleLocales();
		for (Locale visibleLocale : visibleLocales) {
			if (visibleLocale == null) {
				logger.error("Null locale found in getVisibleLocales: "
						+ Strings.implode(visibleLocales));
				continue;
			}
			if (visibleLocale.equals(locale)) {
				return locale;
			}
		}

		return null;
	}

	private PicsEnvironment picsEnvironment() {
		if (picsEnvironment == null) {
			picsEnvironment = new PicsEnvironment(propertyDAO.getProperty(AppProperty.VERSION_MAJOR),
					propertyDAO.getProperty(AppProperty.VERSION_MINOR));
		}

		return picsEnvironment;
	}

	private class ByLanguageTag implements Comparator<Locale> {
		@Override
		public int compare(Locale locale1, Locale locale2) {
			if (locale1 == null) {
				return -1;
			}

			if (locale2 == null) {
				return 1;
			}

			return locale1.toLanguageTag().compareTo(locale2.toLanguageTag());
		}
	}
}
