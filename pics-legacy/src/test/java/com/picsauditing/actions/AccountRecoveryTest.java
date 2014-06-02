package com.picsauditing.actions;

import com.picsauditing.PicsActionTest;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.email.AccountRecoveryEmailService;
import com.picsauditing.util.URLUtils;
import com.picsauditing.validator.InputValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AccountRecoveryTest extends PicsActionTest {

	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";
	private static final String TESTING_EMAIL = "tester@picsauditing.com";
	private static final String RECOVER_USERNAME_URL = "/AccountRecovery!recoverUsername";
	private static final String LOGIN_URL = "Login";

	private AccountRecovery accountRecovery;

	@Mock
	private AccountRecoveryEmailService emails;
	@Mock
	private URLUtils urlUtils;
	@Mock
	private User user;
	@Mock
	private AppUser appUser;
	@Mock
	private UserDAO userDAO;
	@Mock
	private AppUserService appUserService;
	@Mock
	private ProfileEntityService profileService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		accountRecovery = new AccountRecovery();
		Whitebox.setInternalState(accountRecovery, "emails", emails);
		Whitebox.setInternalState(accountRecovery, "inputValidator", new InputValidator());
		Whitebox.setInternalState(accountRecovery, "urlUtils", urlUtils);
		Whitebox.setInternalState(accountRecovery, "userDAO", userDAO);
		Whitebox.setInternalState(accountRecovery, "appUserService", appUserService);
		Whitebox.setInternalState(accountRecovery, "profileService", profileService);

		super.setUp(accountRecovery);
	}

	@Test
	public void testFindName_EmailIsNullOrEmpty() throws Exception {
		accountRecovery.setEmail(null);
		testFindName_WithErrors();
	}

	@Test
	public void testFindName_EmailIsEmpty() throws Exception {
		accountRecovery.setEmail("");
		testFindName_WithErrors();
	}

	@Test
	public void testFindName_EmailIsInvalid() throws Exception {
		accountRecovery.setEmail("Test TESTING_EMAIL");
		testFindName_WithErrors();
	}

	private void testFindName_WithErrors() throws Exception {

		final String result = accountRecovery.findName();

		assertEquals(USERNAME, result);
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verifyZeroInteractions(emails);
		verify(userDAO, never()).findByEmail(anyString());

	}


	@Test
	public void testFindName_EmailMatchesNoUsers() throws Exception {
		when(urlUtils.getActionUrl("AccountRecovery", "recoverUsername")).thenReturn(RECOVER_USERNAME_URL);
		when(userDAO.findByEmail(anyString())).thenReturn(Collections.<User>emptyList());

		accountRecovery.setEmail(TESTING_EMAIL);

		final String result = accountRecovery.findName();

		assertEquals(PicsActionSupport.REDIRECT, result);
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());
		assertEquals(RECOVER_USERNAME_URL, accountRecovery.getUrl());

		verifyZeroInteractions(emails);
		verify(userDAO).findByEmail(anyString());
	}

	@Test
	public void testFindName_EmailMatchesAtLeastOneUser() throws Exception {

		final List<User> matchingUsers = new ArrayList<>();
		matchingUsers.add(user);

		when(urlUtils.getActionUrl("AccountRecovery", "recoverUsername")).thenReturn(RECOVER_USERNAME_URL);
		when(urlUtils.getActionUrl(LOGIN_URL)).thenReturn(LOGIN_URL);
		when(user.getEmail()).thenReturn(TESTING_EMAIL);
		when(userDAO.findByEmail(anyString())).thenReturn(matchingUsers);

		accountRecovery.setEmail(TESTING_EMAIL);

		final String result = accountRecovery.findName();

		assertEquals(PicsActionSupport.REDIRECT, result);
		assertFalse(accountRecovery.hasActionErrors());
		assertTrue(accountRecovery.hasActionMessages());
		assertEquals(LOGIN_URL, accountRecovery.getUrl());

		verify(emails).sendUsernameRecoveryEmail(anyListOf(User.class));
		verify(userDAO).findByEmail(anyString());
	}

	@Test
	public void testFindName_EmailMatchesAtLeastOneUserButThrowsExceptionSending() throws Exception {

		List<User> matchingUsers = new ArrayList<>();
		matchingUsers.add(user);

		doThrow(new IOException()).when(emails).sendUsernameRecoveryEmail(anyListOf(User.class));
		when(urlUtils.getActionUrl("AccountRecovery", "recoverUsername")).thenReturn(RECOVER_USERNAME_URL);
		when(user.getEmail()).thenReturn(TESTING_EMAIL);
		when(userDAO.findByEmail(anyString())).thenReturn(matchingUsers);

		accountRecovery.setEmail(TESTING_EMAIL);

		final String result = accountRecovery.findName();

		assertEquals(USERNAME, result);
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verify(emails).sendUsernameRecoveryEmail(anyListOf(User.class));
		verify(userDAO).findByEmail(anyString());
	}

	@Test
	public void testResetPassword_NullDeletedUserName() throws Exception {
		accountRecovery.setUsername(null);
		testResetPassword_expectErrors();
	}

	@Test
	public void testResetPassword_EmptyUsername() throws Exception {
		accountRecovery.setUsername("");
		testResetPassword_expectErrors();
	}

	@Test
	public void testResetPassword_DeletedUsername() throws Exception {
		accountRecovery.setUsername("DELETE-test");
		testResetPassword_expectErrors();
	}

	private void testResetPassword_expectErrors() {
		final String result = accountRecovery.resetPassword();

		assertEquals(PASSWORD, result);
		assertTrue(accountRecovery.hasActionErrors());
	}

	@Test
	public void testResetPassword_UsernameReturnsNullAppUser() throws Exception {
		accountRecovery.setUsername("test");

		when(appUserService.findByUsername(anyString())).thenReturn(null);

		final String result = accountRecovery.resetPassword();

		assertEquals(PASSWORD, result);
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());
	}

	@Test
	public void testResetPassword_UserIsInactive() throws Exception {
		accountRecovery.setUsername("test");

		when(user.isActiveB()).thenReturn(false);
		when(userDAO.findName(anyString())).thenReturn(user);

		final String result = accountRecovery.resetPassword();

		assertEquals(PASSWORD, result);
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verify(user, never()).setResetHash(anyString());
		verify(userDAO, never()).save(user);
	}

	@Test
	public void testResetPassword_UserIsActive() throws Exception {
		accountRecovery.setUsername("test");

		when(user.isActiveB()).thenReturn(true);
		when(userDAO.findName(anyString())).thenReturn(user);
		when(appUserService.findByUsername(anyString())).thenReturn(appUser);

		final String result = accountRecovery.resetPassword();

		assertEquals(PicsActionSupport.REDIRECT, result);
		assertFalse(accountRecovery.hasActionErrors());
		assertTrue(accountRecovery.hasActionMessages());
		assertEquals("Login.action", accountRecovery.getUrl());

		verify(appUser).setResetHash(anyString());
		verify(appUserService).save(appUser);
	}
}
