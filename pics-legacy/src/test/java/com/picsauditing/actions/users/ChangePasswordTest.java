package com.picsauditing.actions.users;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.validator.PasswordValidator;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Locale;
import java.util.Vector;

public class ChangePasswordTest extends PicsActionTest {

	private ChangePassword changePassword;

    private Vector<String> errors;

	@Mock
    private UserDAO userDAO1;
	@Mock
    private UserDAO userDAO2;
	@Mock
    private User user;
	@Mock
    private Account account;
    @Mock
    private PasswordValidator passwordValidator;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		changePassword = new ChangePassword();
		super.setUp(changePassword);

        errors = new Vector<>();

		Whitebox.setInternalState(changePassword, "dao", userDAO1);
		Whitebox.setInternalState(changePassword, "userDAO", userDAO2);
		Whitebox.setInternalState(changePassword, "user", user);
		Whitebox.setInternalState(changePassword, "u", user);
		Whitebox.setInternalState(changePassword, "passwordValidator", passwordValidator);

		when(userDAO1.find(anyInt())).thenReturn(user);
		when(userDAO2.findName(anyString())).thenReturn(user);
		when(user.getId()).thenReturn(1);
		when(user.getAccount()).thenReturn(account);
		when(account.getPasswordSecurityLevel()).thenReturn(PasswordSecurityLevel.Maximum);
        when(translationService.hasKey(anyString(),any(Locale.class))).thenReturn(true);
        when(passwordValidator.validatePassword(any(User.class), anyString())).thenReturn(errors);
	}

	@Test
	public void testExecute_PasswordSecurityLevelShouldBeSet() throws Exception {
		changePassword.execute();

		assertEquals(changePassword.getPasswordSecurityLevel(), changePassword.getUser().getAccount().getPasswordSecurityLevel());
	}

    @Test
    public void testAddActionErrorsForPasswordValidation_NoErrors_AddsNoErrors() throws Exception {
        Whitebox.invokeMethod(changePassword, "addActionErrorsForPasswordValidation");

        assertTrue(changePassword.getActionErrors().isEmpty());
    }

    @Test
    public void testAddActionErrorsForPasswordValidation_HasErrors_AddsErrors() throws Exception {
        errors.add("foo");

        Whitebox.invokeMethod(changePassword, "addActionErrorsForPasswordValidation");

        assertFalse(changePassword.getActionErrors().isEmpty());
    }

    @Test
    public void testAddActionErrorsForPasswordValidation_NoErrors_TranslatesNothing() throws Exception {
        Whitebox.invokeMethod(changePassword, "addActionErrorsForPasswordValidation");

        verify(translationService, never()).getText(anyString());
    }

    @Test
    public void testAddActionErrorsForPasswordValidation_HasErrors_TranslatesThem() throws Exception {
        errors.add("foo");

        Whitebox.invokeMethod(changePassword, "addActionErrorsForPasswordValidation");

        verify(translationService).getText("foo", Locale.ENGLISH, null);
    }

    @Test
    public void testAddActionErrorsForPasswordValidation_HasParameterizedErrors_TranslatesThem() throws Exception {
        errors.add("foo::5");

        Whitebox.invokeMethod(changePassword, "addActionErrorsForPasswordValidation");

        verify(translationService).getText("foo", Locale.ENGLISH, "5");
    }

}
