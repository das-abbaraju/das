package com.picsauditing.mail;

import java.util.Date;

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
		email.setFromAddress("info@picsauditing.com");
		email.setBody("Welcome John Doe,\n\nThis is a test email");
		email.setToAddresses("knannapaneni@picsauditing.com");
		email.setCcAddresses("tallred@picsauditing.com");
		email.setCreationDate(new Date());
		try {
			mailer.sendMail(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
	
	public void testSender() {
		EmailSender sender = new EmailSender();
		EmailQueue email = new EmailQueue();
		email.setSubject("Test Activation");
		email.setFromAddress("info@picsauditing.com");
		email.setBody("Welcome John Doe,\n\nThis is a test email");
		email.setToAddresses("knannapaneni@picsauditing.com");
		email.setCcAddresses("tallred@picsauditing.com");
		email.setCreationDate(new Date());
		try {
			sender.sendNow(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
