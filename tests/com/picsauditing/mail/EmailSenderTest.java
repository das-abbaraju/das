package com.picsauditing.mail;

import java.util.Date;

import junit.framework.TestCase;

import com.picsauditing.jpa.entities.EmailQueue;

public class EmailSenderTest extends TestCase {
	public EmailSenderTest(String name) {
		super(name);
	}

	public void testInfoEmail() {
		String username = "info@picsauditing.com";
		String password = "e3r4t5";
		GMailSender mailer = new GMailSender(username, password);
		EmailQueue email = new EmailQueue();
		email.setSubject("jUnit Test");
		email.setFromAddress("PICS Mailer <info@picsauditing.com>");
		email.setFromAddress("Trevor <tallred@picsauditing.com>");
		email.setBody("This is a test from EmailSenderTest.testInfoEmail()");
		//email.setToAddresses("jsmith@picsauditing.com");
		email.setToAddresses("tallred@picsauditing.com");
		email.setCcAddresses("knannapaneni@picsauditing.com");
		email.setCreationDate(new Date());
		try {
			mailer.sendMail(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
	
	public void testSender() {
		EmailQueue email = new EmailQueue();
		email.setSubject("Test Activation");
		email.setFromAddress("PICS Mailer<info@picsauditing.com>");
		email.setBody("Welcome John Doe,\n\nThis is a test email");
		email.setToAddresses("knannapaneni@picsauditing.com");
		email.setCcAddresses("tallred@picsauditing.com");
		email.setCreationDate(new Date());
		try {
			SendMail sendMail = new SendMail();
			sendMail.send(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

}
