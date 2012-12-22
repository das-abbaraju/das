package com.picsauditing.util;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.jpa.entities.Currency;

// TODO pull out email addresses into their own file
public class EmailAddressUtils {
	
	// TODO: This should be further refactored into a configuration file
	public static final String PICS_INFO_EMAIL_ADDRESS = "info@picsauditing.com";
	public static final String PICS_INFO_EMAIL_ADDRESS_WITH_NAME = "PICS Info <" + PICS_INFO_EMAIL_ADDRESS + ">";
	// TODO Don't store plaintext passwords in source code
	public static final String PICS_INFO_EMAIL_ADDRESS_PASSWORD = "PicsS@fety1";
	
	public static final String PICS_ERROR_EMAIL_ADDRESS = "errors@picsauditing.com";
	public static final String PICS_ERROR_EMAIL_ADDRESS_WITH_NAME = "\"IT\"<" + PICS_ERROR_EMAIL_ADDRESS + ">";
	public static final String PICS_EXCEPTION_HANDLER_EMAIL = "\"PICS Exception Handler\"<" + PICS_ERROR_EMAIL_ADDRESS + ">";
	
	public static final String PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS = "\"PICS Customer Service\"<" + PICS_INFO_EMAIL_ADDRESS + ">";
	public static final String PICS_SYSTEM_EMAIL_ADDRESS = "\"PICS System\"<" + PICS_INFO_EMAIL_ADDRESS + ">";
	public static final String PICS_REGISTRATION_EMAIL_ADDRESS = "Registrations@picsauditing.com";
	
	public static final String PICS_MARKETING_EMAIL_ADDRESS = "marketing@picsauditing.com";
	public static final String PICS_MARKETING_EMAIL_ADDRESS_WITH_NAME = "\"PICS Marketing\"<" + PICS_MARKETING_EMAIL_ADDRESS + ">";
	
	public static final String PICS_AUDIT_EMAIL_ADDRESS = "audits@picsauditing.com";
	public static final String PICS_AUDIT_EMAIL_ADDRESS_WITH_NAME = "\"PICS Auditing\"<" + PICS_AUDIT_EMAIL_ADDRESS + ">";
	
	public static final String PICS_IT_TEAM_EMAIL = "\"PICS IT Team\"<it@picsauditing.com>";
	
	public static final String MINA_MINA_EMAIL = "Mina Mina <mmina@picsauditing.com>";
	
	public static final String PICS_ADMIN_EMAIL = "admin@picsauditing.com";
	
	public static final String PICS_FLAG_CHANGE_EMAIL = "flagchanges@picsauditing.com";
	
	public static final String PICS_TECH_SERVICES = "TechServices@picsauditing.com";
	
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

	/** Use InputValidator::validateEmail() going forward */
	@Deprecated
	public static boolean isValidEmail(String email) {
		boolean result = false;
		if (Strings.isEmpty(email) || email.trim().contains(" "))
			return false;
				
		int index = email.indexOf("@");
		if (index > 0) {
			int pindex = email.indexOf(".", index);
			if ((pindex > index + 1) && (email.length() > pindex + 1))
				result = true;
		}
		
		return result;
	}

	/** Use InputValidator::validateEmail() going forward */
	@Deprecated
	public static String validate(final String email) {
		String tempEmail = email.trim();

		boolean matchFound = isValidEmail(tempEmail);

		if (matchFound)
			return tempEmail;
		else
			return PICS_INFO_EMAIL_ADDRESS;

	}

	// TODO possibly move this to I18nCache, or some other localization class
	@Deprecated
	public static String getBillingEmail(Currency currency) {
		if (currency != null && (currency.isEUR() || currency.isGBP())){
			return "\"PICS Billing\"<eubilling@picsauditing.com>";
		} else {
			return "\"PICS Billing\"<billing@picsauditing.com>";
		} 
	}
	
}
