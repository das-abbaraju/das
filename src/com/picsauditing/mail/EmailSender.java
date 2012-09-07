package com.picsauditing.mail;

import java.util.Date;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.messaging.Publisher;
import com.picsauditing.toggle.FeatureToggleChecker;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

public class EmailSender {
	private final Logger logger = LoggerFactory.getLogger(EmailSender.class);
	
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private FeatureToggleChecker featureToggleChecker;
	
	// this is @Autowired at the setter because we need @Qualifier which does NOT work
	// on the variable declaration; only on the method (I think this is a Spring bug)
	private Publisher emailQueuePublisher;

	@Autowired
	@Qualifier("EmailQueuePublisher")
	public void setEmailQueuePublisher(Publisher emailQueuePublisher) {
		this.emailQueuePublisher = emailQueuePublisher;
	}

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
			gridSender = new GridSender(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS, EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS_PASSWORD);
		}
		gridSender.sendMail(email);

		email.setStatus(EmailStatus.Sent);
		email.setSentDate(new Date());

		emailQueueDAO.save(email);
	}

	private boolean checkDeactivated(EmailQueue email) {
		if (contractorIsDeactivated(email) && !emailTemplateIsValidForDeactivatedContractors(email)) {
			logEmailAsSendError(email);
			return true;
		}
		
		return false;
	}

	private void logEmailAsSendError(EmailQueue email) {
		logger.warn("Skipping Email \nFROM: {}\nTO: {}\nSUBJECT: {}", new Object[] {email.getFromAddress(), email.getToAddresses(), email.getSubject()});
		email.setStatus(EmailStatus.Error);
		email.setSentDate(new Date());
		emailQueueDAO.save(email);
	}

	private boolean contractorIsDeactivated(EmailQueue email) {
		return (email.getContractorAccount() != null && email.getContractorAccount().getStatus().isDeactivated());
	}
	
	private boolean emailTemplateIsValidForDeactivatedContractors(EmailQueue email) {
		if (email.getEmailTemplate() == null) {
			// this duplicates previous behavior before I refactored - if the template is null, then
			// it is valid for deactivated contractors
			return true;
		} else { 
			return EmailTemplate.VALID_DEACTIVATED_EMAILS().contains(email.getEmailTemplate().getId());
		}
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
			email.setFromAddress(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS);
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
		publishEnterpriseMessageIfEmailShouldBeSent(email);
	}

	public void publishSubscription(EmailQueue email) {
		emailQueuePublisher.publish(email, "email-subscription");
	}
	
	public void publish(EmailQueue email) {
		publishEnterpriseMessageIfEmailShouldBeSent(email);
	}
	
	private void publishEnterpriseMessageIfEmailShouldBeSent(EmailQueue email) {
		if (contractorIsDeactivated(email) && !emailTemplateIsValidForDeactivatedContractors(email)) {
			// this will write to the database NOW, as opposed to on actual sending of the email we're 
			// only going to do this if the feature is enabled. We'll log now and not publish for sending
			if (featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.EmailQueue")) {
				logEmailAsSendError(email);
			}
		} else {
			if (email.getPriority() >= EmailQueue.HIGH_PRIORITY) {
				emailQueuePublisher.publish(email, "email-queue-priority");
			} else {
				emailQueuePublisher.publish(email, "email-queue-normal");
			}
		}
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
