package com.picsauditing.mail;

import java.util.Date;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class EmailSender {
	private static String defaultPassword = "e3r4t5";
	private EmailQueueDAO emailQueueDAO = null;

	/**
	 * Try sending with the first email address info@picsauditing.com
	 * still fails, then try regular linux sendmail
	 * 
	 * @param email
	 * @throws Exception
	 */
	private void sendMail(EmailQueue email, int attempts) throws Exception {
		attempts++;
		boolean useSendGrid = true;
		if (attempts > 2 || email.getToAddresses().endsWith("@picsauditing.com"))
			useSendGrid = false;
		
		try {
			if (useSendGrid) {
				GridSender gridSender;
				if (!Strings.isEmpty(email.getFromPassword())) {
					// Use a specific email address like tallred@picsauditing.com
					// We need the password to correctly authenticate with GMail
					PicsLogger.log("using SendGrid to send email from " + email.getFromAddress());
					gridSender = new GridSender(email.getFromAddress(), email.getFromPassword());
				} else {
					// Use the default info@picsauditing.com address
					PicsLogger.log("using SendGrid to send email from info@picsauditing.com");
					gridSender = new GridSender("info@picsauditing.com", defaultPassword);
				}
				gridSender.sendMail(email);
			} else {
				PicsLogger.log("using localhost sendmail to send");
				SendMail sendMail = new SendMail();
				sendMail.send(email);
			}
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
			PicsLogger.log("Send Mail Exception with account info@picsauditing.com: " + e.toString() + " "
					+ e.getMessage() + "\nFROM: " + email.getFromAddress() + "\nTO: " + email.getToAddresses()
					+ "\nSUBJECT: " + email.getSubject());
			if (useSendGrid) {
				this.sendMail(email, attempts);
			} else {
				PicsLogger.log("Failed to send email using sendmail...exiting");
				email.setStatus(EmailStatus.Error);

				if (emailQueueDAO == null)
					emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
				
				emailQueueDAO.save(email);
			}
		}
	}

	/**
	 * Send this through GMail or SendMail
	 * 
	 * @param email
	 * @throws Exception
	 */
	public void sendNow(EmailQueue email) throws Exception {
		PicsLogger.start("EmailSender", email.getSubject() + " to " + email.getToAddresses());
		try {
			// Check all the addresses
			if (email.getFromAddress2() == null)
				email.setFromAddress("info@picsauditing.com");
			if (email.getToAddresses2() == null) {
				email.setToAddresses(email.getCcAddresses());
				email.setCcAddresses(null);
			}
			email.getCcAddresses2();
			email.getBccAddresses2();
			
			sendMail(email, 0);
			
		} catch (javax.mail.internet.AddressException e) {
			email.setStatus(EmailStatus.Error);
			if (emailQueueDAO == null)
				emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
			emailQueueDAO.save(email);
		} finally {
			PicsLogger.stop();
		}
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
