package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import com.picsauditing.dao.PasswordDAO;
import com.picsauditing.jpa.entities.PasswordHistory;
import com.picsauditing.security.EncodedMessage;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class PasswordValidator {

	@Autowired
	protected PasswordDAO passwordDAO;

	public static int MINIMUM_LENGTH = 5;
    public static final String REGEX_NON_WORD_CHARACTER = "[^\\w\\*]";

	public Vector<String> validatePassword(User user, String newPassword) {
		Vector<String> errorMessages = new Vector<String>();

        if (newPassword.equalsIgnoreCase(user.getUsername())) {
            errorMessages.addElement("Please choose a password different from your username.");
        }

        if (!newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*[0-9].*")) {
            errorMessages.addElement("Your password should contain digits and letters");
        }

		String encryptedNewPassword = EncodedMessage.hash(newPassword + user.getId());
		if (encryptedNewPassword.equals(user.getPassword())) {
			errorMessages.addElement("Please choose a different password than your current password.");
		}

		enforceAccountPasswordPreferences(user, newPassword, errorMessages);

		return errorMessages;
	}

    private void enforceAccountPasswordPreferences(User user, String newPassword, Vector<String> errorMessages) {
        Account account = user.getAccount();
        PasswordSecurityLevel passwordSecurityLevel = account.getPasswordSecurityLevel();

        if (newPassword.length() < passwordSecurityLevel.minLength) {
            errorMessages.addElement("Please choose a password at least " + passwordSecurityLevel.minLength + " characters in length.");
        }

        if (passwordSecurityLevel.enforceMixedCase) {
            if (!Strings.isMixedCase(newPassword)) {
                errorMessages.addElement("Please choose a password with at least one upper case and one lower case character.");
            }
        }

        if (passwordSecurityLevel.enforceSpecialCharacter) {
            boolean found = Pattern.compile(REGEX_NON_WORD_CHARACTER).matcher(newPassword).find();

            if (!found) {
                errorMessages.addElement("Please choose a password with at least one special character.");
            }
        }

        if (passwordSecurityLevel.enforceHistory()) {
            if (passwordHistoryListContainsPassword(newPassword, user, passwordSecurityLevel)) {
        		errorMessages.add("You have used this password too recently. Please choose a different password.");
            }
        }
    }

    private boolean passwordHistoryListContainsPassword(String newPassword, User user, PasswordSecurityLevel passwordSecurityLevel) {
        String encryptedNewPassword = EncodedMessage.hash(newPassword + user.getId());

        int entriesToDisallow = passwordSecurityLevel.entriesOfHistoryToDisallow;
        if (entriesToDisallow > 0) {
        	if (findRecentPasswordsByCount(user, entriesToDisallow).contains(encryptedNewPassword)) {
        		return true;
        	}
        }

        int monthsToDisallow = passwordSecurityLevel.monthsOfHistoryToDisallow;
        if (monthsToDisallow > 0) {
        	if (findRecentPasswordsByPreviousMonths(user, monthsToDisallow).contains(encryptedNewPassword)) {
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

