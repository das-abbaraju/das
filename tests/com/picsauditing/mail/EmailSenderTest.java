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
			mailer.sendMail("Account Activation", 
					"Welcome John Doe,\n\nPlease click on this link to confirm your receipt of this email:\nhttp://www.picsauditing.com/login.jsp?uname=canoo_contractor1", 
					"Trevor Allred <tallred@picsauditing.com>", 
					"tallred@picsauditing.com");
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }


	public void testSender() {
		try {
			EmailSender.send("Trevor Allred <tallred@picsauditing.com>", 
					"tester@picsauditing.com", null, "JUnit Test", "Test body");
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
}
