package com.picsauditing.PICS;

import java.util.Date;
import java.util.Vector;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

public class ContractorValidator {
	public final int MIN_PASSWORD_LENGTH = 5;
	protected ContractorAccountDAO contractorAccountDAO;

	public ContractorValidator(ContractorAccountDAO contractorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public Vector<String> validateContractor(ContractorAccount contractor, String password1, String password2) {
		Vector<String> errorMessages = new Vector<String>();
		if (contractor.getType() == null) {
			errorMessages.addElement("Please indicate the account type.");
			return errorMessages;
		}

		// Username
		if (Strings.isEmpty(contractor.getUsername()))
			errorMessages.addElement("Please fill in the Username field.");
		else if (!verifyUsername(contractor))
			errorMessages.addElement("Username already exists. Please type another.");

		// Company Name
		if (Strings.isEmpty(contractor.getName()))
			errorMessages.addElement("Please fill in the Company Name field.");
		else if (contractor.getName().length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long.");

		// Passwords
		if (!Strings.isEmpty(password1)) {
			// They are trying to set/reset the password
			if (!password1.equals(password2) && !password1.equals(contractor.getPassword()))
				errorMessages.addElement("The passwords don't match");

			if (password1.length() < MIN_PASSWORD_LENGTH)
				errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
						+ " characters in length.");
			if (password1.equalsIgnoreCase(contractor.getUsername()))
				errorMessages.addElement("Please choose a password different from your username.");
			if (password1.equalsIgnoreCase("password"))
				errorMessages.addElement("You can't use that password");
			if (errorMessages.size() == 0) {
				contractor.setPassword(password1);
				contractor.setPasswordChange(new Date());
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
			errorMessages.addElement("Pleae enter your 9 digit tax ID with only digits 0-9, no dashes.");
		else if (!verifyTaxID(contractor))
			errorMessages.addElement("The tax ID <b>" + contractor.getTaxId()
					+ "</b> already exists.  Please contact a company representative.");
		// Main Trade
		if (Strings.isEmpty(contractor.getMainTrade())
				|| contractor.getMainTrade().equals("- Trade -"))
			errorMessages.addElement("Please select a main trade");

		return errorMessages;
	}

	public boolean verifyUsername(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDAO.findName(contractorAccount.getUsername());
		if (cAccount == null || cAccount.equals(contractorAccount))
			return true;

		return false;
	}

	public boolean verifyTaxID(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDAO.findName(contractorAccount.getTaxId());
		if (cAccount == null || cAccount.equals(contractorAccount))
			return true;

		return false;
	}

}
