package com.picsauditing.actions.users;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.util.SpringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

public class ChangePasswordTest extends PicsActionTest {

	private ChangePassword changePassword;

	@Mock private UserDAO userDAO1;
	@Mock private UserDAO userDAO2;
	@Mock private User user;
	@Mock private Account account;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		changePassword = new ChangePassword();
		super.setUp(changePassword);

		Whitebox.setInternalState(changePassword, "dao", userDAO1);
		Whitebox.setInternalState(changePassword, "userDAO", userDAO2);
		Whitebox.setInternalState(changePassword, "user", user);
		Whitebox.setInternalState(changePassword, "u", user);

		when(userDAO1.find(anyInt())).thenReturn(user);
		when(userDAO2.findName(anyString())).thenReturn(user);
		when(user.getId()).thenReturn(1);
		when(user.getAccount()).thenReturn(account);
		when(account.getPasswordSecurityLevel()).thenReturn(PasswordSecurityLevel.Maximum);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", (ApplicationContext) null);
	}

	@Test
	public void testExecute_PasswordSecurityLevelShouldBeSet() throws Exception {
		changePassword.execute();

		assertEquals(changePassword.getPasswordSecurityLevel(), changePassword.getUser().getAccount().getPasswordSecurityLevel());
	}
}
