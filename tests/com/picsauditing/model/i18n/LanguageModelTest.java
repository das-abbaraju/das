package com.picsauditing.model.i18n;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;
import com.picsauditing.toggle.FeatureToggle;
import org.apache.struts2.ServletActionContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LanguageModelTest {

	private LanguageModel languageModel;

	@Mock
	private ActionContext actionContext;
	@Mock
	private AppProperty appProperty;
	@Mock
	private AppPropertyDAO appPropertyDAO;
	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private LanguageProvider languageProvider;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		ActionContext.setContext(actionContext);

		languageModel = new LanguageModel();

		when(actionContext.get(ServletActionContext.HTTP_REQUEST)).thenReturn(httpServletRequest);
		when(httpServletRequest.getServerName()).thenReturn("localhost");
		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(createLanguageList());

		Whitebox.setInternalState(languageModel, "languageProvider", languageProvider);
		Whitebox.setInternalState(languageModel, "propertyDAO", appPropertyDAO);
	}

	@AfterClass
	public static void classTearDown() {
		ActionContext.setContext(null);
	}

	@Test
	public void testIsLanguageVisible_NoAppLanguageDataMeansEnglishOnly() throws Exception {
		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(null);
		assertTrue(languageModel.isLanguageVisible(Locale.ENGLISH));
		assertFalse(languageModel.isLanguageVisible(Locale.FRENCH));
		assertFalse(languageModel.isLanguageVisible(Locale.GERMAN));
	}

	@Test
	public void testIsLanguageVisible_NoAppLanguageDataMeansEnglishOnlyTryVariants() throws Exception {
		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(null);
		assertTrue(languageModel.isLanguageVisible(new Locale("en", "GB")));
		assertTrue(languageModel.isLanguageVisible(new Locale("en", "US")));
		assertTrue(languageModel.isLanguageVisible(new Locale("en", "CA")));
		assertTrue(languageModel.isLanguageVisible(new Locale("en", "SA")));
	}

	@Test
	public void testIsLanguageVisible_WithAppLanguageData() throws Exception {
		Language language1 = mock(Language.class);
		Language language2 = mock(Language.class);

		when(language1.getLanguage()).thenReturn("fr");
		when(language2.getLanguage()).thenReturn("de");

		List<Language> languages = new ArrayList<>();
		languages.add(language1);
		languages.add(language2);

		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(languages);

		assertFalse(languageModel.isLanguageVisible(Locale.ENGLISH));
		assertTrue(languageModel.isLanguageVisible(Locale.FRENCH));
		assertTrue(languageModel.isLanguageVisible(Locale.GERMAN));
	}

	@Test
	public void testGetVisibleLanguagesSansDialect_NoAppLanguageDataMeansEnglishOnly() throws Exception {
		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(null);
		assertNotNull(languageModel.getVisibleLanguagesSansDialect());
		assertEquals(1, languageModel.getVisibleLanguagesSansDialect().size());
		assertEquals(Locale.ENGLISH.getLanguage(), languageModel.getVisibleLanguagesSansDialect().get(0).getKey());
	}

	@Test
	public void testGetVisibleLanguagesSansDialect_WithAppLanguageData() throws Exception {
		Language language1 = new Language();
		Language language2 = new Language();

		language1.setLocale(Locale.FRENCH);
		language1.setLanguage(Locale.FRENCH.getLanguage());

		language2.setLocale(Locale.GERMAN);
		language2.setLanguage(Locale.GERMAN.getLanguage());

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);

		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(languages);

		final List<KeyValue> supportedLanguages = languageModel.getVisibleLanguagesSansDialect();
		assertNotNull(supportedLanguages);
		assertEquals(2, supportedLanguages.size());
		assertEquals(Locale.GERMAN.getLanguage(), supportedLanguages.get(0).getKey());
		assertEquals(Locale.FRENCH.getLanguage(), supportedLanguages.get(1).getKey());
	}

	@Test
	public void testGetVisibleLanguages_NoAppLanguageDataMeansEnglishUnitedStatesOnly() throws Exception {
		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(null);
		Set<Language> visibleLanguages = languageModel.getVisibleLanguages();
		assertNotNull(visibleLanguages);
		assertEquals(1, visibleLanguages.size());

		Iterator<Language> iterator = visibleLanguages.iterator();
		while (iterator.hasNext()) {
			assertEquals(LanguageModel.ENGLISH.getCountry(), iterator.next().getCountry());
		}
	}

	@Test
	public void testGetVisibleLanguages_VariantsOnlyNoLanguage() throws Exception {
		for (Language stableLanguage : languageModel.getVisibleLanguages()) {
			assertNotNull(stableLanguage.getCountry());
		}
	}

	@Test
	public void testSetFirstLanguageAsEnglish_HasEnglish() throws Exception {
		List<String> languageKeys = new ArrayList<>();
		languageKeys.add("de");
		languageKeys.add("fr");
		languageKeys.add("en");

		Whitebox.invokeMethod(languageModel, "setFirstLanguageAsEnglish", languageKeys);

		assertEquals("en", languageKeys.get(0));
	}

	@Test
	public void testSetFirstLanguageAsEnglish_NoEnglish() throws Exception {
		List<String> languageKeys = new ArrayList<>();
		languageKeys.add("de");
		languageKeys.add("fr");
		languageKeys.add("es");

		Whitebox.invokeMethod(languageModel, "setFirstLanguageAsEnglish", languageKeys);

		assertEquals("de", languageKeys.get(0));
	}

	@Test
	public void testGetVisibleLocales() throws Exception {
		List<Language> languages = createLanguageList();

		Set<Locale> visibleLocales = languageModel.getVisibleLocales();
		assertNotNull(visibleLocales);
		assertEquals(3, visibleLocales.size());

		Iterator<Locale> iterator = visibleLocales.iterator();
		while (iterator.hasNext()) {
			// en_GB
			assertEquals(languages.get(0).getLocale(), iterator.next());
			// es_MX
			assertEquals(languages.get(2).getLocale(), iterator.next());
			// fr_CA
			assertEquals(languages.get(1).getLocale(), iterator.next());
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetVisibleLocales_EnsureListIsUnmodifiable() {
		List<Language> languages = createLanguageList();

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		Set<Locale> stableLanguageLocales = languageModel.getVisibleLocales();

		stableLanguageLocales.add(Locale.CHINA);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetStableLanguages_EnsureListIsUnmodifiable() {
		List<Language> languages = createLanguageList();

		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(languages);

		Set<Language> stableLanguageLocales = languageModel.getVisibleLanguages();

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
		language1.setLanguage("en");
		language1.setCountry("GB");

		language2.setLocale(canadianFrench);
		language2.setLanguage("fr");
		language2.setCountry("CA");

		language3.setLocale(mexicanSpanish);
		language3.setLanguage("es");
		language3.setCountry("MX");

		List<Language> languages = new ArrayList<>();
		languages.add(language1);
		languages.add(language2);
		languages.add(language3);

		return languages;
	}

	@Test
	public void testSetFirstLanguageAsEnglish() throws Exception {
		List<String> languages = new ArrayList<>();
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
	public void testGetMatchingVisibleLanguageLocale() throws Exception {
		List<Language> languageList = mockLanguageList(new Locale[]{
				Locale.FRENCH,
				Locale.GERMAN,
				Locale.ITALIAN
		});

		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(languageList);

		Locale result = Whitebox.invokeMethod(languageModel, "getMatchingVisibleLanguageLocale", new Locale("it"));
		assertEquals(Locale.ITALIAN, result);
	}

	@Test
	public void testGetMatchingVisibleLanguageLocale_WithNull() throws Exception {
		List<Language> languageList = mockLanguageList(new Locale[]{
				Locale.FRENCH,
				Locale.GERMAN,
				null,
				Locale.ITALIAN
		});
		Logger logger = Mockito.mock(Logger.class);

		Whitebox.setInternalState(languageModel, "logger", logger);

		ArgumentCaptor<String> errorMessageCaptor = ArgumentCaptor.forClass(String.class);

		when(languageProvider.findByStatuses(any(LanguageStatus[].class))).thenReturn(languageList);

		Locale result = Whitebox.invokeMethod(languageModel, "getMatchingVisibleLanguageLocale", new Locale("it"));
		assertEquals(Locale.ITALIAN, result);

		verify(logger).error(errorMessageCaptor.capture());

		assertEquals("Null locale found in getVisibleLocales: null,de,fr,it",
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
