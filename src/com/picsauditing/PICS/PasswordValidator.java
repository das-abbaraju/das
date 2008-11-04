package com.picsauditing.PICS;

import java.util.Vector;

public class PasswordValidator {
	public final static int MIN_PASSWORD_LENGTH = 8;

	public PasswordValidator() {
	}

	public static Vector<String> validateContractor(String password, String userName) {
		Vector<String> errorMessages = new Vector<String>();
		if (password == null || password.length() <= MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
					+ " characters in length.");
		if (password == null || password.equalsIgnoreCase(userName))
			errorMessages.addElement("Please choose a password different from your username.");
		if (!password.matches(".*[a-z][A-Z].*") || !password.matches(".*[0-9].*"))
			errorMessages.addElement("Your password should contain digits and letters");
		return errorMessages;
	}
}
