package com.picsauditing.access;

import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginServiceTest {

	LoginService loginService = new LoginService();

	@Mock private UserService userService;
	@Mock private User user;
	private String username = "joesixpack";
	private String password = "8675309";
	private String key = "abc123";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		loginService.userService = userService;
		setupNormalUser();
	}

	@Test
	public void testLoginNormally_HappyPathShouldNotThrowErrors() throws Exception {

		loginService.loginNormally(username, password);
	}

	@Test(expected = AccountNotFoundException.class)
	public void testLoginNormally_NonExistentUserShouldThrowAccountNotFoundException() throws Exception {
		when(userService.loadUserByUsername(username)).thenReturn(null);

		loginService.loginNormally(username, password);
	}

	@Test(expected = AccountLockedException.class)
	public void testLoginNormally_LockedUserShouldThrowAccountLockedException() throws Exception {
		when(user.isLocked()).thenReturn(true);

		loginService.loginNormally(username, password);
	}

	@Test(expected = AccountInactiveException.class)
	public void testLoginNormally_InactiveUserShouldThrowAccountInactiveException() throws Exception {
		when(userService.isUserActive(user)).thenReturn(false);

		loginService.loginNormally(username, password);
	}

	@Test(expected = FailedLoginException.class)
	public void testLoginNormally_FailedLogin() throws Exception {
		when(user.isEncryptedPasswordEqual(password)).thenReturn(false);
		when(user.getFailedAttempts()).thenReturn(1);

		loginService.loginNormally(username, password);

		verify(user).setFailedAttempts(2);
		verify(user, never()).setLockUntil(any(Date.class));
	}

	@Test(expected = FailedLoginAndLockedException.class)
	public void testLoginNormally_FailedLoginAndLocked() throws Exception {
		when(user.isEncryptedPasswordEqual(password)).thenReturn(false);
		when(user.getFailedAttempts()).thenReturn(LoginService.MAX_FAILED_ATTEMPTS + 1);

		loginService.loginNormally(username, password);

		verify(user).setFailedAttempts(0);
		verify(user).setLockUntil(any(Date.class));
	}

	@Test(expected = PasswordExpiredException.class)
	public void testLoginNormally_ExpiredPasswordShouldThrowPasswordExpiredException() throws Exception {
		when(userService.isPasswordExpired(user)).thenReturn(true);

		loginService.loginNormally(username, password);
	}

	@Test
	public void testLoginForResetPassword_HappyPathShouldNotThrowErrors() throws Exception {
		when(user.getResetHash()).thenReturn(key);

		loginService.loginForResetPassword(username, key);
	}


	@Test(expected = InvalidResetKeyException.class)
	public void testLoginForResetPassword_InvalidResetKeyShouldThrowInvalidResetKeyException() throws Exception {
		when(user.getResetHash()).thenReturn("notAMatch");

		loginService.loginForResetPassword(username, key);
	}

	@Test
	public void testLoginForResetPassword_IfKeyMatchesPrepareUserForLoginAfterReset() throws Exception {
		when(user.getResetHash()).thenReturn(key);

		loginService.loginForResetPassword(username, key);

		verify(user).setForcePasswordReset(true);
		verify(user).setResetHash("");
		verify(user).unlockLogin();
	}

	@Test(expected = AccountNotFoundException.class)
	public void testLoginForResetPassword_NonExistentUserShouldThrowAccountNotFoundException() throws Exception {
		when(userService.loadUserByUsername(username)).thenReturn(null);

		loginService.loginForResetPassword(username, key);
	}

	@Test(expected = AccountLockedException.class)
	public void testLoginForResetPassword_LockedUserShouldThrowAccountLockedException() throws Exception {
		when(user.isLocked()).thenReturn(true);

		loginService.loginForResetPassword(username, key);
	}

	@Test(expected = AccountInactiveException.class)
	public void testLoginForResetPassword_InactiveUserShouldThrowAccountInactiveException() throws Exception {
		when(userService.isUserActive(user)).thenReturn(false);

		loginService.loginForResetPassword(username, key);
	}

	@Test(expected = PasswordExpiredException.class)
	public void testLoginForResetPassword_ExpiredPasswordShouldThrowPasswordExpiredException() throws Exception {
		when(userService.isPasswordExpired(user)).thenReturn(true);

		loginService.loginForResetPassword(username, key);
	}

	private void setupNormalUser() {
		when(user.isLocked()).thenReturn(false);
		when(user.isEncryptedPasswordEqual(password)).thenReturn(true);
		when(user.getResetHash()).thenReturn(key);
		when(userService.loadUserByUsername(anyString())).thenReturn(user);
		when(userService.isUserActive(user)).thenReturn(true);
		when(userService.isPasswordExpired(user)).thenReturn(false);
	}
}
