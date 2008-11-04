package com.picsauditing.PICS;

import java.util.Vector;

import com.picsauditing.jpa.entities.User;

public class PasswordValidator {
	public final static int MIN_PASSWORD_LENGTH = 8;

	public PasswordValidator() {
	}

	public static Vector<String> validateContractor(User user) {
		Vector<String> errorMessages = new Vector<String>();
		if (user.getPassword() == null || user.getPassword().length() < MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
					+ " characters in length.");
		if (user.getPassword() == null || user.getPassword().equalsIgnoreCase(user.getUsername()))
			errorMessages.addElement("Please choose a password different from your username.");
		if (user.getPassword().matches(".*[a-z][A-Z].*") && user.getPassword().matches(".*[0-9].*"))
			errorMessages.addElement("Your password should contain digits and letters");
		return errorMessages;
	}
}
