package com.picsauditing.PICS;

import java.util.Date;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class ContractorValidator {
	public final int MIN_PASSWORD_LENGTH = 5;
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected UserDAO userDAO;

	public Vector<String> validateContractor(ContractorAccount contractor) {
		Vector<String> errorMessages = new Vector<String>();
		if (contractor.getType() == null) {
			errorMessages.addElement("Please indicate the account type.");
			return errorMessages;
		}

		// Company Name
		if (Strings.isEmpty(contractor.getName()))
			errorMessages.addElement("Please fill in the Company Name field.");
		else if (contractor.getName().length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long.");

		if (Strings.isEmpty(contractor.getAddress()))
			errorMessages.addElement("Please fill in the Address field.");
		if (Strings.isEmpty(contractor.getCity()))
			errorMessages.addElement("Please fill in the City field.");
		if (contractor.getCountry() == null || Strings.isEmpty(contractor.getCountry().getIsoCode()))
			errorMessages.addElement("Please select a Country");
		if (contractor.getCountry().getIsoCode().equals("US") || contractor.getCountry().getIsoCode().equals("CA")) {
			if (contractor.getState() == null || Strings.isEmpty(contractor.getState().getIsoCode())) {
				errorMessages.addElement("Please select a State");
			}
		}

		if (Strings.isEmpty(contractor.getPhone()))
			errorMessages.addElement("Please fill in the Phone field.");

		boolean uae = false;
		if (contractor.getCountry() != null && contractor.getCountry().getIsoCode().equals("AE"))
			uae = true;

		if (!uae && Strings.isEmpty(contractor.getZip()))
			errorMessages.addElement("Please fill in the Zip field.");
		if (!Strings.isEmpty(contractor.getTaxId())) {
			if (!uae && !java.util.regex.Pattern.matches("\\d{9}", contractor.getTaxId()))
				errorMessages.addElement("Please enter your 9 digit tax ID with only digits 0-9, no dashes.");
		} else if (!uae)
			errorMessages.addElement("Please fill in the Tax ID field");

		// Onsite / Offsite / Material Supplier
		if (contractor.getAccountTypes().isEmpty())
			errorMessages.addElement("Please select at least one of Onsite Services, Offsite Services "
					+ "or Material Supplier to indicate the services you perform.");

		return errorMessages;
	}

	public Vector<String> validateUser(String password1, String password2, User user) {
		Vector<String> errorMessages = new Vector<String>();

		// Username
		if (!Strings.validUserName(user.getUsername()).equals("valid"))
			errorMessages.addElement(Strings.validUserName(user.getUsername()));
		else if (!verifyUsername(user))
			errorMessages.addElement("Username already exists. Please type another.");

		if (Strings.isEmpty(user.getName()))
			errorMessages.addElement("Please fill in the Primary Contact Name");
		if (!Strings.isValidEmail(user.getEmail()))
			errorMessages
					.addElement("Please enter a valid email address. This is our main way of communicating with you.");

		// Passwords
		if (!Strings.isEmpty(password2)) {
			// They are trying to set/reset the password

			if (!password1.equals(password2))
				errorMessages.addElement("The passwords don't match");

			if (password1.length() < MIN_PASSWORD_LENGTH)
				errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
						+ " characters in length.");
			if (password1.equalsIgnoreCase(user.getUsername()))
				errorMessages.addElement("Please choose a password different from your username.");
			if (password1.equalsIgnoreCase("password"))
				errorMessages.addElement("You can't use that password");
			// TODO - Remove Side-effect
			if (errorMessages.size() == 0) {
				user.setPassword(password1);
				user.setPasswordChanged(new Date());
			}
		}

		return errorMessages;
	}

	public boolean verifyUsername(User user) {
		User foundUser = userDAO.findName(user.getUsername());
		if (foundUser == null)
			return true;
		if (user.getId() > 0) {
			user = userDAO.find(user.getId());
			if (foundUser.equals(user))
				return true;
		}
		return false;
	}

	public boolean verifyTaxID(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDAO.findTaxID(contractorAccount.getTaxId(), contractorAccount
				.getCountry().getIsoCode());
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
