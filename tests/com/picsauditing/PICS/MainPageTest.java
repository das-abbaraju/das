package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.actions.TranslationActionSupport;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Country;

public class MainPageTest extends PicsTranslationTest {
	private MainPage mainPage;

	private final String SYSTEM_MESSAGE_KEY = "SYSTEM.message.en";

	@Mock
	private Country country;
	@Mock
	private CountryDAO countryDAO;
	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpSession session;
	@Mock
	private Permissions permissions;
	@Mock
	private BasicDynaBean basicDynaBean;
	@Spy
	private AppPropertyDAO appPropertyDAO = new AppPropertyDAO();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mainPage = new MainPage(request, session);

		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
		when(session.getAttribute("permissions")).thenReturn(permissions);

		Whitebox.setInternalState(mainPage, "appPropertyDAO", appPropertyDAO);
		Whitebox.setInternalState(mainPage, "countryDAO", countryDAO);
	}

	@Test
	public void testGetPermissions_LoadedFromSession() throws Exception {
		assertEquals(permissions, mainPage.getPermissions());
	}

	@Test
	public void testGetPermissions_MissingFromSession() throws Exception {
		assertEquals(0, mainPage.getPermissions().getUserId());
		assertEquals(0, mainPage.getPermissions().getAccountId());
		assertNull(mainPage.getPermissions().getUsername());
		assertFalse(mainPage.getPermissions().isLoggedIn());
		assertFalse(mainPage.getPermissions().isActive());
	}

	@Test
	public void testGetPermissions_SetByAction() {
		Permissions permissions = new Permissions();
		mainPage.setPermissions(permissions);

		assertEquals(permissions, mainPage.getPermissions());
	}

	@Test
	public void testIsPageSecure_IsSecure() throws Exception {
		when(request.isSecure()).thenReturn(true);

		assertTrue(mainPage.isPageSecure());
	}

	@Test
	public void testIsPageSecure_PortIs443() throws Exception {
		when(request.getLocalPort()).thenReturn(443);

		assertTrue(mainPage.isPageSecure());
	}

	@Test
	public void testIsPageSecure_PortIs81() throws Exception {
		when(request.getLocalPort()).thenReturn(81);

		assertTrue(mainPage.isPageSecure());
	}

	@Test
	public void testIsPageSecure_IsInsecure() throws Exception {
		when(request.isSecure()).thenReturn(false);

		assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testIsPageSecure_UnsecuredPortNumber() throws Exception {
		when(request.getLocalPort()).thenReturn(80);

		assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testIsPageSecure_RandomPortNumber() throws Exception {
		when(request.getLocalPort()).thenReturn(1234);

		assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testIsPageSecure_RequestIsMissing() throws Exception {
		Whitebox.setInternalState(mainPage, "request", (HttpServletRequest) null);

		assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testSplitRegexOnDot() {
		String[] exploded = SYSTEM_MESSAGE_KEY.split("\\.");

		assertEquals(3, exploded.length);
		assertEquals("SYSTEM", exploded[0]);
		assertEquals("message", exploded[1]);
		assertEquals("en", exploded[2]);
	}

	@Test
	public void testIsDebugMode_CookieSetTrue() {
		Cookie debuggingCookie = new Cookie("debugging", "true");

		when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		assertTrue(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieSetOne() {
		Cookie debuggingCookie = new Cookie("debugging", "1");

		when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieSetNull() {
		Cookie debuggingCookie = new Cookie("debugging", null);

		when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieSetFalse() {
		Cookie debuggingCookie = new Cookie("debugging", "false");

		when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieRandom() {
		Cookie debuggingCookie = new Cookie("debugging", "Hello World");

		when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieMissing() {
		when(request.getCookies()).thenReturn(new Cookie[] {});

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_NoCookies() {
		when(request.getCookies()).thenReturn(null);

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieNotDebugging() {
		when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("Test", "cookie") });

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_MissingRequest() {
		Whitebox.setInternalState(mainPage, "request", (HttpServletRequest) null);
		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDisplaySystemMessage_AppPropertySet1() {
		AppProperty showMessages = new AppProperty();
		showMessages.setProperty("show messages");
		showMessages.setValue("1");

		doReturn(showMessages).when(appPropertyDAO).find(AppProperty.SYSTEM_MESSAGE);

		assertTrue(mainPage.isDisplaySystemMessage());
	}

	@Test
	public void testIsDisplaySystemMessage_AppPropertySetOther() {
		AppProperty showMessages = new AppProperty();
		showMessages.setProperty("show messages");
		showMessages.setValue("Hello World");
		doReturn(showMessages).when(appPropertyDAO).find(AppProperty.SYSTEM_MESSAGE);

		assertFalse(mainPage.isDisplaySystemMessage());

		showMessages.setValue("0");
		assertFalse(mainPage.isDisplaySystemMessage());

		doReturn(null).when(appPropertyDAO).find(AppProperty.SYSTEM_MESSAGE);
		assertFalse(mainPage.isDisplaySystemMessage());
	}

	@Test
	public void testGetPhoneNumber_WithoutCountry() {
		// Defaults to "PicsPhone" translation from i18nCache
		// Permissions will have a null country,
		// so it should skip to printing the default number
		String phoneNumber = mainPage.getPhoneNumber();

		assertEquals(MainPage.PICS_PHONE_NUMBER, phoneNumber);
	}

	@Test
	public void testGetPhoneNumber_PassedInCountry() {
		// Get phone number from country object based on isocode passed in
		String countryPhoneNumber = "Phone Number";
		when(country.getPhone()).thenReturn(countryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getPhoneNumber("US");

		assertEquals(countryPhoneNumber, phoneNumber);
	}

	@Test
	public void testGetPhoneNumber_CountryFromPermissions() {
		// Get phone number from country object based on isocode passed in (from
		// permissions)
		when(permissions.getCountry()).thenReturn("US");

		String permissionsCountryPhoneNumber = "Phone Number";
		when(country.getPhone()).thenReturn(permissionsCountryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getPhoneNumber("US");

		assertEquals(permissionsCountryPhoneNumber, phoneNumber);
	}

	@Test
	public void testGetSalesPhoneNumber_WithoutCountryAndWithoutPermissions() {
		assertEquals(MainPage.PICS_SALES_NUMBER, mainPage.getSalesPhoneNumber());
	}

	@Test
	public void testGetSalesPhoneNumber_PassedInCountry() {
		String countryPhoneNumber = "Phone Number";
		when(country.getSalesPhone()).thenReturn(countryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getSalesPhoneNumber("US");

		assertEquals(countryPhoneNumber, phoneNumber);
	}

	@Test
	public void testGetSalesPhoneNumber_CountryFromPermissions() {
		when(permissions.getCountry()).thenReturn("US");

		String permissionsCountryPhoneNumber = "Phone Number";
		when(country.getSalesPhone()).thenReturn(permissionsCountryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getSalesPhoneNumber("US");

		assertEquals(permissionsCountryPhoneNumber, phoneNumber);
	}

	@Test
	public void testGetFaxNumber_WithoutCountryAndWithoutPermissions() {
		assertEquals(MainPage.PICS_FAX_NUMBER, mainPage.getFaxNumber());
	}

	@Test
	public void testGetFaxNumber_PassedInCountry() {
		String countryPhoneNumber = "Phone Number";
		when(country.getFax()).thenReturn(countryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getFaxNumber("US");

		assertEquals(countryPhoneNumber, phoneNumber);
	}

	@Test
	public void testGetFaxNumber_CountryFromPermissions() {
		when(permissions.getCountry()).thenReturn("US");

		String permissionsCountryPhoneNumber = "Phone Number";
		when(country.getFax()).thenReturn(permissionsCountryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getFaxNumber("US");

		assertEquals(permissionsCountryPhoneNumber, phoneNumber);
	}

    @Test
    public void testInsertI18nPhoneDescriptionsForMultiplePhoneNumbers_NullCountryReturnsSamePhone()
            throws Exception {
        String adjustedPhoneDescription = Whitebox.invokeMethod(
                mainPage,
                "insertI18nPhoneDescriptionsForMultiplePhoneNumbers",
                (String)null,
                MainPage.PICS_PHONE_NUMBER);
        assertEquals(MainPage.PICS_PHONE_NUMBER, adjustedPhoneDescription);
    }

    @Test
    public void testInsertI18nPhoneDescriptionsForMultiplePhoneNumbers_NullPhoneReturnsNull()
            throws Exception {
        String adjustedPhoneDescription = Whitebox.invokeMethod(
                mainPage,
                "insertI18nPhoneDescriptionsForMultiplePhoneNumbers",
                (String)null,
                (String)null);
        assertNull(adjustedPhoneDescription);
    }

    @Test
    public void testInsertI18nPhoneDescriptionsForMultiplePhoneNumbers_CountryWithOneNumberIsSamePhoneNumber()
            throws Exception {
        String adjustedPhoneDescription = Whitebox.invokeMethod(
                mainPage,
                "insertI18nPhoneDescriptionsForMultiplePhoneNumbers",
                Country.US_ISO_CODE,
                MainPage.PICS_PHONE_NUMBER);
        assertEquals(MainPage.PICS_PHONE_NUMBER, adjustedPhoneDescription);
    }

    @Test
    public void testInsertI18nPhoneDescriptionsForMultiplePhoneNumbers_ChinaAdjustsNumber() throws Exception {
        String chinaPhone = "10800-1301-799 10800-713-1837";
        String chinaPhoneAdjusted = "China (North): 10800-1301-799 | China (South): 10800-713-1837";
        when(translationService.getText("Main.Phone.China.Label1", TranslationActionSupport.getLocaleStatic())).
                thenReturn("China (North)");
        when(translationService.getText("Main.Phone.China.Label2", TranslationActionSupport.getLocaleStatic())).
                thenReturn("China (South)");

        String adjustedPhoneDescription = Whitebox.invokeMethod(
                mainPage,
                "insertI18nPhoneDescriptionsForMultiplePhoneNumbers",
                Country.CHINA_ISO_CODE,
                chinaPhone);

        assertEquals(chinaPhoneAdjusted, adjustedPhoneDescription);
    }

    @Test
    public void testInsertI18nPhoneDescriptionsForMultiplePhoneNumbers_ChinaButPhoneNumberHasChangedFormatReturnsPhoneGiven() throws Exception {
        String chinaPhone = "10800-1301-799 10800-713-1837 10800-713-1837";

        String adjustedPhoneDescription = Whitebox.invokeMethod(
                mainPage,
                "insertI18nPhoneDescriptionsForMultiplePhoneNumbers",
                Country.CHINA_ISO_CODE,
                chinaPhone);

        assertEquals(chinaPhone, adjustedPhoneDescription);
    }

}
