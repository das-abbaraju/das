package com.picsauditing.PICS;

import java.util.Vector;

import com.picsauditing.jpa.entities.User;

public class PasswordValidator {
	public static int MINIMUM_LENGTH = 5;
	//private String requiredPattern = ".*[a-z][A-Z].*";

	/**
	 * 
	 * @param user
	 * @param newPassword cannot be null
	 * @return
	 */
	public static Vector<String> validateContractor(User user, String newPassword) {
		Vector<String> errorMessages = new Vector<String>();
		
		int minLength = MINIMUM_LENGTH;
		// TODO Temporary Fix for password authentication for BP Cherry
		// Point Refinery
		if (user.getAccount().getId() == 969) {
			minLength = 8;
		}
		// TODO
		//if (user.getAccount().getPasswordPreferences().getMinLength() > 0)
		//	minLength = user.getAccount().getPasswordPreferences().getMinLength();
		
		if (newPassword.length() < minLength)
			errorMessages.addElement("Please choose a password at least " + minLength
				+ " characters in length.");
		
		if (newPassword.equalsIgnoreCase(user.getUsername()))
			errorMessages.addElement("Please choose a password different from your username.");
		
		if (!newPassword.matches(".*[a-z][A-Z].*") || !newPassword.matches(".*[0-9].*"))
			errorMessages.addElement("Your password should contain digits and letters");
		
		if (user.getPasswordHistoryList().contains(newPassword))
			errorMessages.addElement("You can't reuse this password");
		
		return errorMessages;
	}
}
