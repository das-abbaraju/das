package com.picsauditing.access;

public class UserIndexPage {
	public static String getIndexURL(Permissions permissions) {
		if (permissions == null || !permissions.isLoggedIn()) {
			return "Login.action";
		}

		if (permissions.isContractor()) {
			if (permissions.getAccountStatus().isActive()) {
				return "ContractorView.action";
			} else {
				return "RegistrationMakePayment.action";
			}
		}

		return "Home.action";
	}
}
