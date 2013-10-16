package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailService {
	public static final int EG_WELCOME_EMAIL = 357;

	@Autowired
	private EmailSender emailSender;
	@Autowired
	private AccountDAO accountDAO;

	public void sendEGWelcomeEmail(EmailHash hash) throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setToAddresses(hash.getEmailAddress());
		emailBuilder.setFromAddress("PICS <info@picsauditing.com>");
		emailBuilder.setTemplate(EG_WELCOME_EMAIL);
		emailBuilder.addToken("hash", hash);
		emailBuilder.addToken("CompanyName", accountDAO.find(hash.getEmployee().getAccountId()).getName());
		emailSender.sendNow(emailBuilder.build());

	}
}
