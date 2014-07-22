package com.picsauditing.employeeguard.controllers;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.entities.builder.AppUserBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.service.authentication.UsernameNotAvailableException;
import com.picsauditing.struts.url.PicsUrlConstants;
import com.picsauditing.util.system.PicsEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.Cookie;

import java.util.Locale;

import static com.picsauditing.employeeguard.EGTestDataUtil.*;
import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountActionTest extends PicsActionTest {

	private static final String USER_NAME = "username";
	private static final String PASSWORD = "password";
	public static final int APP_USER_ID = 78;

	// Class under test
	private AccountAction accountAction;

	@Mock
	private ActionInvocation actionInvocation;
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

		when(emailHashService.invalidHash(INVALID_EMAIL_HASH)).thenReturn(true);
		when(emailHashService.invalidHash(null)).thenReturn(true);
		when(emailHashService.findByHash(INVALID_EMAIL_HASH_STRING)).thenReturn(INVALID_EMAIL_HASH);
		when(emailHashService.findByHash(VALID_EMAIL_HASH_STRING)).thenReturn(VALID_EMAIL_HASH);
		when(emailHashService.isUserRegistered(EXISTING_PROFILE_EMAIL_HASH)).thenReturn(true);
		when(emailHashService.findByHash(EXISTING_PROFILE_EMAIL_HASH_STRING)).thenReturn(EXISTING_PROFILE_EMAIL_HASH);
	}

	@Test
	public void testIndex_ExistingProfile() throws Exception {
		accountAction.setHashCode(EXISTING_PROFILE_EMAIL_HASH_STRING);

		String result = accountAction.index();

		assertEquals(PicsRestActionSupport.REDIRECT, result);
		assertEquals(accountAction.getUrl(), PicsUrlConstants.LOGIN_URL);
	}

	@Test
	public void testIndex() throws Exception {
		accountAction.setHashCode(VALID_EMAIL_HASH_STRING);

		String result = accountAction.index();

		verifyTestIndex(result);
	}

	private void verifyTestIndex(String result) {
		assertEquals(AccountAction.SIGN_UP_VIEW, result);

		assertEquals(VALID_EMAIL_HASH.getEmployee().getFirstName(), accountAction.getProfile().getFirstName());
		assertEquals(VALID_EMAIL_HASH.getEmployee().getLastName(), accountAction.getProfile().getLastName());
		assertEquals(VALID_EMAIL_HASH.getEmployee().getEmail(), accountAction.getProfile().getEmail());

		verify(emailHashService).invalidHash(VALID_EMAIL_HASH);
		verify(emailHashService).findByHash(VALID_EMAIL_HASH_STRING);
	}

	@Test
	public void testIndex_MissingHash() throws Exception {
		String result = accountAction.index();

		assertEquals(PicsRestActionSupport.REDIRECT, result);
		assertEquals(accountAction.getUrl(), EmployeeGUARDUrlUtils.INVALID_HASH_LINK);
	}

	@Test
	public void testIndex_InvalidHash() throws Exception {
		accountAction.setHashCode(INVALID_EMAIL_HASH_STRING);

		String result = accountAction.index();

		assertEquals(PicsRestActionSupport.REDIRECT, result);
		assertEquals(accountAction.getUrl(), EmployeeGUARDUrlUtils.INVALID_HASH_LINK);
	}

	@Test
	public void testCreate() throws Exception {
		accountAction.setHashCode(VALID_EMAIL_HASH_STRING);

		String result = accountAction.create();

		assertEquals(AccountAction.SIGN_UP_VIEW, result);
	}

	@Test
	public void testCreate_MissingHash() throws Exception {
		String result = accountAction.create();

		assertEquals(PicsRestActionSupport.REDIRECT, result);
		assertEquals(accountAction.getUrl(), EmployeeGUARDUrlUtils.INVALID_HASH_LINK);
	}

	@Test
	public void testCreate_ExistingProfile() throws Exception {
		accountAction.setHashCode(EXISTING_PROFILE_EMAIL_HASH_STRING);

		String result = accountAction.create();

		assertEquals(PicsRestActionSupport.REDIRECT, result);
		assertEquals(accountAction.getUrl(), PicsUrlConstants.LOGIN_URL);
	}

	@Test
	public void testCreate_InvalidHash() throws Exception {
		accountAction.setHashCode("Invalid hash");

		String result = accountAction.create();

		assertEquals(PicsActionSupport.REDIRECT, result);
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

		accountAction.setHashCode(VALID_EMAIL_HASH_STRING);
		accountAction.setProfileForm(profileForm);

		when(authenticationService.createNewAppUser(anyString(), anyString())).thenReturn(new AppUserBuilder()
				.id(APP_USER_ID).build());

		when(authenticationService.authenticateEmployeeGUARDUser(USER_NAME, PASSWORD, VALID_EMAIL_HASH_STRING, true))
				.thenReturn("fake cookie content");

		actionContext.setActionInvocation(actionInvocation);
		when(actionInvocation.getInvocationContext()).thenReturn(actionContext);
		actionContext.setLocale(Locale.UK);
		when(request.getParameter("employee_guard_create_account.hashCode")).thenReturn(VALID_EMAIL_HASH_STRING);
	}

	private void verifyTestInsert(String result) throws FailedLoginException {
		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(EMPLOYEE_SUMMARY, accountAction.getUrl());

		verify(profileEntityService).save(any(Profile.class), any(EntityAuditInfo.class));
		verify(emailHashService).findByHash(VALID_EMAIL_HASH_STRING);
		verify(employeeEntityService).linkEmployeeToProfile(any(SoftDeletedEmployee.class), any(Profile.class));
		verify(emailHashService).expire(any(EmailHash.class));
		verify(authenticationService).authenticateEmployeeGUARDUser(USER_NAME, PASSWORD, VALID_EMAIL_HASH_STRING, true);
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
