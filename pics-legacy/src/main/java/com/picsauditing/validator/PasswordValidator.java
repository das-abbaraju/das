package com.picsauditing.validator;

import com.picsauditing.dao.PasswordDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.PasswordHistory;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class PasswordValidator {

	public static final String REGEX_ALL_LETTERS = ".*[a-zA-Z].*";
	public static final String REGEX_ALL_NUMBERS = ".*[0-9].*";
	public static final String REGEX_NON_WORD_CHARACTER = "[^\\w\\*]";

	@Autowired
    private PasswordDAO passwordDAO;

    public Vector<String> validatePassword(User user, String newPassword) {
        Vector<String> errorMessages = new Vector<String>();
        if (newPassword == null) {
        	return errorMessages;
        }

        if (newPassword.equalsIgnoreCase(user.getUsername())) {
            errorMessages.addElement("PasswordValidator.error.PasswordCannotBeUserName");
        }

        if (!newPassword.matches(REGEX_ALL_LETTERS) || !newPassword.matches(REGEX_ALL_NUMBERS)) {
            errorMessages.addElement("PasswordValidator.error.PasswordMustContainNumbersAndCharacters");
        }

        String encryptedNewPassword = EncodedMessage.hash(newPassword + user.getId());
        if (encryptedNewPassword.equals(user.getPassword())) {
            errorMessages.addElement("PasswordValidator.error.PasswordCannotBeCurrentPassword");
        }

        enforceAccountPasswordPreferences(user, newPassword, errorMessages);

        return errorMessages;
    }

    private void enforceAccountPasswordPreferences(User user, String newPassword, Vector<String> errorMessages) {
    	PasswordSecurityLevel passwordSecurityLevel = PasswordSecurityLevel.Normal;

        Account account = user.getAccount();
        if (account != null) {
        	passwordSecurityLevel = account.getPasswordSecurityLevel();
        }

        if (newPassword.length() < passwordSecurityLevel.minLength) {
            errorMessages.addElement("PasswordValidator.error.PasswordMustMeetMinimumLength::" + passwordSecurityLevel.minLength);
        }

        if (passwordSecurityLevel.enforceMixedCase) {
            if (!Strings.isMixedCase(newPassword)) {
                errorMessages.addElement("PasswordValidator.error.PasswordMustBeMixedCase");
            }
        }

        if (passwordSecurityLevel.enforceSpecialCharacter) {
            boolean found = Pattern.compile(REGEX_NON_WORD_CHARACTER).matcher(newPassword).find();

            if (!found) {
                errorMessages.addElement("PasswordValidator.error.PasswordMustContainSpecialCharacter");
            }
        }

        if (passwordSecurityLevel.enforceHistory()) {
            if (passwordHistoryListContainsPassword(newPassword, user, passwordSecurityLevel)) {
                errorMessages.add("PasswordValidator.error.PasswordUsedTooRecently");
            }
        }
    }

    private boolean passwordHistoryListContainsPassword(String newPassword, User user, PasswordSecurityLevel passwordSecurityLevel) {
        String encryptedNewPassword = EncodedMessage.hash(newPassword + user.getId());

        if (passwordSecurityLevel.enforceEntriesOfHistory()) {
	        if (findRecentPasswordsByCount(user, passwordSecurityLevel.entriesOfHistoryToDisallow).contains(encryptedNewPassword)) {
                return true;
            }
        }

	    if (passwordSecurityLevel.enforceMonthsOfHistory()) {
            if (findRecentPasswordsByPreviousMonths(user, passwordSecurityLevel.monthsOfHistoryToDisallow).contains(encryptedNewPassword)) {
                return true;
            }
        }

        return false;
    }

    private List<String> findRecentPasswordsByCount(User user, int numberOfEntries) {
        List<String> recentPasswords = new ArrayList<String>();
        List<PasswordHistory> recentEntriesByCount = passwordDAO.findRecentEntriesByCount(user.getId(), numberOfEntries);

        for (PasswordHistory passwordHistory : recentEntriesByCount) {
            recentPasswords.add(passwordHistory.getPasswordHash());
        }

        return recentPasswords;
    }

    private List<String> findRecentPasswordsByPreviousMonths(User user, int recentMonths) {
        List<String> recentPasswords = new ArrayList<String>();
        List<PasswordHistory> recentEntriesByPreviousMonths = passwordDAO.findRecentEntriesByPreviousMonths(user.getId(), recentMonths);

        for (PasswordHistory passwordHistory : recentEntriesByPreviousMonths) {
            recentPasswords.add(passwordHistory.getPasswordHash());
        }

        return recentPasswords;
    }

}

