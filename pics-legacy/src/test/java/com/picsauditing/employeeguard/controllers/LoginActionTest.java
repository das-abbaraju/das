package com.picsauditing.employeeguard.controllers;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils;
import com.picsauditing.employeeguard.validators.login.LoginFormValidator;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.struts.url.PicsUrlConstants;
import com.picsauditing.util.system.PicsEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.security.auth.login.FailedLoginException;

import static com.picsauditing.employeeguard.EGTestDataUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginActionTest extends PicsActionTest {

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	public static final int EMPLOYEE_ID = 890;
	public static final String EMPLOYEE_COMPANY_NAME = "PICS";

	// Class under test
	private LoginAction loginAction;

	@Mock
	private AccountService accountService;
	@Mock
	private AuthenticationService authenticationService;
	@Mock
	private EmailHashService emailHashService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private PicsEnvironment picsEnvironment;
	@Mock
	private ProfileEntityService profileEntityService;
	@Mock
	private LoginFormValidator loginFormValidator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		loginAction = new LoginAction();

		super.setUp(loginAction);

		Whitebox.setInternalState(loginAction, "accountService", accountService);
		Whitebox.setInternalState(loginAction, "authenticationService", authenticationService);
		Whitebox.setInternalState(loginAction, "emailHashService", emailHashService);
		Whitebox.setInternalState(loginAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(loginAction, "picsEnvironment", picsEnvironment);
		Whitebox.setInternalState(loginAction, "profileEntityService", profileEntityService);

		AccountModel accountModel = new AccountModel.Builder().name("PICS").build();

		when(accountService.getAccountById(anyInt())).thenReturn(accountModel);
		when(picsEnvironment.isLocalhost()).thenReturn(true);
		when(emailHashService.invalidHash(INVALID_EMAIL_HASH)).thenReturn(true);
		when(emailHashService.invalidHash(VALID_EMAIL_HASH)).thenReturn(false);
		when(emailHashService.isUserRegistered(EXISTING_PROFILE_EMAIL_HASH)).thenReturn(true);

		when(emailHashService.findByHash(null)).thenReturn(INVALID_EMAIL_HASH);
		when(emailHashService.findByHash(VALID_EMAIL_HASH_STRING)).thenReturn(VALID_EMAIL_HASH);
		when(emailHashService.findByHash(INVALID_EMAIL_HASH_STRING)).thenReturn(INVALID_EMAIL_HASH);
		when(emailHashService.findByHash(EXISTING_PROFILE_EMAIL_HASH_STRING)).thenReturn(EXISTING_PROFILE_EMAIL_HASH);
		when(emailHashService.findByHash(NO_EMPLOYEE_EMAIL_HASH_STRING)).thenReturn(NO_EMPLOYEE_EMAIL_HASH);
	}

	@Test
	public void testIndex() throws Exception {
		loginAction.setHashCode(VALID_EMAIL_HASH_STRING);

		String result = loginAction.welcome();

		verifyTestIndex(result);
	}

	private void verifyTestIndex(String result) {
		assertEquals(PicsRestActionSupport.LIST, result);
		assertNotNull(loginAction.getProfile());
		assertEquals(VALID_EMAIL_HASH.getEmployee().getFirstName(), loginAction.getProfile().getFirstName());
		assertEquals(VALID_EMAIL_HASH.getEmployee().getLastName(), loginAction.getProfile().getLastName());
		assertEquals(EMPLOYEE_COMPANY_NAME, loginAction.getCompanyName());
		verify(emailHashService).invalidHash(VALID_EMAIL_HASH);
		verify(emailHashService).findByHash(VALID_EMAIL_HASH_STRING);
	}

	@Test
	public void testIndex_HashIsMissing() throws Exception {
		String result = loginAction.welcome();

		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(loginAction.getUrl(), EmployeeGUARDUrlUtils.INVALID_HASH_LINK);
	}

	@Test
	public void testIndex_HashIsInvalid() throws Exception {
		loginAction.setHashCode(INVALID_EMAIL_HASH_STRING);

		String result = loginAction.welcome();

		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(loginAction.getUrl(), EmployeeGUARDUrlUtils.INVALID_HASH_LINK);
	}

	@Test
	public void testIndex_UserAlreadyRegistered() throws Exception {
		loginAction.setHashCode(EXISTING_PROFILE_EMAIL_HASH_STRING);

		String result = loginAction.welcome();

		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(loginAction.getUrl(), PicsUrlConstants.LOGIN_URL);
	}

	@Test(expected = PageNotFoundException.class)
	public void testIndex_EmployeeIsMissing() throws Exception {
		loginAction.setHashCode(NO_EMPLOYEE_EMAIL_HASH_STRING);

		loginAction.welcome();
	}

	@Test
	public void testLogin() throws Exception {
		setupTestLogin();

		String result = loginAction.login();

		verifyTestLogin(result);
	}

	private void setupTestLogin() throws FailedLoginException {
		LoginForm loginForm = new LoginForm();
		loginForm.setUsername(USERNAME);
		loginForm.setPassword(PASSWORD);
		loginForm.setHashCode(VALID_EMAIL_HASH_STRING);

		loginAction.setHashCode(VALID_EMAIL_HASH_STRING);
		loginAction.setLoginForm(loginForm);

		when(authenticationService.authenticateEmployeeGUARDUser(USERNAME, PASSWORD, loginForm.getHashCode(), true))
				.thenReturn("fake cookie content");
	}

	private void verifyTestLogin(String result) {
		assertEquals(PicsActionSupport.REDIRECT, result);
		verify(emailHashService).findByHash(VALID_EMAIL_HASH_STRING);
		verify(profileEntityService).findByAppUserId(anyInt());
		verify(employeeEntityService).linkEmployeeToProfile(any(SoftDeletedEmployee.class), any(Profile.class));
	}

	@Test(expected = PageNotFoundException.class)
	public void testLogin_Failure() throws Exception {
		setupTestLogin_Failure();

		loginAction.login();
	}

	private void setupTestLogin_Failure() {
		LoginForm loginForm = new LoginForm();
		loginForm.setUsername(USERNAME);
		loginForm.setPassword(PASSWORD);
		loginForm.setHashCode(VALID_EMAIL_HASH_STRING);

		loginAction.setLoginForm(loginForm);
	}
}
