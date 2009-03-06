package com.picsauditing.mail;

import java.util.Date;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class EmailSender extends GMailSender {
	private static int currentDefaultSender = 1;
	private static final int NUMBER_OF_GMAIL_ACOUNTS = 12;
	private static String defaultPassword = "e3r4t5";
	private EmailQueueDAO emailQueueDAO = null;

	public EmailSender() {
		super(getDefaultSender(), defaultPassword);
	}

	public EmailSender(String user, String password) {
		super(user, password);
	}

	/**
	 * Try sending with the first email address info@picsauditing.com If it
	 * errors try info2, info3, ...info6. If it still fails, then exit
	 * 
	 * @param email
	 * @throws Exception
	 */
	private void sendMail(EmailQueue email, int attempts) throws Exception {
		attempts++;
		if (attempts > NUMBER_OF_GMAIL_ACOUNTS)
			throw new Exception("Failed to send email, used all possible senders. See log file for more info.");
		try {
			String fromAddress = "";
			if (email.getFromAddress() != null && email.getFromAddress().length() > 7)
				fromAddress = email.getFromAddress();
			else
				fromAddress = getDefaultSender();
			
			html = email.isHtml();
			
			this.sendMail(email.getSubject(), email.getBody(), fromAddress, email.getToAddresses(), email.getBccAddresses(), email.getCcAddresses());
			email.setStatus(EmailStatus.Sent);
			email.setSentDate(new Date());
			
			if (emailQueueDAO == null)
				emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
			
			emailQueueDAO.save(email);
		} catch (javax.mail.internet.AddressException e) {
			email.setStatus(EmailStatus.Error);
			if (emailQueueDAO == null)
				emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
			emailQueueDAO.save(email);
		} catch (Exception e) {
			System.out.println("Send Mail Exception with account " + currentDefaultSender + ": " + e.toString() + " "
					+ e.getMessage() + 
					"\nFROM: " + email.getFromAddress() + 
					"\nTO: " + email.getToAddresses() + 
					"\nSUBJECT: " + email.getSubject());
			changeDefaultSender();
			this.sendMail(email, attempts);
		}
	}

	/**
	 * Send this through GMail
	 * 
	 * @param email
	 * @throws Exception
	 */
	public void sendNow(EmailQueue email) throws Exception {
		sendMail(email, 0);
	}

	private static String getDefaultSender() {
		if (EmailSender.currentDefaultSender >= 2)
			return "info" + currentDefaultSender + "@picsauditing.com";
		else
			return "info@picsauditing.com";
	}

	private static void changeDefaultSender() {
		currentDefaultSender = (currentDefaultSender % NUMBER_OF_GMAIL_ACOUNTS) + 1;
	}

	/**
	 * Save this email to the queue for sending later
	 * 
	 * @param email
	 * @throws Exception
	 */
	public static void send(EmailQueue email) throws Exception {
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
		emailQueueDAO.save(email);
	}

	/**
	 * Save this email to the queue for sending later
	 */
	public static void send(String fromAddress, String toAddress, String ccAddress, String subject, String body)
			throws Exception {
		EmailQueue email = new EmailQueue();
		email.setCreationDate(new Date());
		email.setSubject(subject);
		email.setBody(body);
		email.setFromAddress(fromAddress);
		email.setToAddresses(toAddress);
		email.setCcAddresses(ccAddress);
		email.setCreationDate(new Date());
		EmailSender.send(email);
	}

	public static void send(String toAddress, String subject, String body) throws Exception {

		send(null, toAddress, null, subject, body);
	}

}
