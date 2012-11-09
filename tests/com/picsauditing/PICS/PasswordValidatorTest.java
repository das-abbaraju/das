package com.picsauditing.PICS;

import com.picsauditing.dao.PasswordDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.PasswordHistory;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasswordValidatorTest {

    private User user;
    private Vector<String> errorMessages;
	private PasswordValidator passwordValidator;
	private List<PasswordHistory> recentEntries;

	@Before
    public void setUp() throws Exception {
		setupTestUser(PasswordSecurityLevel.Normal);
		passwordValidator = new PasswordValidator();
		PasswordDAO passwordDAO = setupPasswordDao();
		passwordValidator.passwordDAO = passwordDAO;
		errorMessages = new Vector<String>(0);
    }

	@After
    public void tearDown() throws Exception {

    }

    @Test
    public void testValidatePassword_validNormalCases() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "joes1";

        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertTrue(errorMessages.size() == 0);
    }

    @Test
    public void testValidatePassword_validHighCases() throws Exception {
        setupTestUser(PasswordSecurityLevel.High);
        String newPassword = "joeSi1";

        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertTrue(errorMessages.size() == 0);
    }

    @Test
    public void testValidatePassword_validMaximumCases() throws Exception {

        setupTestUser(PasswordSecurityLevel.Maximum);
        String newPassword = "joeSix$1";

        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertTrue(errorMessages.size() == 0);
    }

    @Test
    public void testValidatePassword_cannotUseUsername() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "joeSixpack";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        // todo: Currently, PasswordValidator uses hard-coded error strings. Update this when we fix the error messages.
        assertTrue(errorMessages.contains("Please choose a password different from your username."));
    }

    @Test
    public void testValidatePassword_mustAlwaysContainDigitsAndLetters() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "joeSixpack";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains("Your password should contain digits and letters"));
    }

    @Test
    public void testValidatePassword_cannotReuseCurrentPassword() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "joeSixpack";
	    user.setPassword(encryptPassword(newPassword));
	    errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains("Please choose a different password than your current password."));
    }

    @Test
    public void testValidatePassword_basic_Normal() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "1234";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains("Please choose a password at least " + PasswordSecurityLevel.Normal.minLength + " characters in length."));
    }

    @Test
    public void testValidatePassword_basic_High() throws Exception {

        setupTestUser(PasswordSecurityLevel.High);
        String newPassword = "sixpa";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains("Please choose a password at least " + PasswordSecurityLevel.High.minLength + " characters in length."));
        assertTrue(errorMessages.contains("Please choose a password with at least one upper case and one lower case character."));
        assertFalse(errorMessages.contains("Please choose a password with at least one special character."));
    }

    @Test
    public void testValidatePassword_basic_Maximum() throws Exception {

        setupTestUser(PasswordSecurityLevel.Maximum);
        String newPassword = "sixpa";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains("Please choose a password at least " + PasswordSecurityLevel.Maximum.minLength + " characters in length."));
        assertTrue(errorMessages.contains("Please choose a password with at least one upper case and one lower case character."));
	    assertTrue(errorMessages.contains("Please choose a password with at least one special character."));
    }

    @Test
    public void testValidatePassword_passwordHistory_Normal() throws Exception {

	    setupTestUser(PasswordSecurityLevel.Normal);

	    String newPassword = "hello1";
	    errorMessages = passwordValidator.validatePassword(user, newPassword);
	    assertFalse(errorMessages.contains("You have used this password too recently. Please choose a different password."));
    }

    @Test
    public void testValidatePassword_passwordHistory_High() throws Exception {

	    setupTestUser(PasswordSecurityLevel.High);

	    String newPassword = "hello1";
	    errorMessages = passwordValidator.validatePassword(user, newPassword);
	    assertTrue(errorMessages.contains("You have used this password too recently. Please choose a different password."));

	    newPassword = "hello6";
	    errorMessages = passwordValidator.validatePassword(user, newPassword);
	    assertFalse(errorMessages.contains("You have used this password too recently. Please choose a different password."));
    }

    @Test
    public void testValidatePassword_passwordHistory_Maximum() throws Exception {

	    setupTestUser(PasswordSecurityLevel.Maximum);

	    String newPassword = "hello1";
	    errorMessages = passwordValidator.validatePassword(user, newPassword);
	    assertTrue(errorMessages.contains("You have used this password too recently. Please choose a different password."));

	    newPassword = "hello6";
	    errorMessages = passwordValidator.validatePassword(user, newPassword);
	    assertFalse(errorMessages.contains("You have used this password too recently. Please choose a different password."));
    }

    private void setupTestUser(PasswordSecurityLevel passwordSecurityLevel) {
        user = new User();
	    user.setId(5);
        user.setUsername("joeSixpack");
        Account account = new Account();
        account.setPasswordSecurityLevel(passwordSecurityLevel);
        user.setAccount(account);
    }

	private PasswordDAO setupPasswordDao() {
		PasswordDAO passwordDAO = mock(PasswordDAO.class);
		setupRecentEntries();
		when(passwordDAO.findRecentEntriesByCount(anyInt(), anyInt())).thenReturn(recentEntries);
		when(passwordDAO.findRecentEntriesByPreviousMonths(anyInt(), anyInt())).thenReturn(recentEntries);
		return passwordDAO;
	}

	private void setupRecentEntries() {
		recentEntries = new ArrayList<PasswordHistory>();
		recentEntries.add(new PasswordHistory(user, encryptPassword("hello1"), getDateXMonthsAgo(1)));
		recentEntries.add(new PasswordHistory(user, encryptPassword("hello2"), getDateXMonthsAgo(2)));
		recentEntries.add(new PasswordHistory(user, encryptPassword("hello3"), getDateXMonthsAgo(3)));
		recentEntries.add(new PasswordHistory(user, encryptPassword("hello4"), getDateXMonthsAgo(4)));
		recentEntries.add(new PasswordHistory(user, encryptPassword("hello5"), getDateXMonthsAgo(5)));
	}

	private String encryptPassword(String password) {
		return EncodedMessage.hash(password + user.getId());
	}

	private Date getDateXMonthsAgo(int monthsAgo) {
		return DateBean.addMonths(new Date(), monthsAgo);
	}

}
