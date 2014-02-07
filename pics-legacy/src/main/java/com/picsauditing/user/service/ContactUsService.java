package com.picsauditing.user.service;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.user.model.ContactUsInfo;

public class ContactUsService {

	public ContactUsInfo getContactUsInfo(User user) {
		System.out.println("ContactUsService.getContactUsInfo");
		System.out.println("user = " + user);

		return bogusContactInfo();

	}

	public boolean sendMessageToCsr(String subject, String message, User sendingUser) {
		System.out.println("ContactUsService.sendMessageToCsr");
		System.out.println("subject = " + subject);
		System.out.println("message = " + message);
		System.out.println("sendingUser = " + sendingUser);

		return true;
	}

	private ContactUsInfo bogusContactInfo() {
		ContactUsInfo contactUsInfo = new ContactUsInfo();
		contactUsInfo.setCsrPhoneNumber("555-867-5309");
		contactUsInfo.setCsrPhoneNumberExtension("9876");
		contactUsInfo.setCsrName("Joe Sixpack");

		return contactUsInfo;
	}
}
