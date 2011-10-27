package com.picsauditing.mail;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class EmailSenderSpring {
	@Autowired
	private EmailQueueDAO emailQueueDAO;

	private static String defaultPassword = "e3r4t5";

	/**
	 * Try sending with the first email address info@picsauditing.com still fails, then try regular linux sendmail
	 * 
	 * @param email
	 */
	private void sendMail(EmailQueue email, int attempts) throws AddressException {
		attempts++;

		if (attempts > 3)
			throw new AddressException();
		
		try {
			if (checkDeactivated(email))
				return;

			GridSender gridSender;
			if (!Strings.isEmpty(email.getFromPassword())) {
				// Use a specific email address like
				// tallred@picsauditing.com
				// We need the password to correctly authenticate with GMail
				PicsLogger.log("using SendGrid to send email from " + email.getFromAddress());
				gridSender = new GridSender(email.getFromAddress(), email.getFromPassword());
			} else {
				// Use the default info@picsauditing.com address
				PicsLogger.log("using SendGrid to send email from info@picsauditing.com");
				gridSender = new GridSender("info@picsauditing.com", defaultPassword);
			}
			gridSender.sendMail(email);

			email.setStatus(EmailStatus.Sent);
			email.setSentDate(new Date());

			emailQueueDAO.save(email);
		} catch (AddressException e) {
			email.setStatus(EmailStatus.Error);
			emailQueueDAO.save(email);
		} catch (MessagingException e) {
			PicsLogger.log("Send Mail Exception with account info@picsauditing.com: " + e.toString() + " "
					+ e.getMessage() + "\nFROM: " + email.getFromAddress() + "\nTO: " + email.getToAddresses()
					+ "\nSUBJECT: " + email.getSubject());

			this.sendMail(email, attempts);
		}
	}

	private boolean checkDeactivated(EmailQueue email) {
		if (email.getContractorAccount() != null && email.getContractorAccount().getStatus().isDeactivated()) {
			if (email.getEmailTemplate() != null
					&& !EmailTemplate.VALID_DEACTIVATED_EMAILS().contains(email.getEmailTemplate().getId())) {
				email.setStatus(EmailStatus.Error);
				email.setSentDate(new Date());
				PicsLogger.log("Skipping Email \nFROM: " + email.getFromAddress() + "\nTO: " + email.getToAddresses()
						+ "\nSUBJECT: " + email.getSubject());
				emailQueueDAO.save(email);
				return true;
			}
		}
		return false;
	}

	/**
	 * Send this through GMail or SendMail
	 * 
	 * @param email
	 * @throws Exception
	 */
	public void sendNow(EmailQueue email) {
		PicsLogger.start("EmailSender", email.getSubject() + " to " + email.getToAddresses());
		try {
			email.cleanupEmailAddresses();

			if (checkDeactivated(email))
				return;
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

		} catch (AddressException e) {
			email.setStatus(EmailStatus.Error);
			emailQueueDAO.save(email);
		} finally {
			PicsLogger.stop();
		}
	}

	/**
	 * Save this email to the queue for sending later
	 * 
	 * @param email
	 */
	public void send(EmailQueue email) {
		emailQueueDAO.save(email);
	}

	/**
	 * Save this email to the queue for sending later
	 */
	public void send(String fromAddress, String toAddress, String ccAddress, String subject, String body) {
		EmailQueue email = new EmailQueue();
		email.setCreationDate(new Date());
		email.setSubject(subject);
		email.setBody(body);
		email.setFromAddress(fromAddress);
		email.setToAddresses(toAddress);
		email.setCcAddresses(ccAddress);
		email.setCreationDate(new Date());
		send(email);
	}

	public void send(String toAddress, String subject, String body) throws Exception {
		send(null, toAddress, null, subject, body);
	}

}
