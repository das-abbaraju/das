package com.picsauditing.mail;

import junit.framework.TestCase;

public class EmailSenderTest extends TestCase {
	public EmailSenderTest(String name) {
		super(name);
	}

	public void testInfoEmail() {
		String username = "info@picsauditing.com";
		String password = "e3r4t5";
		GMailSender mailer = new GMailSender(username, password);
		try {
			mailer.sendMail("Test Activation", 
					"Welcome John Doe,\n\nThis is a test email", 
					"Trevor Allred <tallred@picsauditing.com>", 
					"tester@picsauditing.com",
					"tallred@picsauditing.com",
					"tester@picsauditing.com");
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }

}
