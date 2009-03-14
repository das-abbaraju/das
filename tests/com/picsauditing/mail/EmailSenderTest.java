package com.picsauditing.mail;

import com.picsauditing.jpa.entities.EmailQueue;

import junit.framework.TestCase;

public class EmailSenderTest extends TestCase {
	public EmailSenderTest(String name) {
		super(name);
	}

	public void testInfoEmail() {
		String username = "info@picsauditing.com";
		String password = "e3r4t5";
		GMailSender mailer = new GMailSender(username, password);
		EmailQueue email = new EmailQueue();
		email.setSubject("Test Activation");
		email.setBody("Welcome John Doe,\n\nThis is a test email");
		email.setToAddresses("Trevor Allred <tallred@picsauditing.com>");
		email.setCcAddresses("tester@picsauditing.com");
		email.setBccAddresses("tallred@picsauditing.com");
		
		try {
			mailer.sendMail(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }

}
