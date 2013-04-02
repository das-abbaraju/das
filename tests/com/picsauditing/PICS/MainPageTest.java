package com.picsauditing.PICS;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Country;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class MainPageTest {
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
	public void testIsLiveChatEnabled_Enabled() throws Exception {
		AppProperty appProperty = new AppProperty();
		appProperty.setValue("1");

		doReturn(appProperty).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		assertTrue(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testIsLiveChatEnabled_SetTo0() throws Exception {
		AppProperty appProperty = new AppProperty();
		appProperty.setValue("0");

		doReturn(appProperty).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		assertFalse(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testIsLiveChatEnabled_SetToRandom() throws Exception {
		AppProperty appProperty = new AppProperty();
		appProperty.setValue("Hello World");

		doReturn(appProperty).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		assertFalse(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testIsLiveChatEnabled_Null() throws Exception {
		doReturn(null).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		assertFalse(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testIsDebugMode_CookieSetTrue() {
		Cookie debuggingCookie = new Cookie("debugging", "true");

		when(request.getCookies()).thenReturn(new Cookie[]{debuggingCookie});

		assertTrue(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieSetOne() {
		Cookie debuggingCookie = new Cookie("debugging", "1");

		when(request.getCookies()).thenReturn(new Cookie[]{debuggingCookie});

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieSetNull() {
		Cookie debuggingCookie = new Cookie("debugging", null);

		when(request.getCookies()).thenReturn(new Cookie[]{debuggingCookie});

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieSetFalse() {
		Cookie debuggingCookie = new Cookie("debugging", "false");

		when(request.getCookies()).thenReturn(new Cookie[]{debuggingCookie});

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieRandom() {
		Cookie debuggingCookie = new Cookie("debugging", "Hello World");

		when(request.getCookies()).thenReturn(new Cookie[]{debuggingCookie});

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieMissing() {
		when(request.getCookies()).thenReturn(new Cookie[]{});

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_NoCookies() {
		when(request.getCookies()).thenReturn(null);

		assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testIsDebugMode_CookieNotDebugging() {
		when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("Test", "cookie") });

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
		// Get phone number from country object based on isocode passed in (from permissions)
		when(permissions.getCountry()).thenReturn("US");

		String permissionsCountryPhoneNumber = "Phone Number";
		when(country.getPhone()).thenReturn(permissionsCountryPhoneNumber);
		when(countryDAO.find("US")).thenReturn(country);

		String phoneNumber = mainPage.getPhoneNumber("US");

		assertEquals(permissionsCountryPhoneNumber, phoneNumber);
	}
}
