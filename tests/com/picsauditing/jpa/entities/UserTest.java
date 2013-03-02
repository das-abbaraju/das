package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.validator.InputValidator;

public class UserTest {

	private User user;

	@Before
	public void setUp() {
		user = new User();
	}

	@Test
	public void testIsLocked_DefaultIsFalse() throws Exception {
		assertFalse(user.isLocked());
	}

	@Test
	public void testIsLockedNow() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		user.setLockUntil(calendar.getTime());

		assertTrue(user.isLocked());
	}

	@Test
	public void testIsLockedBefore() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		user.setLockUntil(calendar.getTime());

		assertFalse(user.isLocked());
	}

	@Ignore("Need to be able to mock SpringUtils")
	@Test
	public void testGetInputValidator_DoesntReturnNull() {
//		PowerMockito.mockStatic(SpringUtils.class);
//		when(SpringUtils.getBean("InputValidator")).thenReturn(new InputValidator());
		InputValidator inputValidator = user.getInputValidator();

		assertNotNull(inputValidator);
	}

	@Ignore("Need to be able to mock SpringUtils")
	@Test
	public void testIsUserNameValid_WhenInputValidatorInNull_ThenItsInjectedAndNoExceptionIsThrown() {
		Whitebox.setInternalState(user, "inputValidator", (InputValidator) null);

		user.isUsernameValid("dont care");
	}

	@Ignore("Need to be able to mock SpringUtils")
	@Test
	public void testContainsOnlySafeCharacters_WhenInputValidatorInNull_ThenItsInjectedAndNoExceptionIsThrown() {
		Whitebox.setInternalState(user, "inputValidator", (InputValidator) null);

		user.containsOnlySafeCharacters("dont care");
	}

	@Ignore("Need to be able to mock SpringUtils")
	@Test
	public void testIsUsernameNotTaken_ThenItsInjectedAndNoExceptionIsThrown() {
		Whitebox.setInternalState(user, "inputValidator", (InputValidator) null);

		user.isUsernameNotTaken("dont care");
	}
}
