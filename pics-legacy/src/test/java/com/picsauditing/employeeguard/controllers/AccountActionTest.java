package com.picsauditing.employeeguard.controllers;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.factory.EmailHashServiceFactory;
import com.picsauditing.employeeguard.services.factory.EmployeeServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileServiceFactory;
import com.picsauditing.util.system.PicsEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

public class AccountActionTest extends PicsActionTest {
	private AccountAction accountAction;

	private AppUserService appUserService;
	private EmailHashService emailHashService;
	private EmployeeService employeeService;
	private ProfileService profileService;

	@Mock
	private PicsEnvironment picsEnvironment;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		accountAction = new AccountAction();

//		appUserService = AppUserServiceFactory.getAppUserService();
		emailHashService = EmailHashServiceFactory.getEmailHashService();
		employeeService = EmployeeServiceFactory.getEmployeeService();
//		loginService = LoginServiceFactory.getLoginService();
		profileService = ProfileServiceFactory.getProfileService();

		super.setUp(accountAction);

		Whitebox.setInternalState(accountAction, "appUserService", appUserService);
		Whitebox.setInternalState(accountAction, "emailHashService", emailHashService);
		Whitebox.setInternalState(accountAction, "employeeService", employeeService);
		Whitebox.setInternalState(accountAction, "picsEnvironment", picsEnvironment);
		Whitebox.setInternalState(accountAction, "profileService", profileService);
	}

	@Test
	public void testIndex() throws Exception {
		String validHash = EmailHashServiceFactory.VALID_HASH;

		accountAction.setHashCode(validHash);

		assertEquals(PicsRestActionSupport.LIST, accountAction.index());
		assertNotNull(accountAction.getProfile());
		assertEquals(EmailHashServiceFactory.FIRST_NAME, accountAction.getProfile().getFirstName());
		assertEquals(EmailHashServiceFactory.LAST_NAME, accountAction.getProfile().getLastName());
		assertEquals(EmailHashServiceFactory.EMAIL, accountAction.getProfile().getEmail());
		verify(emailHashService).hashIsValid(validHash);
		verify(emailHashService).findByHash(validHash);
	}

	@Test(expected = PageNotFoundException.class)
	public void testIndex_MissingHash() throws Exception {
		accountAction.index();
	}

	@Test(expected = PageNotFoundException.class)
	public void testIndex_InvalidHash() throws Exception {
		accountAction.setHashCode("Invalid hash");
		accountAction.index();
	}

	@Test
	public void testCreate() throws Exception {
		accountAction.setHashCode(EmailHashServiceFactory.VALID_HASH);
		assertEquals(PicsRestActionSupport.CREATE, accountAction.create());
	}

	@Test(expected = PageNotFoundException.class)
	public void testCreate_MissingHash() throws Exception {
		accountAction.create();
	}

	@Test(expected = PageNotFoundException.class)
	public void testCreate_InvalidHash() throws Exception {
		accountAction.setHashCode("Invalid hash");
		accountAction.create();
	}

//	@Test
//	public void testInsert() throws Exception {
//		String username = AppUserServiceFactory.USERNAME;
//		ProfileForm profileForm = new ProfileForm();
//		profileForm.setEmail(username);
//		profileForm.setPassword(AppUserServiceFactory.PASSWORD);
//
//		when(picsEnvironment.isLocalhost()).thenReturn(true);
//
//		accountAction.setHashCode(EmailHashServiceFactory.VALID_HASH);
//		accountAction.setProfileForm(profileForm);
//
//		assertEquals(PicsActionSupport.REDIRECT, accountAction.insert());
//		assertEquals("/employee-guard/employee/dashboard", accountAction.getUrl());
//		verify(appUserService).createNewAppUser(username, AppUserServiceFactory.PASSWORD);
//		verify(profileService).create(any(Profile.class));
//		verify(emailHashService).findByHash(EmailHashServiceFactory.VALID_HASH);
//		verify(employeeService).linkEmployeeToProfile(any(SoftDeletedEmployee.class), any(Profile.class));
//		verify(emailHashService).expire(any(EmailHash.class));
//		verify(loginService).loginViaRest(username, AppUserServiceFactory.PASSWORD);
//		verify(response).addCookie(any(Cookie.class));
//	}
//
//	@Test
//	public void testInsert_CreateAppUserFailure() throws Exception {
//		ProfileForm profileForm = new ProfileForm();
//		profileForm.setEmail(AppUserServiceFactory.FAIL);
//		profileForm.setPassword(AppUserServiceFactory.FAIL);
//
//		accountAction.setProfileForm(profileForm);
//
//		assertEquals(PicsActionSupport.ERROR, accountAction.insert());
//	}
//
//	@Test(expected = FailedLoginException.class)
//	public void testInsert_FailedLoginViaRest() throws Exception {
//		when(loginService.loginViaRest(anyString(), anyString())).thenReturn((JSONObject) JSONValue.parse("{\"status\":\"FAILURE\"}"));
//
//		ProfileForm profileForm = new ProfileForm();
//		profileForm.setEmail(AppUserServiceFactory.USERNAME);
//		profileForm.setPassword(AppUserServiceFactory.PASSWORD);
//
//		accountAction.setHashCode(EmailHashServiceFactory.VALID_HASH);
//		accountAction.setProfileForm(profileForm);
//
//		accountAction.insert();
//	}
}
