package com.picsauditing.model.i18n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LanguageModelTest {

	private LanguageModel languageModel;

	@Mock
	private LanguageProvider languageProvider;

	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private AppProperty appProperty;

	private static ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
	private static AppPropertyDAO appPropertyDAO = Mockito.mock(AppPropertyDAO.class);
	private static CountryDAO countryDAO = Mockito.mock(CountryDAO.class);

	@BeforeClass
	public static void classSetup() {
		when(applicationContext.getBean(SpringUtils.APP_PROPERTY_DAO)).thenReturn(appPropertyDAO);
		when(applicationContext.getBean(SpringUtils.COUNTRY_DAO)).thenReturn(countryDAO);
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.reset(appPropertyDAO, countryDAO);

		languageModel = new LanguageModel();

		Whitebox.setInternalState(languageModel, "languageProvider", languageProvider);
	}

	@Test
	public void testIsLanguageStable_NoAppLanguageDataMeansEnglishOnly() throws Exception {
		assertTrue(languageModel.isLanguageStable(Locale.ENGLISH));
		assertFalse(languageModel.isLanguageStable(Locale.FRENCH));
		assertFalse(languageModel.isLanguageStable(Locale.GERMAN));
	}

	@Test
	public void testIsLanguageStable_NoAppLanguageDataMeansEnglishOnlyTryVariants() throws Exception {
		assertTrue(languageModel.isLanguageStable(new Locale("en", "GB")));
		assertTrue(languageModel.isLanguageStable(new Locale("en", "US")));
		assertTrue(languageModel.isLanguageStable(new Locale("en", "CA")));
		assertTrue(languageModel.isLanguageStable(new Locale("en", "SA")));
	}

	@Test
	public void testIsLanguageStable_WithAppLanguageData() throws Exception {
		Language language1 = mock(Language.class);
		Language language2 = mock(Language.class);

		when(language1.getLanguage()).thenReturn("fr");
		when(language2.getLanguage()).thenReturn("de");

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		assertFalse(languageModel.isLanguageStable(Locale.ENGLISH));
		assertTrue(languageModel.isLanguageStable(Locale.FRENCH));
		assertTrue(languageModel.isLanguageStable(Locale.GERMAN));
	}

	@Test
	public void testGetStableLanguageValues_NoAppLanguageDataMeansEnglishOnly() throws Exception {
		assertNotNull(languageModel.getStableLanguagesSansDialect());
		assertEquals(1, languageModel.getStableLanguagesSansDialect().size());
		assertEquals(Locale.ENGLISH.getLanguage(), languageModel.getStableLanguagesSansDialect().get(0).getKey());
	}

	@Test
	public void testStableLanguageValues_WithAppLanguageData() throws Exception {
		Language language1 = new Language();
		Language language2 = new Language();

		language1.setLocale(Locale.FRENCH);
		language1.setLanguage(Locale.FRENCH.getLanguage());

		language2.setLocale(Locale.GERMAN);
		language2.setLanguage(Locale.GERMAN.getLanguage());

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		final List<KeyValue> supportedLanguages = languageModel.getStableLanguagesSansDialect();
		assertNotNull(supportedLanguages);
		assertEquals(2, supportedLanguages.size());
		assertEquals(Locale.GERMAN.getLanguage(), supportedLanguages.get(0).getKey());
		assertEquals(Locale.FRENCH.getLanguage(), supportedLanguages.get(1).getKey());
	}

	@Test
	public void testGetStableLanguageVariants_NoAppLanguageDataMeansEnglishUnitedStatesOnly() throws Exception {
		assertNotNull(languageModel.getStableLanguages());
		assertEquals(1, languageModel.getStableLanguages().size());
		assertEquals(LanguageModel.ENGLISH.getCountry(), languageModel.getStableLanguages().get(0).getCountry());
	}

	@Test
	public void testGetStableLanguageVariants_VariantsOnlyNoLanguage() throws Exception {
		Language language1 = mock(Language.class);
		Language language2 = mock(Language.class);
		Language language3 = mock(Language.class);

		when(language1.getLocale()).thenReturn(new Locale("en", "GB"));
		when(language1.getLanguage()).thenReturn("en");
		when(language1.getCountry()).thenReturn("GB");
		when(language2.getLocale()).thenReturn(new Locale("fr", "CA"));
		when(language2.getLanguage()).thenReturn("fr");
		when(language2.getCountry()).thenReturn("CA");
		when(language3.getLocale()).thenReturn(new Locale("es", "MX"));
		when(language3.getLanguage()).thenReturn("en");
		when(language3.getCountry()).thenReturn("MX");

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);
		languages.add(language3);

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		for (Language stableLanguage : languageModel.getStableLanguages()) {
			assertNotNull(stableLanguage.getCountry());
		}
	}

	@Test
	public void testSetFirstLanguageAsEnglish_HasEnglish() throws Exception {
		List<String> languageKeys = new ArrayList<String>();
		languageKeys.add("de");
		languageKeys.add("fr");
		languageKeys.add("en");

		Whitebox.invokeMethod(languageModel, "setFirstLanguageAsEnglish", languageKeys);

		assertEquals("en", languageKeys.get(0));
	}

	@Test
	public void testSetFirstLanguageAsEnglish_NoEnglish() throws Exception {
		List<String> languageKeys = new ArrayList<String>();
		languageKeys.add("de");
		languageKeys.add("fr");
		languageKeys.add("es");

		Whitebox.invokeMethod(languageModel, "setFirstLanguageAsEnglish", languageKeys);

		assertEquals("de", languageKeys.get(0));
	}

	@Test
	public void testGetStableLanguageLocales() throws Exception {
		Language language1 = new Language();
		Language language2 = new Language();
		Language language3 = new Language();

		Locale britishEnglish = new Locale("en", "GB");
		Locale canadianFrench = new Locale("fr", "CA");
		Locale mexicanSpanish = new Locale("es", "MX");

		language1.setLocale(britishEnglish);
		language2.setLocale(canadianFrench);
		language3.setLocale(mexicanSpanish);

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);
		languages.add(language3);

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		List<Locale> stableLanguageLocales = languageModel.getStableLanguageLocales();
		assertNotNull(stableLanguageLocales);
		assertEquals(britishEnglish, stableLanguageLocales.get(0));
		assertEquals(mexicanSpanish, stableLanguageLocales.get(1));
		assertEquals(canadianFrench, stableLanguageLocales.get(2));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetStableLanguageLocales_EnsureListIsUnmodifiable() {
		List<Language> languages = createLanguageList();

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		List<Locale> stableLanguageLocales = languageModel.getStableLanguageLocales();

		stableLanguageLocales.add(Locale.CHINA);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetStableLanguages_EnsureListIsUnmodifiable() {
		List<Language> languages = createLanguageList();

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		List<Language> stableLanguageLocales = languageModel.getStableLanguages();

		stableLanguageLocales.add(new Language());
	}

	private List<Language> createLanguageList() {
		Language language1 = new Language();
		Language language2 = new Language();
		Language language3 = new Language();

		Locale britishEnglish = new Locale("en", "GB");
		Locale canadianFrench = new Locale("fr", "CA");
		Locale mexicanSpanish = new Locale("es", "MX");

		language1.setLocale(britishEnglish);
		language2.setLocale(canadianFrench);
		language3.setLocale(mexicanSpanish);

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);
		languages.add(language3);

		return languages;
	}

	@Test
	public void testSetFirstLanguageAsEnglish() throws Exception {
		List<String> languages = new ArrayList<String>();
		languages.add(Locale.CHINESE.getLanguage());
		languages.add(Locale.FRENCH.getLanguage());
		languages.add(Locale.ENGLISH.getLanguage());
		languages.add(Locale.GERMAN.getLanguage());

		Whitebox.invokeMethod(languageModel, "setFirstLanguageAsEnglish", languages);

		assertEquals(Locale.ENGLISH.getLanguage(), languages.get(0));
		assertTrue(languages.contains(Locale.FRENCH.getLanguage()));
		assertTrue(languages.contains(Locale.GERMAN.getLanguage()));
		assertTrue(languages.contains(Locale.CHINESE.getLanguage()));
	}


	@Test
	public void testGetMatchingStableLocale() throws Exception {
		List<Language> languageList = mockLanguageList(new Locale[]{
				Locale.FRENCH,
				Locale.GERMAN,
				Locale.ITALIAN
		});

		when(languageProvider.findByStatus(LanguageStatus.Beta)).thenReturn(languageList);

		Locale result = Whitebox.invokeMethod(languageModel, "getMatchingStableLocale", new Locale("it"));
		assertEquals(Locale.ITALIAN, result);
	}

	@Test
	public void testGetMatchingStableLocale_WithNull() throws Exception {
		List<Language> languageList = mockLanguageList(new Locale[]{
				Locale.FRENCH,
				Locale.GERMAN,
				null,
				Locale.ITALIAN
		});
		Logger logger = Mockito.mock(Logger.class);

		Whitebox.setInternalState(languageModel, "logger", logger);

		ArgumentCaptor<String> errorMessageCaptor = ArgumentCaptor.forClass(String.class);

		when(languageProvider.findByStatus(LanguageStatus.Beta)).thenReturn(languageList);

		Locale result = Whitebox.invokeMethod(languageModel, "getMatchingStableLocale", new Locale("it"));
		assertEquals(Locale.ITALIAN, result);

		verify(logger).error(errorMessageCaptor.capture());

		assertEquals("Null locale found in Stable and Beta locales list: en_US,fr,de,null,it",
				errorMessageCaptor.getValue());
	}

	private List<Language> mockLanguageList(Locale[] localesToMock) {
		List<Language> languageList = new ArrayList<>();
		for (Locale locale : localesToMock) {
			Language language = Mockito.mock(Language.class);
			when(language.getLocale()).thenReturn(locale);
			languageList.add(language);
		}
		return languageList;
	}

	@AfterClass
	public static void classTeardown() {
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", (ApplicationContext) null);
		//Whitebox.setInternalState(LanguageModel.class, "languagesToEmailInvoicesInBPROCS", (Set<String>) null);
	}

	private void setupNullLanguageSpecifiedForBPROCSEmail() {
		when(appProperty.getValue()).thenReturn(null);
		when(appPropertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LANGUAGES_TO_EMAIL_VIA_BPROCS)).thenReturn(appProperty);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_NoLanguagesSpecified() {
		String contractorsLanguage = "de";
		Locale localeContractorAccountShouldReturn = Locale.GERMANY;
		when(contractorAccount.getLocale()).thenReturn(localeContractorAccountShouldReturn);

		setupNullLanguageSpecifiedForBPROCSEmail();
		boolean result = languageModel.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	private void setupEnglishToBeEmailedViaBProcs() {
		when(appProperty.getValue()).thenReturn("en");
		when(appPropertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LANGUAGES_TO_EMAIL_VIA_BPROCS)).thenReturn(appProperty);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveNullContractor() throws Exception {
		setupEnglishToBeEmailedViaBProcs();

		boolean result = languageModel.invoiceIsToBeEmailedViaBPROCS(null);

		assertFalse(result);
	}

	@Test
	public void testNoAppPropertyFound_DoesNotThrowException() {
		when(appPropertyDAO.find(anyString())).thenReturn(null);

		languageModel.invoiceIsToBeEmailedViaBPROCS(null);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveContractorWithNullLocale() throws Exception {
		setupEnglishToBeEmailedViaBProcs();
		Locale localeContractorAccountShouldReturn = Locale.GERMANY;
		when(contractorAccount.getLocale()).thenReturn(localeContractorAccountShouldReturn);

		boolean result = languageModel.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	private void setupLanguageModelEmailListContainsContractorsLanguage() {
		String languageToAddToEmailViaBPROCSList = "de";
		Locale localeContractorAccountShouldReturn = Locale.GERMANY;

		setupEnglishToBeEmailedViaBProcs();
		setupContractorAccountLocale(localeContractorAccountShouldReturn);
		addLanguageToEmailList(languageToAddToEmailViaBPROCSList);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_LanguageToEmailListCoversContractorsLocale() throws Exception {
		setupLanguageModelEmailListContainsContractorsLanguage();

		boolean result = languageModel.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertTrue(result);
	}

	private void setupLanguageModelEmailListDoesNotContainContractorsLanguage() {
		String languageToRemoveFromEmailViaBPROCSList = "de";
		Locale localeContractorAccountShouldReturn = Locale.GERMANY;

		setupEnglishToBeEmailedViaBProcs();
		setupContractorAccountLocale(localeContractorAccountShouldReturn);
		removeLanguageFromEmailList(languageToRemoveFromEmailViaBPROCSList);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_LanguageToEmailListDoesNotContainContractorsLocale() throws Exception {
		setupLanguageModelEmailListDoesNotContainContractorsLanguage();

		boolean result = languageModel.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	private void setupContractorAccountLocale(Locale locale) {
		when(contractorAccount.getLocale()).thenReturn(locale);
	}

	private void addLanguageToEmailList(String language) {
		languageModel.getLanguagesToEmailInvoicesInBPROCS().add(language);
	}

	private void removeLanguageFromEmailList(String language) {
		languageModel.getLanguagesToEmailInvoicesInBPROCS().remove(language);
	}

	private List<String> getLanguageList() {
		return Whitebox.getInternalState(LanguageModel.class, "languagesToEmailInvoicesInBPROCS");
	}

	@Test
	public void testGetDialectCountriesBasedOn_WhenDialectWithNullCountryFound_ThenNoExceptionIsThrown() {
		Language dialectWithNullCountry = mock(Language.class);
		when(dialectWithNullCountry.getCountry()).thenReturn(null);
		String language = "en";
		List<Language> dialectsByLanguage = new ArrayList<>();
		dialectsByLanguage.add(dialectWithNullCountry);
		when(languageProvider.findDialectsByLanguage(language)).thenReturn(dialectsByLanguage);

		languageModel.getDialectCountriesBasedOn(language);
	}
}
