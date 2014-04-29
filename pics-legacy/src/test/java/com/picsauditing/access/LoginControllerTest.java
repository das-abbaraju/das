package com.picsauditing.access;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.user.UserModeProvider;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.security.CookieSupport;
import com.picsauditing.service.user.UserService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.hierarchy.HierarchyBuilder;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.Cookie;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class LoginControllerTest extends PicsActionTest {
	private LoginController loginController;
	private int NOT_ZERO = 1;
	private int SWITCH_USER_ID = 2;

	@Mock
	protected AppPropertyDAO propertyDAO;
	@Mock
	private UserLoginLogDAO loginLogDAO;
	@Mock
	private AppUserDAO appUserDAO;
	@Mock
	private ProfileEntityService profileEntityService;
	@Mock
	private AppUser appUser;
	@Mock
	private User user;
	@Mock
	private User switchUser;
	@Mock
	private Account account;
	@Mock
	private Account switchAccount;
	@Mock
	private FeatureToggle featureToggleChecker;
	@Mock
	private HierarchyBuilder hierarchyBuilder;
	@Mock
	private UserService userService;
	@Mock
	private LanguageModel languageModel;
	@Mock
	private ReportUserDAO reportUserDAO;
	@Mock
	private UserModeProvider userModeProvider;
    @Mock
    private AppUserService appUserService;

	private LoginService loginService;


	@Spy
	private PermissionBuilder permissionBuilder;

	@AfterClass
	public static void tearDown() throws Exception {
		PicsActionTest.classTearDown();
		PicsTestUtil.resetSpringUtilsBeans();
	}

	@Before
	public void setUp() throws Exception {
		loginService = new LoginService();

		System.setProperty("sk",
				"87hsbhW3PaIlmYB9FEM6rclCc0sGiIfq3tRpGKQFw8ynTFrUU6XQqg7oYk4DXQBkAqdYnGqvDMKRCfwiWOSoVg==");

		MockitoAnnotations.initMocks(this);
		LoginController.reportUserDAO = reportUserDAO;
		loginController = new LoginController();
		super.setUp(loginController);

		Whitebox.setInternalState(permissionBuilder, "hierarchyBuilder", hierarchyBuilder);
		Whitebox.setInternalState(permissionBuilder, "userService", userService);
		Whitebox.setInternalState(permissionBuilder, "userModeProvider", userModeProvider);
		Whitebox.setInternalState(loginController, "permissionBuilder", permissionBuilder);
		Whitebox.setInternalState(loginController, "loginLogDAO", loginLogDAO);
		Whitebox.setInternalState(loginController, "profileEntityService", profileEntityService);
		Whitebox.setInternalState(loginController, "propertyDAO", propertyDAO);
		Whitebox.setInternalState(loginController, "permissions", permissions);
		Whitebox.setInternalState(loginController, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(loginService, "userService", userService);
		Whitebox.setInternalState(loginController, "userService", userService);
		Whitebox.setInternalState(loginController, "loginService", loginService);
		Whitebox.setInternalState(loginController, "supportedLanguages", languageModel);
		Whitebox.setInternalState(loginController, "appUserService", appUserService);

		setupSpringUtils();

		session.put("somethingToTest", new Integer(21));
		when(user.getAccount()).thenReturn(account);
		when(switchUser.getAccount()).thenReturn(switchAccount);
		when(request.getServerName()).thenReturn("www.picsorganizer.com");
		when(userService.findByName(anyString())).thenReturn(user);
		when(userService.findById(941)).thenReturn(user);
		when(userService.loadUserByUsername(anyString())).thenReturn(user);
		when(appUserService.findAppUser(anyString())).thenReturn(appUser);

		List<AppUser> appUserList = new ArrayList<>();
		appUserList.add(new AppUser());

		when(appUserDAO.findListByUserName(anyString())).thenReturn(appUserList);
		when(profileEntityService.findByAppUserId(anyInt())).thenReturn(new Profile());

		JSONObject result = new JSONObject();
		result.put("status", "SUCCESS");
		result.put("cookie", "whatevz");
	}

	private void setupSpringUtils() {
		PicsTestUtil.setSpringUtilsBeans(new HashMap<String, Object>());
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
		when(request.getCookies()).thenReturn(new Cookie[]{cookie1});

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
		when(loginService.postLoginHomePageTypeForRedirect(null, user)).thenReturn(HomePageType.Deactivated);
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
	@SuppressWarnings("deprecation")
	@Test
	public void testExecute_logoutNotAdminCookiesDisabled() throws Exception {
		String testMessage = "Test Message";
		loginController.setButton("login");

		when(translationService.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq("Login.CookiesAreDisabled"), eq(Locale.ENGLISH), any())).thenReturn(
				testMessage);
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
		when(userService.findById(NOT_ZERO)).thenReturn(user);

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
	@SuppressWarnings("deprecation")
	@Test
	public void testExecute_Confirm() throws Exception {
		String testMessage = "You are confirmed";
		loginController.setButton("confirm");
		loginController.setUsername("test");
		when(userService.findByName("test")).thenReturn(user);

		when(translationService.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq("Login.ConfirmedEmailAddress"), eq(Locale.ENGLISH), any())).thenReturn(
				testMessage);

		String actionResult = loginController.execute();

		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
		verify(user).setEmailConfirmedDate((Date) any());
		assertThat(loginController.getActionMessages(), hasItem(testMessage));
		verify(userService).saveUser(user);
	}

	// As a user responding to the confirmation email
	// Given the button is "confirm"
	// When the user clicks the link
	// And the system fails to find the username
	// Then the system does not save the confirmation date
	// And action messages informs the user they're NOT confirmed
	@SuppressWarnings("deprecation")
	@Test
	public void testExecute_ConfirmFailure() throws Exception {
		String testMessage = "You are NOT confirmed";
		loginController.setButton("confirm");
		loginController.setUsername("test");

		when(userService.findByName("test")).thenReturn(null);
		when(translationService.hasKey(anyString(), any(Locale.class))).thenReturn(true);
		when(translationService.getText(eq("Login.AccountConfirmationFailed"), eq(Locale.ENGLISH), any())).thenReturn(
				testMessage);

		String actionResult = loginController.execute();

		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
		verify(user, never()).setEmailConfirmedDate((Date) any());
		assertThat(loginController.getActionErrors(), hasItem(testMessage));
		verify(userService, never()).saveUser(user);
	}

	// As a logged in user
	// Given user wishes to switch to another user
	@Test
	public void testExecute_SwitchToUser() throws Exception {
		normalLoginSetup();
		when(permissions.hasPermission(OpPerms.SwitchUser)).thenReturn(true);
		loginController.setSwitchToUser(SWITCH_USER_ID);
		when(userService.findById(SWITCH_USER_ID)).thenReturn(switchUser);
		when(switchUser.getAppUser()).thenReturn(appUser);
		when(switchUser.getId()).thenReturn(SWITCH_USER_ID);
		when(switchUser.isUsingVersion7Menus()).thenReturn(true);
		when(permissions.getUserId()).thenReturn(NOT_ZERO);
		when(switchUser.getLocale()).thenReturn(Locale.ENGLISH);
		Account account = new Account();
		account.setType("Admin");
		when(switchUser.getAccount()).thenReturn(account);

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
		Account account = new Account();
		account.setType("Admin");
		when(userService.findById(anyInt())).thenReturn(user);
		when(user.getAccount()).thenReturn(account);
		String actionResult = loginController.execute();
		verify(permissionBuilder).login(user);
		verify(userService).updateUserForSuccessfulLogin(user);
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

	@SuppressWarnings("deprecation")
	@Test
	public void testExecuteReset_NullUserGivesErrorMessage() throws Exception {
		when(userService.loadUserByUsername(anyString())).thenReturn(null);
		when(translationService.hasKey(eq("Login.PasswordIncorrect"), (Locale) any())).thenReturn(true);
		when(translationService.getText(eq("Login.PasswordIncorrect"), (Locale) any(), anyVararg())).thenReturn(
				"Password incorrect");

		loginController.setButton("reset");
		loginController.execute();

		assertTrue(loginController.getActionErrors().contains("Password incorrect"));
	}

	@Test
	public void testGetPreLoginUrl_StartsWithDoubleQuote() throws Exception {
		Cookie cookie = mock(Cookie.class);
		when(cookie.getName()).thenReturn("from");
		when(cookie.getValue()).thenReturn("\"/Home.action");
		when(request.getCookies()).thenReturn(new Cookie[]{cookie});

		String urlPreLogin = Whitebox.invokeMethod(loginController, "getPreLoginUrl");

		assertFalse(urlPreLogin.contains("\""));
	}

	@Test
	public void testGetPreLoginUrl() throws Exception {
		Cookie cookie = mock(Cookie.class);
		when(cookie.getName()).thenReturn("from");
		when(cookie.getValue()).thenReturn("/ContractorDashboard.action?foo=1");
		when(request.getCookies()).thenReturn(new Cookie[]{cookie});

		String urlPreLogin = Whitebox.invokeMethod(loginController, "getPreLoginUrl");

		assertTrue("/ContractorDashboard.action?foo=1".equals(urlPreLogin));
	}

	@Test
	public void testExecute_RedirectNormallyIfPasswordNotExpired() throws Exception {
		normalLoginSetup();
		when(userService.isPasswordExpired(user)).thenReturn(false);
		when(userService.findById(anyInt())).thenReturn(user);
		Account account = new Account();
		account.setType("Admin");
		when(user.getAccount()).thenReturn(account);

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
		when(userService.findByName("test")).thenReturn(user);
		when(userService.findByAppUserId(anyInt())).thenReturn(user);
		when(user.getAppUser()).thenReturn(appUser);
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
