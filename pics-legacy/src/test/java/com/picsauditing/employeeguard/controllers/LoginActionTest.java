package com.picsauditing.employeeguard.controllers;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.factory.EmailHashServiceFactory;
import com.picsauditing.employeeguard.services.factory.EmployeeServiceFactory;
import com.picsauditing.employeeguard.services.factory.LoginServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileServiceFactory;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.util.system.PicsEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginActionTest extends PicsActionTest {
	private LoginAction loginAction;

	private EmailHashService emailHashService;
	private EmployeeService employeeService;
	private LoginService loginService;
	private ProfileService profileService;

	@Mock
	private AccountService accountService;
	@Mock
	private AppUserDAO appUserDAO;
	@Mock
	private PicsEnvironment picsEnvironment;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		loginAction = new LoginAction();
		emailHashService = EmailHashServiceFactory.getEmailHashService();
		employeeService = EmployeeServiceFactory.getEmployeeService();
		loginService = LoginServiceFactory.getLoginService();
		profileService = ProfileServiceFactory.getProfileService();

		super.setUp(loginAction);

		Whitebox.setInternalState(loginAction, "accountService", accountService);
		Whitebox.setInternalState(loginAction, "emailHashService", emailHashService);
		Whitebox.setInternalState(loginAction, "employeeService", employeeService);
		Whitebox.setInternalState(loginAction, "loginService", loginService);
		Whitebox.setInternalState(loginAction, "picsEnvironment", picsEnvironment);
		Whitebox.setInternalState(loginAction, "profileService", profileService);

		AccountModel accountModel = new AccountModel.Builder().name("PICS").build();
		when(accountService.getAccountById(anyInt())).thenReturn(accountModel);
		when(picsEnvironment.isLocalhost()).thenReturn(true);
	}

	@Test
	public void testIndex() throws Exception {
		String validHash = EmailHashServiceFactory.VALID_HASH;
		loginAction.setHashCode(validHash);

		assertEquals(PicsRestActionSupport.LIST, loginAction.index());
		assertNotNull(loginAction.getProfile());
		assertEquals("First", loginAction.getProfile().getFirstName());
		assertEquals("Last", loginAction.getProfile().getLastName());
		assertEquals("PICS", loginAction.getCompanyName());
		verify(emailHashService).hashIsValid(validHash);
		verify(emailHashService).findByHash(validHash);
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
		String validHash = EmailHashServiceFactory.VALID_HASH;

		when(emailHashService.findByHash(validHash)).thenReturn(emailHash);

		Whitebox.setInternalState(loginAction, "emailHashService", emailHashService);

		loginAction.setHashCode(validHash);
		loginAction.index();
	}

	@Test
	public void testLogin() throws Exception {
		LoginForm loginForm = new LoginForm();
		loginForm.setUsername(LoginServiceFactory.USERNAME);
		loginForm.setPassword(LoginServiceFactory.PASSWORD);
		loginForm.setHashCode(EmailHashServiceFactory.VALID_HASH);

		String validHash = EmailHashServiceFactory.VALID_HASH;

		AppUser appUser = new AppUser();
		appUser.setId(Identifiable.SYSTEM);
		when(appUserDAO.findListByUserName(LoginServiceFactory.USERNAME)).thenReturn(Arrays.asList(appUser));

		loginAction.setHashCode(validHash);
		loginAction.setLoginForm(loginForm);

		assertEquals(PicsActionSupport.REDIRECT, loginAction.login());
		verify(emailHashService).findByHash(validHash);
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
		verify(employeeService).linkEmployeeToProfile(any(SoftDeletedEmployee.class), any(Profile.class));
	}

	@Test(expected = PageNotFoundException.class)
	public void testLogin_Failure() throws Exception {
		LoginForm loginForm = new LoginForm();
		loginForm.setUsername(LoginServiceFactory.FAIL);
		loginForm.setPassword(LoginServiceFactory.FAIL);
		loginForm.setHashCode(EmailHashServiceFactory.VALID_HASH);

		loginAction.setLoginForm(loginForm);
		loginAction.login();
	}
}
