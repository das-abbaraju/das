package com.picsauditing.model.i18n;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageModelTest {
	private LanguageModel languageModel;

	@Mock
	private LanguageProvider languageProvider;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

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
		Language language1 = mock(Language.class);
		Language language2 = mock(Language.class);

		when(language1.getLocale()).thenReturn(Locale.FRENCH);
		when(language1.getLanguage()).thenReturn("fr");
		when(language2.getLocale()).thenReturn(Locale.GERMAN);
		when(language2.getLanguage()).thenReturn("de");

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		final List<LanguageModel.KeyValue> supportedLanguages = languageModel.getStableLanguagesSansDialect();
		assertNotNull(supportedLanguages);
		assertEquals(2, supportedLanguages.size());
		assertEquals(Locale.FRENCH.getLanguage(), supportedLanguages.get(0).getKey());
		assertEquals(Locale.GERMAN.getLanguage(), supportedLanguages.get(1).getKey());
	}

	@Test
	public void testGetStableLanguageVariants_NoAppLanguageDataMeansEnglishOnlyNoVariants() throws Exception {
		assertNotNull(languageModel.getStableLanguages());
		assertEquals(1, languageModel.getStableLanguages().size());
		assertNull(languageModel.getStableLanguages().get(0).getCountry());
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
		Language language1 = mock(Language.class);
		Language language2 = mock(Language.class);
		Language language3 = mock(Language.class);

		Locale britishEnglish = new Locale("en", "GB");
		Locale canadianFrench = new Locale("fr", "CA");
		Locale mexicanSpanish = new Locale("es", "MX");

		when(language1.getLocale()).thenReturn(britishEnglish);
		when(language2.getLocale()).thenReturn(canadianFrench);
		when(language3.getLocale()).thenReturn(mexicanSpanish);

		List<Language> languages = new ArrayList<Language>();
		languages.add(language1);
		languages.add(language2);
		languages.add(language3);

		when(languageProvider.findByStatus(LanguageStatus.Stable)).thenReturn(languages);

		Locale[] stableLanguageLocales = languageModel.getStableLanguageLocales();
		assertNotNull(stableLanguageLocales);
		assertEquals(britishEnglish, stableLanguageLocales[0]);
		assertEquals(canadianFrench, stableLanguageLocales[1]);
		assertEquals(mexicanSpanish, stableLanguageLocales[2]);
	}
}
