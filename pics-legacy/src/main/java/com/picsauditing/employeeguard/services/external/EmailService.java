package com.picsauditing.employeeguard.services.external;

import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailService {

	public static final int EG_WELCOME_EMAIL = 357;

	@Autowired
	private EmailSender emailSender;

	public void sendEGWelcomeEmail(final EmailHash hash, final String accountName) throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();

		emailBuilder.setToAddresses(hash.getEmailAddress());
		emailBuilder.setFromAddress("PICS <info@picsauditing.com>");
		emailBuilder.setTemplate(EG_WELCOME_EMAIL);
		emailBuilder.addToken("hash", hash);
		emailBuilder.addToken("CompanyName", accountName);

		emailSender.sendNow(emailBuilder.build());
	}
}
