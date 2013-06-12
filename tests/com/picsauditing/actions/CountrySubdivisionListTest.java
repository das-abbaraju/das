package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.MainPage;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;

public class CountrySubdivisionListTest extends PicsActionTest {

	private CountrySubdivisionList countrySubdivisionList;

	@Mock
	private Account account;
	@Mock
	private Country country;
	@Mock
	private CountrySubdivision countrySubdivision;
	@Mock
	private CountryDAO countryDAO;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		countrySubdivisionList = new CountrySubdivisionList();

		Whitebox.setInternalState(countrySubdivisionList, "countryDAO", countryDAO);
		Whitebox.setInternalState(countrySubdivisionList, "permissions", permissions);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPhone_ProvidedValidCountryCode() throws Exception {
		when(country.getI18nKey()).thenReturn("Germany");
		when(country.getPhone()).thenReturn("German Phone");
		when(countryDAO.find("de")).thenReturn(country);
		when(translationService.hasKey(eq("Germany"), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq("Germany"), any(Locale.class), any())).thenReturn("Germany");

		countrySubdivisionList.setCountryString("de");

		assertEquals(PicsActionSupport.JSON, countrySubdivisionList.phone());
		assertNotNull(countrySubdivisionList.getJson());
		assertNotNull(countrySubdivisionList.getJson().get("country"));
		assertNotNull(countrySubdivisionList.getJson().get("picsPhoneNumber"));
		assertEquals("Germany", countrySubdivisionList.getJson().get("country"));
		assertEquals("German Phone", countrySubdivisionList.getJson().get("picsPhoneNumber"));
	}

	@Test
	public void testPhone_ProvidedInvalidCountryCode() throws Exception {
		String defaultString = "Country";
		when(translationService.hasKey(eq(defaultString), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq(defaultString), any(Locale.class), any())).thenReturn(defaultString);

		countrySubdivisionList.setCountryString("abc");

		assertEquals(PicsActionSupport.JSON, countrySubdivisionList.phone());
		assertNotNull(countrySubdivisionList.getJson());
		assertNotNull(countrySubdivisionList.getJson().get("country"));
		assertNotNull(countrySubdivisionList.getJson().get("picsPhoneNumber"));
		assertEquals(defaultString, countrySubdivisionList.getJson().get("country"));
		assertEquals(MainPage.PICS_PHONE_NUMBER, countrySubdivisionList.getJson().get("picsPhoneNumber"));
	}

	@Test
	public void testPhone_CountryHasNoPhone() throws Exception {
		when(country.getI18nKey()).thenReturn("Germany");
		when(countryDAO.find("de")).thenReturn(country);
		when(translationService.hasKey(eq("Germany"), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq("Germany"), any(Locale.class), any())).thenReturn("Germany");

		countrySubdivisionList.setCountryString("de");

		assertEquals(PicsActionSupport.JSON, countrySubdivisionList.phone());
		assertNotNull(countrySubdivisionList.getJson());
		assertNotNull(countrySubdivisionList.getJson().get("country"));
		assertNotNull(countrySubdivisionList.getJson().get("picsPhoneNumber"));
		assertEquals("Germany", countrySubdivisionList.getJson().get("country"));
		assertEquals(MainPage.PICS_PHONE_NUMBER, countrySubdivisionList.getJson().get("picsPhoneNumber"));
	}

	@Test
	public void testGetAccountCountrySubdivision_AccountNotNull() throws Exception {
		when(account.getCountrySubdivision()).thenReturn(countrySubdivision);

		countrySubdivisionList.setAccount(account);

		assertEquals(countrySubdivision, countrySubdivisionList.getAccountCountrySubdivision());
	}

	@Test
	public void testGetAccountCountrySubdivision_AccountCountrySubdivisionNull() throws Exception {
		countrySubdivisionList.setAccount(account);

		assertNull(countrySubdivisionList.getAccountCountrySubdivision());
	}

	@Test
	public void testGetAccountCountrySubdivision_CountrySubdivisionStringNotNull() throws Exception {
		countrySubdivisionList.setCountrySubdivisionString("NotNull");

		assertNotNull(countrySubdivisionList.getAccountCountrySubdivision());
		assertEquals("NotNull", countrySubdivisionList.getAccountCountrySubdivision().getIsoCode());
	}

	@Test
	public void testGetAccountCountrySubdivision_CountrySubdivisionStringNullOrEmpty() throws Exception {
		countrySubdivisionList.setCountrySubdivisionString("");
		assertNull(countrySubdivisionList.getAccountCountrySubdivision());

		countrySubdivisionList.setCountrySubdivisionString(null);
		assertNull(countrySubdivisionList.getAccountCountrySubdivision());
	}

	@Test
	public void testGetCountrySubdivisionPrefix_PrefixIsNull() throws Exception {
		assertEquals("countrySubdivision", countrySubdivisionList.getCountrySubdivisionPrefix());
	}

	@Test
	public void testGetCountrySubdivisionPrefix_PrefixContainsCountrySubdivision() throws Exception {
		countrySubdivisionList.setPrefix("CountrySubdivision.blah");
		assertEquals("CountrySubdivision.blah", countrySubdivisionList.getCountrySubdivisionPrefix());
	}

	@Test
	public void testGetCountrySubdivisionPrefix_OtherPrefix() throws Exception {
		countrySubdivisionList.setPrefix("contractor.");
		assertEquals("contractor.countrySubdivision", countrySubdivisionList.getCountrySubdivisionPrefix());
	}
}
