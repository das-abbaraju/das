package com.picsauditing.employeeguard.controllers;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.entities.builder.AppUserBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.EmailHashBuilder;
import com.picsauditing.employeeguard.entities.builders.SoftDeletedEmployeeBuilder;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.service.authentication.UsernameNotAvailableException;
import com.picsauditing.util.system.PicsEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.Cookie;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountActionTest extends PicsActionTest {

	private static final String USER_NAME = "username";
	private static final String PASSWORD = "password";
	private static final String VALID_HASH = "valid hash";
	public static final String EMPLOYEE_LAST_NAME = "last name";
	public static final String EMPLOYEE_FIRST_NAME = "first name";
	public static final String EMPLOYEE_EMAIL = "test@test.com";
	public static final int APP_USER_ID = 78;

	// Class under test
	private AccountAction accountAction;

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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		accountAction = new AccountAction();

		super.setUp(accountAction);

		Whitebox.setInternalState(accountAction, "authenticationService", authenticationService);
		Whitebox.setInternalState(accountAction, "emailHashService", emailHashService);
		Whitebox.setInternalState(accountAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(accountAction, "picsEnvironment", picsEnvironment);
		Whitebox.setInternalState(accountAction, "profileEntityService", profileEntityService);

		when(emailHashService.hashIsValid(VALID_HASH)).thenReturn(true);
	}

	@Test
	public void testIndex() throws Exception {
		accountAction.setHashCode(VALID_HASH);
		setupEmailHashService();

		String result = accountAction.index();

		veriftTestIndex(result);
	}

	private void veriftTestIndex(String result) {
		assertEquals(PicsRestActionSupport.LIST, result);

		assertNotNull(accountAction.getProfile());
		assertEquals(EMPLOYEE_FIRST_NAME, accountAction.getProfile().getFirstName());
		assertEquals(EMPLOYEE_LAST_NAME, accountAction.getProfile().getLastName());
		assertEquals(EMPLOYEE_EMAIL, accountAction.getProfile().getEmail());

		verify(emailHashService).hashIsValid(VALID_HASH);
		verify(emailHashService).findByHash(VALID_HASH);
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
		accountAction.setHashCode(VALID_HASH);

		String result = accountAction.create();

		assertEquals(PicsRestActionSupport.CREATE, result);
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

	@Test
	public void testInsert() throws Exception {
		setupTestInsert();

		String result = accountAction.insert();

		verifyTestInsert(result);
	}

	private void setupTestInsert() throws FailedLoginException {
		ProfileForm profileForm = new ProfileForm();
		profileForm.setEmail(USER_NAME); // username is the email
		profileForm.setPassword(PASSWORD);

		when(picsEnvironment.isLocalhost()).thenReturn(true);

		accountAction.setHashCode(VALID_HASH);
		accountAction.setProfileForm(profileForm);

		when(authenticationService.createNewAppUser(anyString(), anyString())).thenReturn(new AppUserBuilder()
				.id(APP_USER_ID).build());

		when(authenticationService.authenticateEmployeeGUARDUser(USER_NAME, PASSWORD, true))
				.thenReturn("fake cookie content");

		setupEmailHashService();
	}

	private void setupEmailHashService() {
		when(emailHashService.findByHash(VALID_HASH)).thenReturn(new EmailHashBuilder()
				.softDeletedEmployee(new SoftDeletedEmployeeBuilder()
						.lastName(EMPLOYEE_LAST_NAME)
						.firstName(EMPLOYEE_FIRST_NAME)
						.email(EMPLOYEE_EMAIL)
						.build())
				.build());
	}

	private void verifyTestInsert(String result) throws FailedLoginException {
		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(EMPLOYEE_SUMMARY, accountAction.getUrl());

		verify(profileEntityService).save(any(Profile.class), any(EntityAuditInfo.class));
		verify(emailHashService).findByHash(VALID_HASH);
		verify(employeeEntityService).linkEmployeeToProfile(any(SoftDeletedEmployee.class), any(Profile.class));
		verify(emailHashService).expire(any(EmailHash.class));
		verify(authenticationService).authenticateEmployeeGUARDUser(USER_NAME, PASSWORD, true);
		verify(response).addCookie(any(Cookie.class));
	}

	@Test
	public void testInsert_CreateAppUserFailure() throws Exception {
		when(authenticationService.createNewAppUser(USER_NAME, PASSWORD))
				.thenThrow(new UsernameNotAvailableException());

		String result = accountAction.insert();

		assertEquals(PicsActionSupport.ERROR, result);
	}
}
