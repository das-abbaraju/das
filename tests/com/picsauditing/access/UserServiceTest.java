package com.picsauditing.access;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceTest {

	UserService userService = new UserService();

	@Mock private User user;
	@Mock private UserDAO userDAO;
	@Mock private Account account;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupNormalUser();
		userService.userDAO = userDAO;
	}

	@Test
	public void testLoadUserByUsername() throws Exception {

		String username = "joesixpack";
		User result = userService.loadUserByUsername(username);

		assertEquals(result, user);
	}

	@Test
	public void testLoadUserByUsername_NoResultShouldReturnNull() throws Exception {

		String username = "joesixpack";
		when(userDAO.findName(anyString())).thenReturn(null);

		User result = userService.loadUserByUsername(username);

		assertNull(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsUserActive_nullUserShouldThrowIllegalArgumentException() throws Exception {

		boolean result = userService.isUserActive(null);
	}

	@Test
	public void testIsUserActive_OperatorCorporateAccountNotActiveDemoShouldReturnFalse() throws Exception {
		when(account.isOperatorCorporate()).thenReturn(true);
		when(account.getStatus()).thenReturn(AccountStatus.Pending);

		boolean result = userService.isUserActive(user);

		assertFalse(result);
	}

	@Test
	public void testIsUserActive_DefaultCaseShouldReturnTrue() throws Exception {
		when(account.isOperatorCorporate()).thenReturn(false);
		when(account.isContractor()).thenReturn(true);

		boolean result = userService.isUserActive(user);

		assertTrue(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsPasswordExpired_nullUserShouldThrowIllegalArgumentException() {

		boolean result = userService.isPasswordExpired(null);
	}

	@Test
	public void testIsPasswordExpired_PasswordOlderThenExpirationMonthsShouldReturnTrue() throws ParseException {
		assertTrue(PasswordSecurityLevel.Maximum.expirationMonths == 3);
		when(account.getPasswordSecurityLevel()).thenReturn(PasswordSecurityLevel.Maximum);
		when(user.getPasswordChanged()).thenReturn(dateXMonthsAgo(4));

		boolean result = userService.isPasswordExpired(user);

		assertTrue(result);
	}

	@Test
	public void testIsPasswordExpired_PasswordNewerThenExpirationMonthsShouldReturnFalse() throws ParseException {
		assertTrue(PasswordSecurityLevel.Maximum.expirationMonths == 3);
		when(account.getPasswordSecurityLevel()).thenReturn(PasswordSecurityLevel.Maximum);
		when(user.getPasswordChanged()).thenReturn(dateXMonthsAgo(2));

		boolean result = userService.isPasswordExpired(user);

		assertFalse(result);
	}

	private void setupNormalUser() {
		when(userDAO.findName(anyString())).thenReturn(user);
		when(account.getStatus()).thenReturn(AccountStatus.Active);
		when(user.getAccount()).thenReturn(account);
		when(user.getIsActive()).thenReturn(YesNo.Yes);
	}

	private Date dateXMonthsAgo(int monthsAgo) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -monthsAgo);
		return cal.getTime();
	}

}
