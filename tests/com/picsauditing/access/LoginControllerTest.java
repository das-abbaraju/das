package com.picsauditing.access;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.security.CookieSupport;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class LoginControllerTest extends PicsActionTest {
	private LoginController loginController;
	private int NOT_ZERO = 1;
	private int SWITCH_USER_ID = 2;

	@Mock
	private UserDAO userDAO;
	@Mock
	protected AppPropertyDAO propertyDAO;
	@Mock
	private UserLoginLogDAO loginLogDAO;
	@Mock
	private User user;
	@Mock
	private User switchUser;
	@Mock
	private Account account;
	@Mock
	private Account switchAccount;
	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private FeatureToggle featureToggleChecker;
	@Mock
	private HierarchyBuilder hierarchyBuilder;
	@Mock
	private UserService userService;
	@Mock
	private LanguageModel languageModel;

	private LoginService loginService = new LoginService();

	@Spy
	private PermissionBuilder permissionBuilder;

	@AfterClass
	public static void tearDown() throws Exception {
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", (ApplicationContext) null);
	}

	@Before
	public void setUp() throws Exception {
		System.setProperty("sk",
				"87hsbhW3PaIlmYB9FEM6rclCc0sGiIfq3tRpGKQFw8ynTFrUU6XQqg7oYk4DXQBkAqdYnGqvDMKRCfwiWOSoVg==");

		MockitoAnnotations.initMocks(this);
		loginController = new LoginController();
//		permissionBuilder = new PermissionBuilder();
		super.setUp(loginController);

		Whitebox.setInternalState(permissionBuilder, "featureToggle", featureToggleChecker);
		Whitebox.setInternalState(permissionBuilder, "hierarchyBuilder", hierarchyBuilder);
		Whitebox.setInternalState(permissionBuilder, "dao", userDAO);
		Whitebox.setInternalState(loginController, "permissionBuilder", permissionBuilder);
		Whitebox.setInternalState(loginController, "userDAO", userDAO);
		Whitebox.setInternalState(loginController, "loginLogDAO", loginLogDAO);
		Whitebox.setInternalState(loginController, "propertyDAO", propertyDAO);
		Whitebox.setInternalState(loginController, "permissions", permissions);
		Whitebox.setInternalState(loginController, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(loginService, "userService", userService);
		Whitebox.setInternalState(loginController, "loginService", loginService);
		Whitebox.setInternalState(loginController, "supportedLanguages", languageModel);
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);

		session.put("somethingToTest", new Integer(21));
		when(user.getAccount()).thenReturn(account);
		when(switchUser.getAccount()).thenReturn(switchAccount);
		when(request.getServerName()).thenReturn("www.picsorganizer.com");
		when(userDAO.findName(anyString())).thenReturn(user);
		when(userDAO.find(941)).thenReturn(user);
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_PERMISSION_GROUPS)).thenReturn(true);
//		when(userDAO.find(anyInt())).thenReturn(user);
//		when(user.getId()).thenReturn(1);
//		when(permissionBuilder.login(user)).thenReturn(permissions);
		when(userService.loadUserByUsername(anyString())).thenReturn(user);

	}

	@Test(expected = Exception.class)
	public void testSetRedirectUrlPostLogin_NoRedirectUrlThrows() throws Exception {
		Whitebox.setInternalState(loginController, "user", user);

		LoginService loginService = mock(LoginService.class);
		when(loginService.postLoginHomePageTypeForRedirect(null, user)).thenReturn(null);
		Whitebox.setInternalState(loginController, "loginService", loginService);

		Whitebox.invokeMethod(loginController, "setRedirectUrlPostLogin");
	}

	@Test
	public void testSetRedirectUrlPostLogin_PreLogin() throws Exception {
		Whitebox.setInternalState(loginController, "user", user);

		final String PRELOGIN_URL = "/PreLoginUrl";
		Cookie cookie1 = mock(Cookie.class);
		when(cookie1.getName()).thenReturn(CookieSupport.PRELOGIN_URL_COOKIE_NAME);
		when(cookie1.getValue()).thenReturn(PRELOGIN_URL);
		when(request.getCookies()).thenReturn(new Cookie[] { cookie1 });

		LoginService loginService = mock(LoginService.class);
		when(loginService.postLoginHomePageTypeForRedirect(PRELOGIN_URL, user)).thenReturn(HomePageType.PreLogin);
		Whitebox.setInternalState(loginController, "loginService", loginService);

		String strutsResult = Whitebox.invokeMethod(loginController, "setRedirectUrlPostLogin");

		assertThat(PicsActionSupport.REDIRECT, is(equalTo(strutsResult)));
		assertThat(PRELOGIN_URL, is(equalTo(loginController.getUrl())));
	}

	@Test
	public void testSetRedirectUrlPostLogin_ContractorRegistrationStep() throws Exception {
		Whitebox.setInternalState(loginController, "user", user);
		ContractorAccount account = mock(ContractorAccount.class);
		when(user.getAccount()).thenReturn(account);

		LoginService loginService = mock(LoginService.class);
		when(loginService.postLoginHomePageTypeForRedirect(null, user)).thenReturn(
				HomePageType.ContractorRegistrationStep);
		Whitebox.setInternalState(loginController, "loginService", loginService);

		String strutsResult = Whitebox.invokeMethod(loginController, "setRedirectUrlPostLogin");

		assertThat(PicsActionSupport.REDIRECT, is(equalTo(strutsResult)));
		assertThat(ContractorRegistrationStep.Register.getUrl(), is(equalTo(loginController.getUrl())));
	}

	@Test
	public void testSetRedirectUrlPostLogin_Deactivated() throws Exception {
		Whitebox.setInternalState(loginController, "user", user);

		LoginService loginService = mock(LoginService.class);
		when(loginService.postLoginHomePageTypeForRedirect(null, user)).thenReturn(
				HomePageType.Deactivated);
		Whitebox.setInternalState(loginController, "loginService", loginService);

		String strutsResult = Whitebox.invokeMethod(loginController, "setRedirectUrlPostLogin");

		assertThat(PicsActionSupport.REDIRECT, is(equalTo(strutsResult)));
		assertThat(LoginController.DEACTIVATED_ACCOUNT_PAGE, is(equalTo(loginController.getUrl())));
	}

    @Test
    public void testSetRedirectUrlPostLogin_DeclinedAlsoRedirectsToDeactivatedPage() throws Exception {
        Whitebox.setInternalState(loginController, "user", user);

        LoginService loginService = mock(LoginService.class);
        when(loginService.postLoginHomePageTypeForRedirect(null, user)).thenReturn(
                HomePageType.Declined);
        Whitebox.setInternalState(loginController, "loginService", loginService);

        String strutsResult = Whitebox.invokeMethod(loginController, "setRedirectUrlPostLogin");

        assertThat(PicsActionSupport.REDIRECT, is(equalTo(strutsResult)));
        assertThat(LoginController.DEACTIVATED_ACCOUNT_PAGE, is(equalTo(loginController.getUrl())));
    }

    // As a non-admin user
	// Given user wishes to logout
	// When user clicks on logout button
	// Then the system clears permissions and session
	@Test
	public void testExecute_logoutNotAdmin() throws Exception {
		loginController.setButton("logout");

		String actionResult = loginController.execute();

		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
		verify(permissions).clear();
		assertTrue(session.values().isEmpty());
	}

	// As a non-admin user
	// Given user wishes to logout
	// When user clicks on logout button
	// And user-agent's cookies are disabled
	// Then the system adds an action message
	// And returns success
	// But does not clear session or permissions
	@Test
	public void testExecute_logoutNotAdminCookiesDisabled() throws Exception {
		String testMessage = "Test Message";
		loginController.setButton("login");
		when(i18nCache.getText(eq("Login.CookiesAreDisabled"), eq(Locale.ENGLISH), any())).thenReturn(testMessage);
		when(request.getCookies()).thenReturn(null);

		String actionResult = loginController.execute();

		assertThat(loginController.getActionMessages(), hasItem(testMessage));
		assertThat(actionResult, is(equalTo(Action.ERROR)));
		verify(permissions, never()).clear();
		assertFalse(session.values().isEmpty());
	}

	// As a user who has switched to another user
	// Given user wishes to logout as the switched to user
	// When user clicks on logout button
	// And does not clear session
	// Then the system logs out everybody
	@Test
	public void testExecute_logoutUserWhoHasSwitchedToAnotherUser() throws Exception {
		loginController.setButton("logout");
		when(permissions.getAdminID()).thenReturn(NOT_ZERO);
		when(userDAO.find(NOT_ZERO)).thenReturn(user);

		String actionResult = loginController.execute();

		assertThat(actionResult, is(equalTo(PicsActionSupport.SUCCESS)));
		verify(permissions).clear();
		verify(response).addCookie((Cookie) any());
	}

	// As a user responding to the confirmation email
	// Given the button is "confirm"
	// And username is set
	// When the user clicks the link
	// Then the system saves the confirmation date
	// And action messages the user they're confirmed
	@Test
	public void testExecute_Confirm() throws Exception {
		String testMessage = "You are confirmed";
		loginController.setButton("confirm");
		loginController.setUsername("test");
		when(userDAO.findName("test")).thenReturn(user);
		when(i18nCache.getText(eq("Login.ConfirmedEmailAddress"), eq(Locale.ENGLISH), any())).thenReturn(testMessage);

		String actionResult = loginController.execute();

		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
		verify(user).setEmailConfirmedDate((Date) any());
		assertThat(loginController.getActionMessages(), hasItem(testMessage));
		verify(userDAO).save(user);
	}

	// As a user responding to the confirmation email
	// Given the button is "confirm"
	// When the user clicks the link
	// And the system fails to find the username
	// Then the system does not save the confirmation date
	// And action messages informs the user they're NOT confirmed
	@Test
	public void testExecute_ConfirmFailure() throws Exception {
		String testMessage = "You are NOT confirmed";
		loginController.setButton("confirm");
		loginController.setUsername("test");
		when(userDAO.findName("test")).thenReturn(null);
		when(i18nCache.getText(eq("Login.AccountConfirmationFailed"), eq(Locale.ENGLISH), any())).thenReturn(
				testMessage);

		String actionResult = loginController.execute();

		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
		verify(user, never()).setEmailConfirmedDate((Date) any());
		assertThat(loginController.getActionErrors(), hasItem(testMessage));
		verify(userDAO, never()).save(user);
	}

	// As a logged in user
	// Given user wishes to switch to another user
	@Test
	public void testExcecute_SwitchToUser() throws Exception {
		normalLoginSetup();
		when(permissions.hasPermission(OpPerms.SwitchUser)).thenReturn(true);
		loginController.setSwitchToUser(SWITCH_USER_ID);
		when(userDAO.find(SWITCH_USER_ID)).thenReturn(switchUser);
		when(switchUser.getId()).thenReturn(SWITCH_USER_ID);
		when(permissions.getUserId()).thenReturn(NOT_ZERO);
		when(switchUser.getLocale()).thenReturn(Locale.ENGLISH);

		String actionResult = loginController.execute();

		verify(permissionBuilder).login(switchUser);

		assertThat(actionResult, is(equalTo(PicsActionSupport.REDIRECT)));
		assertThat(session.keySet(), hasItem("permissions"));
		assertEquals(SWITCH_USER_ID, ((Permissions) session.get("permissions")).getUserId());
		assertEquals(NOT_ZERO, ((Permissions) session.get("permissions")).getAdminID());
	}

	// As a pics user
	// Given user tries to login
	// And has valid credentials
	// And is active
	// And belongs to groups
	// When the user enters valid username and password
	// And clicks "login"
	// Then the system clears previous perms
	// And logs in the user
	// And logs the login attempt
	// And sets last login date
	@Test
	public void testExecute_NormalLogin() throws Exception {
		normalLoginSetup();
		String actionResult = loginController.execute();
		verify(permissionBuilder).login(user);
		verify(user).setLastLogin((Date) any());
		verify(userDAO).save(user);
		assertThat(actionResult, is(equalTo(PicsActionSupport.REDIRECT)));
		assertThat(session.keySet(), hasItem("permissions"));
	}

	@Test
	public void testLogAndMessageError_NullErrorReturnsFalse() throws Exception {
		assertFalse((Boolean) Whitebox.invokeMethod(loginController, "logAndMessageError", (String) null));
	}

	@Test
	public void testLogAndMessageError_EmptyErrorReturnsFalse() throws Exception {
		assertFalse((Boolean) Whitebox.invokeMethod(loginController, "logAndMessageError", ""));
	}

	@Test
	public void testLogAndMessageError_ErrorClearsSession() throws Exception {
		assertTrue((Boolean) Whitebox.invokeMethod(loginController, "logAndMessageError", "Error"));
		assertTrue(session.isEmpty());
	}

	@Test
	public void testLogAndMessageError_ErrorLogsAttempt() throws Exception {
		Whitebox.setInternalState(loginController, "user", user);
		assertTrue((Boolean) Whitebox.invokeMethod(loginController, "logAndMessageError", "Error"));
		verify(loginLogDAO).save((UserLoginLog) any());
	}

	@Test
	public void testloginForResetPassword_SetsForcePasswordReset() throws Exception {
		when(user.getId()).thenReturn(NOT_ZERO);
		when(user.getLocale()).thenReturn(Locale.ENGLISH);

		loginController.setButton("reset");
		loginController.execute();

		verify(user).setForcePasswordReset(true);
	}

	@Test
	public void testExecuteReset_NullUserGivesErrorMessage() throws Exception {
		when(userService.loadUserByUsername(anyString())).thenReturn(null);
		when(i18nCache.hasKey(eq("Login.PasswordIncorrect"), (Locale) any())).thenReturn(true);
		when(i18nCache.getText(eq("Login.PasswordIncorrect"), (Locale) any(), anyVararg()))
				.thenReturn("Password incorrect");

		loginController.setButton("reset");
		loginController.execute();

		assertTrue(loginController.getActionErrors().contains("Password incorrect"));
	}

	@Test
	public void testLogAttempt_NullUserDoesNotPersistLog() throws Exception {
		Whitebox.setInternalState(loginController, "user", (User) null);

		Whitebox.invokeMethod(loginController, "logAttempt");

		verify(loginLogDAO, never()).save((UserLoginLog) any());
	}

	@Test
	public void testLogAttempt_BigIpCookieIpGetsPersisted() throws Exception {
		Whitebox.setInternalState(loginController, "user", user);
		Cookie cookie1 = mock(Cookie.class);
		when(cookie1.getName()).thenReturn("BIGipServerPOOL-74.205.45.70-81");
		when(cookie1.getValue()).thenReturn("1664397834.20736.0000");
		when(request.getCookies()).thenReturn(new Cookie[] { cookie1 });

		Whitebox.invokeMethod(loginController, "logAttempt");
		ArgumentCaptor<UserLoginLog> captor = ArgumentCaptor.forClass(UserLoginLog.class);

		verify(loginLogDAO).save(captor.capture());

		UserLoginLog log = captor.getValue();

		assertThat(log.getTargetIP(), is(equalTo("74.205.45.70")));
	}

	@Test
	public void testExtractTargetIpFromCookie() throws Exception {
		Cookie cookie1 = mock(Cookie.class);
		when(cookie1.getName()).thenReturn("BIGipServerPOOL-74.205.45.70-81");
		when(cookie1.getValue()).thenReturn("1664397834.20736.0000");
		Cookie cookie2 = mock(Cookie.class);
		when(cookie2.getName()).thenReturn("from");
		when(cookie2.getValue()).thenReturn("/Home.action");

		when(request.getCookies()).thenReturn(new Cookie[] { cookie1, cookie2 });

		String targetIp = Whitebox.invokeMethod(loginController, "extractTargetIpFromCookie");

		assertTrue("74.205.45.70".equals(targetIp));
	}

	@Test
	public void testGetPreLoginUrl_StartsWithDoubleQuote() throws Exception {
		Cookie cookie = mock(Cookie.class);
		when(cookie.getName()).thenReturn("from");
		when(cookie.getValue()).thenReturn("\"/Home.action");
		when(request.getCookies()).thenReturn(new Cookie[] { cookie });

		String urlPreLogin = Whitebox.invokeMethod(loginController, "getPreLoginUrl");

		assertFalse(urlPreLogin.contains("\""));
	}

	@Test
	public void testGetPreLoginUrl() throws Exception {
		Cookie cookie = mock(Cookie.class);
		when(cookie.getName()).thenReturn("from");
		when(cookie.getValue()).thenReturn("/ContractorDashboard.action?foo=1");
		when(request.getCookies()).thenReturn(new Cookie[] { cookie });

		String urlPreLogin = Whitebox.invokeMethod(loginController, "getPreLoginUrl");

		assertTrue("/ContractorDashboard.action?foo=1".equals(urlPreLogin));
	}

	@Test
	public void testExecute_RedirectNormallyIfPasswordNotExpired() throws Exception {
		normalLoginSetup();
		when(userService.isPasswordExpired(user)).thenReturn(false);

		String actionResult = loginController.execute();

		assertEquals(actionResult, PicsActionSupport.REDIRECT);
		assertEquals(loginController.getUrl(), LoginController.LOGIN_ACTION_BUTTON_LOGOUT);

	}

	@Test
	public void testExecute_RedirectToAccountRecoveryIfPasswordExpired() throws Exception {
		normalLoginSetup();
		when(userService.isPasswordExpired(user)).thenReturn(true);

		String actionResult = loginController.execute();

		assertEquals(actionResult, PicsActionSupport.REDIRECT);
		assertEquals(loginController.getUrl(), LoginController.ACCOUNT_RECOVERY_ACTION + user.getUsername());

	}

	@Test
	public void testLogout_BetaLanguageDefaultsLocaleToEnglish() throws Exception {
		Locale germany = Locale.GERMANY;

		ActionContext.getContext().setLocale(germany);
		when(languageModel.isLanguageVisible(germany)).thenReturn(false);
		when(permissions.getLocale()).thenReturn(germany);

		Whitebox.invokeMethod(loginController, "logout");

		assertTrue(session.isEmpty());
		verify(permissions).clear();
		assertEquals(LanguageModel.ENGLISH, ActionContext.getContext().getLocale());
	}

	@Test
	public void testLogout_StableLanguageKeepsSameLocale() throws Exception {
		Locale italy = Locale.ITALY;

		ActionContext.getContext().setLocale(italy);
		when(languageModel.isLanguageVisible(italy)).thenReturn(true);
		when(permissions.getLocale()).thenReturn(italy);

		Whitebox.invokeMethod(loginController, "logout");

		assertTrue(session.isEmpty());
		verify(permissions).clear();
		assertEquals(italy, ActionContext.getContext().getLocale());
	}

	private void normalLoginSetup() {
		loginController.setButton("login");
		loginController.setUsername("test");
		loginController.setPassword("test password");
		when(userDAO.findName("test")).thenReturn(user);
		when(user.getIsActive()).thenReturn(YesNo.Yes);
		when(user.getUsername()).thenReturn("joesixpack");
		when(user.isEncryptedPasswordEqual("test password")).thenReturn(true);
		when(user.getId()).thenReturn(941);
		when(user.getLocale()).thenReturn(Locale.ENGLISH);
		when(permissions.belongsToGroups()).thenReturn(true);
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getAccountName()).thenReturn("test account");
		when(permissions.hasPermission(OpPerms.Dashboard)).thenReturn(true);
		when(userService.isUserActive(user)).thenReturn(true);
	}
}
