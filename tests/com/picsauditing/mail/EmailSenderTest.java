package com.picsauditing.mail;

import junit.framework.TestCase;

public class EmailSenderTest extends TestCase {
	public EmailSenderTest(String name) {
		super(name);
	}
	
	public void testEmail() {
		GMailSender mailer = new GMailSender("tallred@picsauditing.com", "tghf@))&");
		try {
			mailer.sendMail("Test Email", "This is the email body", "tallred@picsauditing.com", "trevorallred@gmail.com");
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
}
