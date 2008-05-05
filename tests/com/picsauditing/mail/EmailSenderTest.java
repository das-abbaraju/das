package com.picsauditing.mail;

import junit.framework.TestCase;

public class EmailSenderTest extends TestCase {
	public EmailSenderTest(String name) {
		super(name);
	}
	
	/*
	public void testEmail() {
		String username = "tester@picsauditing.com";
		String password = "bigboy11";
		GMailSender mailer = new GMailSender(username, password);
		try {
			mailer.sendMail("Test Email", "This is the email body", "PICS Info <tester@picsauditing.com>", "tester@picsauditing.com");
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
    */

	public void testInfoEmail() {
		String username = "info@picsauditing.com";
		String password = "e3r4t5";
		GMailSender mailer = new GMailSender(username, password);
		try {
			mailer.sendMail("Account Activation", 
					"Welcome John Doe,\n\nPlease click on this link to confirm your receipt of this email:\nhttp://www.picsauditing.com/login.jsp?uname=canoo_contractor1", 
					"Trevor Allred <tallred@picsauditing.com>", 
					"tallred@picsauditing.com");
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }


	public void testSender() {
		Email email = new Email();
		email.setSubject("JUnit Test");
		email.setBody("Test body");
		email.setFromAddress("Trevor Allred <tallred@picsauditing.com>");
		email.setToAddress("tester@picsauditing.com");
		
		EmailSender sender = new EmailSender();
		try {
			sender.sendMail(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
}
