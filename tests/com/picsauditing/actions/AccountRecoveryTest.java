package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.URLUtils;
import com.picsauditing.validator.InputValidator;

public class AccountRecoveryTest extends PicsTranslationTest {

	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";

	private AccountRecovery accountRecovery;

	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private EmailQueue emailQueue;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailTemplate emailTemplate;
	@Mock
	private EmailTemplateDAO emailTemplateDAO;
	@Mock
	private URLUtils urlUtils;
	@Mock
	private User user;
	@Mock
	private UserDAO userDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		accountRecovery = new AccountRecovery();

		Whitebox.setInternalState(accountRecovery, "emailBuilder", emailBuilder);
		Whitebox.setInternalState(accountRecovery, "emailSender", emailSender);
		Whitebox.setInternalState(accountRecovery, "emailTemplateDAO", emailTemplateDAO);
		Whitebox.setInternalState(accountRecovery, "inputValidator", new InputValidator());
		Whitebox.setInternalState(accountRecovery, "urlUtils", urlUtils);
		Whitebox.setInternalState(accountRecovery, "userDAO", userDAO);
	}

	@Test
	public void testFindName_EmailIsNullOrEmpty() throws Exception {
		assertEquals(USERNAME, accountRecovery.findName());
		assertTrue(accountRecovery.hasActionErrors());

		verify(emailSender, never()).send(any(EmailQueue.class));

		accountRecovery.setEmail("");

		assertEquals(USERNAME, accountRecovery.findName());
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verify(emailBuilder, never()).build();
		verify(emailTemplateDAO, never()).find(anyInt());
		verify(emailSender, never()).send(any(EmailQueue.class));
		verify(userDAO, never()).findByEmail(anyString());
	}

	@Test
	public void testFindName_EmailIsInvalid() throws Exception {
		accountRecovery.setEmail("Test email");

		assertEquals(USERNAME, accountRecovery.findName());
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verify(emailBuilder, never()).build();
		verify(emailTemplateDAO, never()).find(anyInt());
		verify(emailSender, never()).send(any(EmailQueue.class));
		verify(userDAO, never()).findByEmail(anyString());
	}

	@Test
	public void testFindName_EmailMatchesNoUsers() throws Exception {
		String url = "/AccountRecovery!recoverUsername";
		when(urlUtils.getActionUrl("AccountRecovery", "recoverUsername")).thenReturn(url);
		when(userDAO.findByEmail(anyString())).thenReturn(Collections.<User> emptyList());

		accountRecovery.setEmail("tester@picsauditing.com");

		assertEquals(PicsActionSupport.REDIRECT, accountRecovery.findName());
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());
		assertEquals(url, accountRecovery.getUrl());

		verify(emailBuilder, never()).build();
		verify(emailTemplateDAO, never()).find(anyInt());
		verify(emailSender, never()).send(any(EmailQueue.class));
		verify(userDAO).findByEmail(anyString());
	}

	@Test
	public void testFindName_EmailMatchesAtLeastOneUser() throws Exception {
		String email = "tester@picsauditing.com";
		String url = "/AccountRecovery!recoverUsername";
		String loginUrl = "Login";

		List<User> matchingUsers = new ArrayList<>();
		matchingUsers.add(user);

		when(emailBuilder.build()).thenReturn(emailQueue);
		when(emailTemplateDAO.find(anyInt())).thenReturn(emailTemplate);
		when(urlUtils.getActionUrl("AccountRecovery", "recoverUsername")).thenReturn(url);
		when(urlUtils.getActionUrl(loginUrl)).thenReturn(loginUrl);
		when(user.getEmail()).thenReturn(email);
		when(userDAO.findByEmail(anyString())).thenReturn(matchingUsers);

		accountRecovery.setEmail(email);

		assertEquals(PicsActionSupport.REDIRECT, accountRecovery.findName());
		assertFalse(accountRecovery.hasActionErrors());
		assertTrue(accountRecovery.hasActionMessages());
		assertEquals(loginUrl, accountRecovery.getUrl());

		verify(emailBuilder).build();
		verify(emailSender).send(any(EmailQueue.class));
		verify(emailTemplateDAO).find(anyInt());
		verify(user).getEmail();
		verify(userDAO).findByEmail(anyString());
	}

	@Test
	public void testFindName_EmailMatchesAtLeastOneUserButThrowsExceptionSending() throws Exception {
		String email = "tester@picsauditing.com";
		String url = "/AccountRecovery!recoverUsername";

		List<User> matchingUsers = new ArrayList<>();
		matchingUsers.add(user);

		when(emailBuilder.build()).thenThrow(new IOException());
		when(emailTemplateDAO.find(anyInt())).thenReturn(emailTemplate);
		when(urlUtils.getActionUrl("AccountRecovery", "recoverUsername")).thenReturn(url);
		when(user.getEmail()).thenReturn(email);
		when(userDAO.findByEmail(anyString())).thenReturn(matchingUsers);

		accountRecovery.setEmail(email);

		assertEquals(USERNAME, accountRecovery.findName());
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verify(emailBuilder).build();
		verify(emailSender, never()).send(any(EmailQueue.class));
		verify(emailTemplateDAO).find(anyInt());
		verify(user).getEmail();
		verify(userDAO).findByEmail(anyString());
	}

	@Test
	public void testResetPassword_EmptyOrNullOrDeletedUserName() throws Exception {
		assertEquals(PASSWORD, accountRecovery.resetPassword());
		assertTrue(accountRecovery.hasActionErrors());

		accountRecovery.setUsername("");
		assertEquals(PASSWORD, accountRecovery.resetPassword());
		assertTrue(accountRecovery.hasActionErrors());

		accountRecovery.setUsername("DELETE-test");
		assertEquals(PASSWORD, accountRecovery.resetPassword());
		assertTrue(accountRecovery.hasActionErrors());
	}

	@Test
	public void testResetPassword_UsernameReturnsNullUser() throws Exception {
		accountRecovery.setUsername("test");

		when(userDAO.findName(anyString())).thenReturn(null);

		accountRecovery.resetPassword();

		assertEquals(PASSWORD, accountRecovery.resetPassword());
		assertTrue(accountRecovery.hasActionErrors());
		assertFalse(accountRecovery.hasActionMessages());

		verify(user, never()).setResetHash(anyString());
		verify(userDAO, never()).save(user);
	}

	@Test
	public void testResetPassword_UserIsInactive() throws Exception {
		accountRecovery.setUsername("test");

		when(user.isActiveB()).thenReturn(false);
		when(userDAO.findName(anyString())).thenReturn(user);

		accountRecovery.resetPassword();

		assertEquals(PASSWORD, accountRecovery.resetPassword());
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

		assertEquals(PicsActionSupport.REDIRECT, accountRecovery.resetPassword());
		assertFalse(accountRecovery.hasActionErrors());
		assertTrue(accountRecovery.hasActionMessages());
		assertEquals("Login.action", accountRecovery.getUrl());

		verify(user).setResetHash(anyString());
		verify(userDAO).save(user);
	}
}
