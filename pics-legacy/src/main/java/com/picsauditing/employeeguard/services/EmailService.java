package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailService {

	public static final int EG_WELCOME_EMAIL = 357;

	@Autowired
	private EmailSender emailSender;

	public void sendEGWelcomeEmail(final EmailHash emailHash, final String accountName) throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();

		emailBuilder.setToAddresses(emailHash.getEmailAddress());
		emailBuilder.setFromAddress("PICS <info@picsauditing.com>");
		emailBuilder.setTemplate(EG_WELCOME_EMAIL);
		emailBuilder.addToken("hash", new EmailHashWrapper(emailHash));
		emailBuilder.addToken("CompanyName", accountName);

		emailSender.sendNow(emailBuilder.build());
	}

	/**
	 * Wrapper class to provide same properties to the email template that existed with previous
	 * emailHash.
	 */
	public static class EmailHashWrapper {

		private String emailHashCode;
		private SoftDeletedEmployee softDeletedEmployee;

		public EmailHashWrapper(final EmailHash emailHash) {
			this.emailHashCode = emailHash.getHashCode();
			this.softDeletedEmployee = emailHash.getEmployee();
		}

		public String getHash() {
			return emailHashCode;
		}

		public SoftDeletedEmployee getEmployee() {
			return softDeletedEmployee;
		}
	}
}
