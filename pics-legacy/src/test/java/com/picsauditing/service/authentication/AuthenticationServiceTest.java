package com.picsauditing.service.authentication;

import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.entities.builder.AppUserBuilder;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.builders.UserBuilder;
import com.picsauditing.security.SessionSecurity;
import com.picsauditing.service.user.UserService;
import com.picsauditing.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.FailedLoginException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

	public static final String USERNAME = "Username";
	public static final String PASSWORD = "Password";
	public static final String VALID_HASH = "valid hash";
	public static final int APPUSER_ID = 123;
	public static final int PROFILE_ID = 451;
	public static final int USER_ID = 810;
	public static final String INVALID_HASH = "invalid hash";

	// Class under test
	private AuthenticationService authenticationService;

	@Mock
	private AppUserService appUserService;
	@Mock
	private EmailHashService emailHashService;
	@Mock
	private ProfileEntityService profileEntityService;
	@Mock
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		authenticationService = new AuthenticationService();

		Whitebox.setInternalState(authenticationService, "appUserService", appUserService);
		Whitebox.setInternalState(authenticationService, "profileEntityService", profileEntityService);
		Whitebox.setInternalState(authenticationService, "userService", userService);
		Whitebox.setInternalState(authenticationService, "emailHashService", emailHashService);

		byte[] keyspec = Base64.decode("0GX83VHz1nFalBO71o059rnWbX22ayX31hgNbJoMmiYUpZZqSuIZyLQ0/Cr+nRdQCHPruikNPXV6" +
				"IWtkYJeV7g==");
		org.powermock.reflect.Whitebox.setInternalState(SessionSecurity.class, "serverSecretKey",
				new SecretKeySpec(keyspec, 0, keyspec.length, "HmacSHA1"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNewAppUser_Null_Username_And_Password() {
		authenticationService.createNewAppUser(null, null);
	}

	@Test(expected = UsernameNotAvailableException.class)
	public void testCreateNewAppUser_Username_Unavailable() {
		when(appUserService.isUserNameAvailable("Tester")).thenReturn(false);

		authenticationService.createNewAppUser(USERNAME, PASSWORD);
	}

	@Test
	public void testCreateNewAppUser() {
		setUpTestCreateNewAppUser();

		AppUser result = authenticationService.createNewAppUser(USERNAME, PASSWORD);

		verifyTestCreateNewAppUser(result);
	}

	private void setUpTestCreateNewAppUser() {
		AppUser fakeAppUser = new AppUserBuilder().id(APPUSER_ID).username(USERNAME).password(PASSWORD).build();

		when(appUserService.isUserNameAvailable(USERNAME)).thenReturn(true);
		when(appUserService.generateNewAppUser(USERNAME, PASSWORD)).thenReturn(fakeAppUser);
	}

	private void verifyTestCreateNewAppUser(AppUser result) {
		assertEquals(APPUSER_ID, result.getId());
		assertEquals(USERNAME, result.getUsername());
	}

	@Test(expected = FailedLoginException.class)
	public void testAuthenticateEmployeeGUARDUser_No_AppUser_Found() throws FailedLoginException {
		when(appUserService.findByUsernameAndUnencodedPassword(USERNAME, PASSWORD)).thenReturn(null);

		authenticationService.authenticateEmployeeGUARDUser(USERNAME, PASSWORD, VALID_HASH, true);
	}

	@Test(expected = FailedLoginException.class)
	public void testAuthenticateEmployeeGUARDUser_NoProfileFound_InvalidHash() throws FailedLoginException {
		verifyTestAuthenticateEmployeeGUARDUser_NoProfileFound_InvalidHash();

		authenticationService.authenticateEmployeeGUARDUser(USERNAME, PASSWORD, INVALID_HASH, true);
	}

	private void verifyTestAuthenticateEmployeeGUARDUser_NoProfileFound_InvalidHash() {
		setupAppUserService();
		when(profileEntityService.findByAppUserId(APPUSER_ID)).thenReturn(null);
		when(emailHashService.findByHash(INVALID_HASH)).thenReturn(null);
	}

	@Test
	public void testAuthenticateEmployeeGUARDUser() throws FailedLoginException {
		setupTestAuthenticateEmployeeGUARDUser();

		String result = authenticationService.authenticateEmployeeGUARDUser(USERNAME, PASSWORD, VALID_HASH, true);

		assertTrue(result.startsWith("810|"));
		assertTrue(result.contains("|{\"rememberMe\":true}|123|"));
	}

	private void setupTestAuthenticateEmployeeGUARDUser() {
		setupAppUserService();

		Profile fakeProfile = new ProfileBuilder().id(PROFILE_ID).build();
		when(profileEntityService.findByAppUserId(APPUSER_ID)).thenReturn(fakeProfile);

		User fakeUser = new UserBuilder().id(USER_ID).build();
		when(userService.findByAppUserId(APPUSER_ID)).thenReturn(fakeUser);
	}

	private void setupAppUserService() {
		AppUser fakeAppUser = new AppUserBuilder().id(APPUSER_ID).username(USERNAME).password(PASSWORD).build();
		when(appUserService.findByUsernameAndUnencodedPassword(USERNAME, PASSWORD)).thenReturn(fakeAppUser);
	}
}
