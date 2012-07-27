package com.picsauditing.util;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.Currency;

public class EmailAddressUtils {
	public static Set<String> findUniqueEmailAddresses(String emailAddresses) {
		Set<String> validEmail = new HashSet<String>();

		if (!Strings.isEmpty(emailAddresses)) {
			String[] list1 = emailAddresses.split(",");
			for (String email : list1) {
				if (isValidEmail(email))
					validEmail.add(email);
			}
		}
		return validEmail;
	}

	public static boolean isValidEmail(String email) {
		boolean result = false;
		if (Strings.isEmpty(email) || email.trim().contains(" "))
			return false;
		int index = email.indexOf("@");
		if (index > 0) {
			int pindex = email.indexOf(".", index);
			if ((pindex > index + 1) && (email.length() > pindex + 1))
				result = true;
		}// if
		return result;
	}// isValidEmail

	public static String validate(final String email) {
		String tempEmail = email.trim();

		boolean matchFound = isValidEmail(tempEmail);

		if (matchFound)
			return tempEmail;
		else
			return "info@picsauditing.com";

	}

	public static String getBillingEmail(Currency currency) {
		if (currency != null
				&& (currency.isEUR() || currency.isGBP() || currency.isDKK() || currency.isSEK() || currency.isNOK() || currency
						.isZAR())) {
			return "\"PICS Billing\"<eubilling@picsauditing.com>";
		} else {
			return "\"PICS Billing\"<billing@picsauditing.com>";
		}
	}
	// public static String validate(final String email){
	// String tempEmail = email.trim();
	// Pattern p =
	// Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	// Pattern p =
	// Pattern.compile("^((?:(?:(?:[a-zA-Z0-9][\.\-\+_]?)*)[a-zA-Z0-9])+)\@((?:(?:(?:[a-zA-Z0-9][\.\-_]?){0,62})[a-zA-Z0-9])+)\.([a-zA-Z0-9]{2,6})$");
	// Matcher m = p.matcher(tempEmail);
	// boolean matchFound = m.matches();
	//
	// if(matchFound)
	// return tempEmail;
	// else
	// return "billing@picsauditing.com";
	//
	// }
}
