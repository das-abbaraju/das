package com.picsauditing.mail;

import java.util.Date;

import junit.framework.TestCase;

import com.picsauditing.jpa.entities.EmailQueue;

public class GridSenderTest extends TestCase {
	public GridSenderTest(String name) {
		super(name);
	}
	
	public void testInfoEmail() {
		String username = "info@picsauditing.com";
		String password = "kkttl5";
		GridSender mailer = new GridSender(username, password);
		EmailQueue email = new EmailQueue();
		email.setSubject("jUnit Test");
		email.setFromAddress("Lani Aung <uaung@picsauditing.com>");
		
		Date date = new Date();
		
		email.setBody("This is a test from GridSenderTest.testInfoEmail(). " + date);
		email.setToAddresses("laung@picsauditing.com");
		email.setCreationDate(new Date());
		try {
			long now = System.currentTimeMillis();
			mailer.sendMail(email);
			System.out.println("Sendmail: " + (System.currentTimeMillis() - now));
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
}
