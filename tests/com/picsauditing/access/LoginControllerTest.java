package com.picsauditing.access;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

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
		super.setUp(loginController);

		Whitebox.setInternalState(loginController, "userDAO", userDAO);
		Whitebox.setInternalState(loginController, "loginLogDAO", loginLogDAO);
		Whitebox.setInternalState(loginController, "propertyDAO", propertyDAO);
		Whitebox.setInternalState(loginController, "permissionsForTest", permissions);
		Whitebox.setInternalState(loginController, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);

		session.put("somethingToTest", new Integer(21));
		when(user.getAccount()).thenReturn(account);
		when(switchUser.getAccount()).thenReturn(switchAccount);
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
		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
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
		when(permissions.getUserId()).thenReturn(NOT_ZERO);

		String actionResult = loginController.execute();

		verify(permissions).login(switchUser);
		verify(permissions).setAdminID(NOT_ZERO);
		assertThat(actionResult, is(equalTo(PicsActionSupport.REDIRECT)));
		assertThat(session.keySet(), hasItem("permissions"));
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
		verify(permissions).login(user);
		verify(user).setLastLogin((Date) any());
		verify(userDAO).save(user);
		assertThat(actionResult, is(equalTo(PicsActionSupport.REDIRECT)));
		assertThat(session.keySet(), hasItem("permissions"));
	}

	@Test
	public void testPasswordIsIncorrect_Locked() {
		when(user.getFailedAttempts()).thenReturn(8);
		when(user.getUsername()).thenReturn("test");
	}

	@Test
	public void testClearPicsOrgCookie() throws Exception {

	}

	private void normalLoginSetup() {
		loginController.setButton("login");
		loginController.setUsername("test");
		loginController.setPassword("test password");
		when(userDAO.findName("test")).thenReturn(user);
		when(user.getIsActive()).thenReturn(YesNo.Yes);
		when(user.isEncryptedPasswordEqual("test password")).thenReturn(true);
		when(user.getId()).thenReturn(941);
		when(permissions.belongsToGroups()).thenReturn(true);
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getAccountName()).thenReturn("test account");
		when(permissions.hasPermission(OpPerms.Dashboard)).thenReturn(true);
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
	}

}
