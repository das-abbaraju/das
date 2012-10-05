package com.picsauditing.PICS;

import static org.mockito.Mockito.*;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

public class MainPageTest {
	private MainPage mainPage;

	private final String SYSTEM_MESSAGE_KEY = "SYSTEM.message.en";

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
		Mockito.when(session.getAttribute("permissions")).thenReturn(permissions);

		Whitebox.setInternalState(mainPage, "appPropertyDAO", appPropertyDAO);
	}

	@Test
	public void testPermissionsLoadedFromSession() throws Exception {
		Assert.assertEquals(permissions, mainPage.getPermissions());
	}

	@Test
	public void testPermissionMissingFromSession() throws Exception {
		Assert.assertEquals(0, mainPage.getPermissions().getUserId());
		Assert.assertEquals(0, mainPage.getPermissions().getAccountId());
		Assert.assertNull(mainPage.getPermissions().getUsername());
		Assert.assertFalse(mainPage.getPermissions().isLoggedIn());
		Assert.assertFalse(mainPage.getPermissions().isActive());
	}

	@Test
	public void testRequestIsSecure() throws Exception {
		Mockito.when(request.isSecure()).thenReturn(true);

		Assert.assertTrue(mainPage.isPageSecure());
	}

	@Test
	public void testRequestPortIs443() throws Exception {
		Mockito.when(request.getLocalPort()).thenReturn(443);

		Assert.assertTrue(mainPage.isPageSecure());
	}

	@Test
	public void testRequestPortIs81() throws Exception {
		Mockito.when(request.getLocalPort()).thenReturn(81);

		Assert.assertTrue(mainPage.isPageSecure());
	}

	@Test
	public void testRequestIsInsecure() throws Exception {
		Mockito.when(request.isSecure()).thenReturn(false);

		Assert.assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testRequestUnsecuredPortNumber() throws Exception {
		Mockito.when(request.getLocalPort()).thenReturn(80);

		Assert.assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testRequestRandomPortNumber() throws Exception {
		Mockito.when(request.getLocalPort()).thenReturn(1234);

		Assert.assertFalse(mainPage.isPageSecure());
	}

	@Test
	public void testSplitRegexOnDot() {
		String[] exploded = SYSTEM_MESSAGE_KEY.split("\\.");

		Assert.assertEquals(3, exploded.length);
		Assert.assertEquals("SYSTEM", exploded[0]);
		Assert.assertEquals("message", exploded[1]);
		Assert.assertEquals("en", exploded[2]);
	}

	@Test
	public void testLiveChatEnabled() throws Exception {
		AppProperty appProperty = new AppProperty();
		appProperty.setValue("1");

		Mockito.doReturn(appProperty).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		Assert.assertTrue(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testLiveChatSetTo0() throws Exception {
		AppProperty appProperty = new AppProperty();
		appProperty.setValue("0");

		Mockito.doReturn(appProperty).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		Assert.assertFalse(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testLiveChatSetToRandom() throws Exception {
		AppProperty appProperty = new AppProperty();
		appProperty.setValue("Hello World");

		Mockito.doReturn(appProperty).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		Assert.assertFalse(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testLiveChatNull() throws Exception {
		Mockito.doReturn(null).when(appPropertyDAO).find(AppProperty.LIVECHAT);

		Assert.assertFalse(mainPage.isLiveChatEnabled());
	}

	@Test
	public void testDebugModeCookieSetTrue() {
		Cookie debuggingCookie = new Cookie("debugging", "true");

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		Assert.assertTrue(mainPage.isDebugMode());
	}

	@Test
	public void testDebugModeCookieSetOne() {
		Cookie debuggingCookie = new Cookie("debugging", "1");

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		Assert.assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testDebugModeCookieSetNull() {
		Cookie debuggingCookie = new Cookie("debugging", null);

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		Assert.assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testDebugModeCookieSetFalse() {
		Cookie debuggingCookie = new Cookie("debugging", "false");

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		Assert.assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testDebugModeCookieRandom() {
		Cookie debuggingCookie = new Cookie("debugging", "Hello World");

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { debuggingCookie });

		Assert.assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testDebugModeCookieMissing() {
		Mockito.when(request.getCookies()).thenReturn(new Cookie[] {});

		Assert.assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testDebugModeNoCookies() {
		Mockito.when(request.getCookies()).thenReturn(null);

		Assert.assertFalse(mainPage.isDebugMode());
	}

	@Test
	public void testShowSystemMessagesAppPropertySet1() {
		AppProperty showMessages = new AppProperty();
		showMessages.setProperty("show messages");
		showMessages.setValue("1");

		Mockito.doReturn(showMessages).when(appPropertyDAO).find(AppProperty.SYSTEM_MESSAGE);

		Assert.assertTrue(mainPage.isDisplaySystemMessage());
	}

	@Test
	public void testShowSystemMessagesAppPropertySetOther() {
		AppProperty showMessages = new AppProperty();
		showMessages.setProperty("show messages");
		showMessages.setValue("Hello World");
		Mockito.doReturn(showMessages).when(appPropertyDAO).find(AppProperty.SYSTEM_MESSAGE);

		Assert.assertFalse(mainPage.isDisplaySystemMessage());

		showMessages.setValue("0");
		Assert.assertFalse(mainPage.isDisplaySystemMessage());

		Mockito.doReturn(null).when(appPropertyDAO).find(AppProperty.SYSTEM_MESSAGE);
		Assert.assertFalse(mainPage.isDisplaySystemMessage());
	}
}
