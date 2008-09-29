package com.picsauditing.PICS;

import java.util.Vector;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorValidator {
	public final int MIN_PASSWORD_LENGTH = 5;
	protected ContractorAccountDAO contractorAccountDAO;

	public ContractorValidator(ContractorAccountDAO contractorAccountDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public Vector<String> validateContractor(ContractorAccount contractor) {
		Vector<String> errorMessages = new Vector<String>();
		if (contractor.getType() == null)
			errorMessages.addElement("Please indicate the account type.");
		if (!verifyUsername(contractor))
			errorMessages.addElement("Username already exists. Please type another.");
		if (contractor.getName() == null || contractor.getName().length() == 0)
			errorMessages.addElement("Please fill in the Company Name field.");
		if (contractor.getName() == null || contractor.getName().length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long.");
		if ("Contractor".equals(contractor.getType())) {
			if (contractor.getUsername() == null || contractor.getUsername().length() == 0)
				errorMessages.addElement("Please fill in the Username field.");
			if (contractor.getPassword() == null || contractor.getPassword().length() < MIN_PASSWORD_LENGTH)
				errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
						+ " characters in length.");
			if (contractor.getPassword() == null || contractor.getPassword().equalsIgnoreCase(contractor.getUsername()))
				errorMessages.addElement("Please choose a password different from your username.");
		}
		// Don't check these fields if auditor BJ 10-28-04
		if (contractor.getType() == null || !contractor.getType().equals("Auditor")) {
			if (contractor.getContact() == null || contractor.getContact().length() == 0)
				errorMessages.addElement("Please fill in the Contact field.");
			if (contractor.getAddress() == null || contractor.getAddress().length() == 0)
				errorMessages.addElement("Please fill in the Address field.");
			if (contractor.getCity() == null || contractor.getAddress().length() == 0)
				errorMessages.addElement("Please fill in the City field.");
			if (contractor.getZip() == null || contractor.getZip().length() == 0)
				errorMessages.addElement("Please fill in the Zip field.");
			if (contractor.getPhone() == null || contractor.getPhone().length() == 0)
				errorMessages.addElement("Please fill in the Phone field.");
		}
		if ((contractor.getEmail() == null || contractor.getEmail().length() == 0)
				|| (!Utilities.isValidEmail(contractor.getEmail())))
			errorMessages
					.addElement("Please enter a valid email address. This is our main way of communicating with you so it must be valid.");
		return errorMessages;
	}

	public boolean verifyUsername(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDAO.findName(contractorAccount.getUsername());
		if (cAccount == null || cAccount.equals(contractorAccount))
			return true;

		return false;
	}
}
