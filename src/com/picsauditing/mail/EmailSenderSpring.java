package com.picsauditing.mail;

import java.util.Date;

import javax.mail.MessagingException;

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
	 * 
	 * @param email
	 * @throws MessagingException
	 */
	private void sendMail(EmailQueue email) throws MessagingException {
		if (checkDeactivated(email))
			return;

		GridSender gridSender;
		if (!Strings.isEmpty(email.getFromPassword())) {
			// TODO We don't use Gmail anymore. SendGrid only accepts a single username (info@pics) So we should remove this section
			// We need the password to correctly authenticate with GMail
			gridSender = new GridSender(email.getFromAddress(), email.getFromPassword());
		} else {
			// Use the default info@picsauditing.com address
			gridSender = new GridSender("info@picsauditing.com", defaultPassword);
		}
		gridSender.sendMail(email);

		email.setStatus(EmailStatus.Sent);
		email.setSentDate(new Date());

		emailQueueDAO.save(email);
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
	 * @throws MessagingException
	 */
	public void sendNow(EmailQueue email) throws MessagingException {
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

		sendMail(email);
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
	private void send(String fromAddress, String toAddress, String ccAddress, String subject, String body) {
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
