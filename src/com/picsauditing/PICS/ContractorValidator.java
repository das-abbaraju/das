package com.picsauditing.PICS;

import java.util.Date;
import java.util.Vector;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class ContractorValidator {
	public final int MIN_PASSWORD_LENGTH = 5;
	protected ContractorAccountDAO contractorAccountDAO;
	protected UserDAO userDAO;

	public ContractorValidator(ContractorAccountDAO contractorAccountDAO, UserDAO userDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.userDAO = userDAO;
	}

	public Vector<String> validateContractor(ContractorAccount contractor, String password1, String password2, User user) {
		Vector<String> errorMessages = new Vector<String>();
		if (contractor.getType() == null) {
			errorMessages.addElement("Please indicate the account type.");
			return errorMessages;
		}

		// Username
		if (Strings.isEmpty(user.getUsername()))
			errorMessages.addElement("Please fill in the Username field.");
		else if (!verifyUsername(user))
			errorMessages.addElement("Username already exists. Please type another.");

		// Company Name
		if (Strings.isEmpty(contractor.getName()))
			errorMessages.addElement("Please fill in the Company Name field.");
		else if (contractor.getName().length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long.");

		// Passwords
		if (!Strings.isEmpty(password1)) {
			// They are trying to set/reset the password
			if (!password1.equals(password2) && !password1.equals(user.getPassword()))
				errorMessages.addElement("The passwords don't match");

			if (password1.length() < MIN_PASSWORD_LENGTH)
				errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
						+ " characters in length.");
			if (password1.equalsIgnoreCase(user.getUsername()))
				errorMessages.addElement("Please choose a password different from your username.");
			if (password1.equalsIgnoreCase("password"))
				errorMessages.addElement("You can't use that password");
			if (errorMessages.size() == 0) {
				user.setPassword(password1);
				user.setPasswordChanged(new Date());
			}
		}

		// Contact and Address info
		if (Strings.isEmpty(contractor.getContact()))
			errorMessages.addElement("Please fill in the Contact field.");
		if (Strings.isEmpty(contractor.getAddress()))
			errorMessages.addElement("Please fill in the Address field.");
		if (Strings.isEmpty(contractor.getCity()))
			errorMessages.addElement("Please fill in the City field.");
		if (Strings.isEmpty(contractor.getZip()))
			errorMessages.addElement("Please fill in the Zip field.");
		if (Strings.isEmpty(contractor.getPhone()))
			errorMessages.addElement("Please fill in the Phone field.");

		if (Strings.isEmpty(contractor.getEmail()) || !Utilities.isValidEmail(contractor.getEmail()))
			errorMessages
					.addElement("Please enter a valid email address. This is our main way of communicating with you.");
		
		// Risk Level
		if(contractor.getRiskLevel() == null)
			errorMessages
			.addElement("Please select a riskLevel");
		
		// Tax Id
		if (!java.util.regex.Pattern.matches("\\d{9}", contractor.getTaxId()))
			errorMessages.addElement("Please enter your 9 digit tax ID with only digits 0-9, no dashes.");

		// Main Trade
		if (Strings.isEmpty(contractor.getMainTrade())
				|| contractor.getMainTrade().equals("- Trade -"))
			errorMessages.addElement("Please select a main trade");

		return errorMessages;
	}

	public boolean verifyUsername(User user) {
		User foundUser = userDAO.findName(user.getUsername());
		if(!userDAO.isContained(user))
			user = userDAO.find(user.getId());
		if (foundUser == null || foundUser.equals(user))
			return true;

		return false;
	}

	public boolean verifyTaxID(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDAO.findTaxID(contractorAccount.getTaxId());
		if (cAccount == null || cAccount.equals(contractorAccount))
			return true;

		return false;
	}

	public boolean verifyName(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDAO.findConID(contractorAccount.getName());
		if (cAccount == null || cAccount.equals(contractorAccount))
			return true;

		return false;
	}
}
