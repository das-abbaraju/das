package com.picsauditing.PICS;

import com.picsauditing.dao.PasswordDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.PasswordHistory;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedMessage;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class PasswordValidatorTest {

    private User user;
    private Vector<String> errorMessages;
    private PasswordValidator passwordValidator;
    private List<PasswordHistory> recentEntries;
	@Mock private I18nCache i18nCache;
	@Mock PasswordDAO passwordDAO;


	@Before
    public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(I18nCache.class, "INSTANCE", i18nCache);
		when(i18nCache.getText(anyString(), any(Locale.class), anyList())).then(returnArgumentsToString());
		when(i18nCache.getText(anyString(), any(Locale.class))).then(returnArgumentsToString());
		passwordValidator = new PasswordValidator();
		Whitebox.setInternalState(passwordValidator, "passwordDAO", passwordDAO);
		setupTestUser(PasswordSecurityLevel.Normal);
		setupPasswordDao();
		errorMessages = new Vector<String>(0);
    }

	@After
    public void tearDown() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);
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
        String newPassword = "joeSi?1";

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
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordCannotBeUserName", user)));
    }

    @Test
    public void testValidatePassword_mustAlwaysContainDigitsAndLetters() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "joeSixpack";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustContainNumbersAndCharacters", user)));

        newPassword = "1234567890";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustContainNumbersAndCharacters", user)));

        newPassword = "1234567abc";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertFalse(errorMessages.contains(getText("PasswordValidator.error.PasswordMustContainNumbersAndCharacters", user)));
    }

    @Test
    public void testValidatePassword_cannotReuseCurrentPassword() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "joeSixpack";
        user.setPassword(encryptPassword(newPassword));
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordCannotBeCurrentPassword", user)));
    }

    @Test
    public void testValidatePassword_basic_Normal() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);
        String newPassword = "1234";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustMeetMinimumLength", user, PasswordSecurityLevel.Normal.minLength)));
    }

    @Test
    public void testValidatePassword_basic_High() throws Exception {

        setupTestUser(PasswordSecurityLevel.High);
        String newPassword = "sixpa";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
	    assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustMeetMinimumLength", user, PasswordSecurityLevel.High.minLength)));
	    assertFalse(errorMessages.contains(getText("PasswordValidator.error.PasswordMustBeMixedCase", user)));
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustContainSpecialCharacter", user)));
    }

    @Test
    public void testValidatePassword_basic_Maximum() throws Exception {

        setupTestUser(PasswordSecurityLevel.Maximum);
        String newPassword = "sixpa";
        errorMessages = passwordValidator.validatePassword(user, newPassword);

        assertTrue(errorMessages.size() > 0);
	    assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustMeetMinimumLength", user, PasswordSecurityLevel.Maximum.minLength)));
	    assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustBeMixedCase", user)));
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordMustContainSpecialCharacter", user)));
    }

    @Test
    public void testValidatePassword_passwordHistory_Normal() throws Exception {

        setupTestUser(PasswordSecurityLevel.Normal);

        String newPassword = "hello1";
        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertFalse(errorMessages.contains(getText("PasswordValidator.error.PasswordUsedTooRecently", user)));
    }

    @Test
    public void testValidatePassword_passwordHistory_High() throws Exception {

        setupTestUser(PasswordSecurityLevel.High);

        String newPassword = "hello1";
        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordUsedTooRecently", user)));

        newPassword = "hello6";
        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertFalse(errorMessages.contains(getText("PasswordValidator.error.PasswordUsedTooRecently", user)));
    }

    @Test
    public void testValidatePassword_passwordHistory_Maximum() throws Exception {

        setupTestUser(PasswordSecurityLevel.Maximum);

        String newPassword = "hello1";
        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertTrue(errorMessages.contains(getText("PasswordValidator.error.PasswordUsedTooRecently", user)));

        newPassword = "hello6";
        errorMessages = passwordValidator.validatePassword(user, newPassword);
        assertFalse(errorMessages.contains(getText("PasswordValidator.error.PasswordUsedTooRecently", user)));
    }

    private void setupTestUser(PasswordSecurityLevel passwordSecurityLevel) {
        user = new User();
        user.setId(5);
        user.setUsername("joeSixpack");
	    user.setLocale(Locale.ENGLISH);
        Account account = new Account();
        account.setPasswordSecurityLevel(passwordSecurityLevel);
        user.setAccount(account);
    }

    private void setupPasswordDao() {
        setupRecentEntries();
        when(passwordDAO.findRecentEntriesByCount(anyInt(), anyInt())).thenReturn(recentEntries);
        when(passwordDAO.findRecentEntriesByPreviousMonths(anyInt(), anyInt())).thenReturn(recentEntries);
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

	private String getText(String key, User user) {
		return passwordValidator.getText(key, user);
	}

	private String getText(String key, User user, Object... args) {
		return passwordValidator.getText(key, user, args);
	}

	private Answer<String> returnArgumentsToString() {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return Arrays.toString(args);
			}
		};
	}

}
