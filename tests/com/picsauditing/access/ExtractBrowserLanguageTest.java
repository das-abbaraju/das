package com.picsauditing.access;

import com.picsauditing.jpa.entities.Language;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class ExtractBrowserLanguageTest {
	private ExtractBrowserLanguage extractBrowserLanguage;
	private List<Language> languages;

	@Mock
	private Enumeration enumeration;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private Language language;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		languages = new ArrayList<>();
		languages.add(language);

		when(language.getLanguage()).thenReturn(Locale.GERMAN.getLanguage());
		when(httpServletRequest.getHeaders(ExtractBrowserLanguage.ACCEPT_LANGUAGE)).thenReturn(enumeration);
	}

	@Test
	public void testGetBrowserLanguage_LanguageIsStable() throws Exception {
		when(enumeration.hasMoreElements()).thenReturn(true);
		when(enumeration.nextElement()).thenReturn("de-DE,en-US,sv");

		extractBrowserLanguage = new ExtractBrowserLanguage(httpServletRequest, languages);

		assertEquals("de", extractBrowserLanguage.getBrowserLanguage());
	}

	@Test
	public void testGetBrowserLanguage_LanguageIsNotStableDefaultsToEnglish() throws Exception {
		when(enumeration.hasMoreElements()).thenReturn(true);
		when(enumeration.nextElement()).thenReturn("sv,fr,pt");

		extractBrowserLanguage = new ExtractBrowserLanguage(httpServletRequest, languages);

		assertEquals(Locale.ENGLISH.getLanguage(), extractBrowserLanguage.getBrowserLanguage());
	}

	@Test
	public void testGetBrowserLanguage_NoLanguagesInHeaders() throws Exception {
		when(enumeration.hasMoreElements()).thenReturn(false);

		extractBrowserLanguage = new ExtractBrowserLanguage(httpServletRequest, languages);

		assertEquals(Locale.ENGLISH.getLanguage(), extractBrowserLanguage.getBrowserLanguage());
	}

	@Test
	public void testGetBrowserDialect_LanguageIsStable() throws Exception {
		when(enumeration.hasMoreElements()).thenReturn(true);
		when(enumeration.nextElement()).thenReturn("de-DE,en-US,sv");

		extractBrowserLanguage = new ExtractBrowserLanguage(httpServletRequest, languages);

		assertEquals("DE", extractBrowserLanguage.getBrowserDialect());
	}

	@Test
	public void testGetBrowserDialect_LanguageIsNotStableDialectIsNull() throws Exception {
		when(enumeration.hasMoreElements()).thenReturn(true);
		when(enumeration.nextElement()).thenReturn("fr-CA");

		extractBrowserLanguage = new ExtractBrowserLanguage(httpServletRequest, languages);

		assertNull(extractBrowserLanguage.getBrowserDialect());
	}

	@Test
	public void testGetBrowserLocale_NoLanguagesInHeadersDialectIsNull() throws Exception {
		when(enumeration.hasMoreElements()).thenReturn(false);

		extractBrowserLanguage = new ExtractBrowserLanguage(httpServletRequest, languages);

		assertNull(extractBrowserLanguage.getBrowserDialect());
	}
}
