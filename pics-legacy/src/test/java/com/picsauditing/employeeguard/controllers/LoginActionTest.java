package com.picsauditing.employeeguard.controllers;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.EmailHashBuilder;
import com.picsauditing.employeeguard.entities.builders.SoftDeletedEmployeeBuilder;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.validators.login.LoginFormValidator;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.util.system.PicsEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.security.auth.login.FailedLoginException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginActionTest extends PicsActionTest {

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String VALID_HASH = "valid hash";
	private static final String EMPLOYEE_FIRST_NAME = "First";
	private static final String EMPLOYEE_LAST_NAME = "Last";
	private static final String EMPLOYEE_EMAIL = "tester@picsauditing.com";
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
		when(emailHashService.hashIsValid(VALID_HASH)).thenReturn(true);

		when(emailHashService.findByHash(VALID_HASH)).thenReturn(new EmailHashBuilder()
				.softDeletedEmployee(new SoftDeletedEmployeeBuilder()
						.id(EMPLOYEE_ID)
						.firstName(EMPLOYEE_FIRST_NAME)
						.lastName(EMPLOYEE_LAST_NAME)
						.build())
				.build());
	}

	@Test
	public void testIndex() throws Exception {
		loginAction.setHashCode(VALID_HASH);

		String result = loginAction.index();

		verifyTestIndex(result);
	}

	private void verifyTestIndex(String result) {
		assertEquals(PicsRestActionSupport.LIST, result);
		assertNotNull(loginAction.getProfile());
		assertEquals(EMPLOYEE_FIRST_NAME, loginAction.getProfile().getFirstName());
		assertEquals(EMPLOYEE_LAST_NAME, loginAction.getProfile().getLastName());
		assertEquals(EMPLOYEE_COMPANY_NAME, loginAction.getCompanyName());
		verify(emailHashService).hashIsValid(VALID_HASH);
		verify(emailHashService).findByHash(VALID_HASH);
	}

	@Test(expected = PageNotFoundException.class)
	public void testIndex_HashIsMissing() throws Exception {
		loginAction.index();
	}

	@Test(expected = PageNotFoundException.class)
	public void testIndex_HashIsInvalid() throws Exception {
		loginAction.setHashCode("Invalid hash");
		loginAction.index();
	}

	@Test(expected = PageNotFoundException.class)
	public void testIndex_EmployeeIsMissing() throws Exception {
		EmailHash emailHash = new EmailHash();

		when(emailHashService.findByHash(VALID_HASH)).thenReturn(emailHash);

		Whitebox.setInternalState(loginAction, "emailHashService", emailHashService);

		loginAction.setHashCode(VALID_HASH);
		loginAction.index();
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
		loginForm.setHashCode(VALID_HASH);

		loginAction.setHashCode(VALID_HASH);
		loginAction.setLoginForm(loginForm);

		when(authenticationService.authenticateEmployeeGUARDUser(USERNAME, PASSWORD, true))
				.thenReturn("fake cookie content");
	}

	private void verifyTestLogin(String result) {
		assertEquals(PicsActionSupport.REDIRECT, result);
		verify(emailHashService).findByHash(VALID_HASH);
		verify(profileEntityService).findByAppUserId(anyInt());
		verify(employeeEntityService).linkEmployeeToProfile(any(SoftDeletedEmployee.class), any(Profile.class));
	}

	@Test(expected = PageNotFoundException.class)
	public void testLogin_Failure() throws Exception {
		LoginForm loginForm = new LoginForm();
		loginForm.setUsername(USERNAME);
		loginForm.setPassword(PASSWORD);
		loginForm.setHashCode(VALID_HASH);

		loginAction.setLoginForm(loginForm);
		loginAction.login();
	}
}
