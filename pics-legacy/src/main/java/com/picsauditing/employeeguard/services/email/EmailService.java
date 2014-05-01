package com.picsauditing.employeeguard.services.email;

import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private EmailSender emailSender;
	@Autowired
	private AppPropertyService appPropertyService;

	public void sendEGWelcomeEmail(final EmailHash emailHash, final String accountName) throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();

		emailBuilder.setToAddresses(emailHash.getEmailAddress());
		emailBuilder.setFromAddress(loadEmailAddress(AppProperty.EMAIL_FROM_INFO_AT_PICSAUDITING,
				"PICS <info@picsauditing.com>"));
		emailBuilder.setTemplate(loadEmailTemplateId(AppProperty.EG_WELCOME_EMAIL_TEMPLATE_ID, 357));
		emailBuilder.addToken("hash", new EmailHashWrapper(emailHash));
		emailBuilder.addToken("CompanyName", accountName);

		emailSender.sendNow(emailBuilder.build());
	}

	public boolean sendEGFeedBackEmail(final String feedback, final String accountName, final int appUserId,
									   final String userEmailAddress) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();

			emailBuilder.setToAddresses(loadEmailAddress(AppProperty.EMAIL_TO_EG_FEEDBACK, "mdesio@picsauditing.com"));
			emailBuilder.setFromAddress(getFromAddressIfUserIsMissingEmail(userEmailAddress));
			emailBuilder.setTemplate(loadEmailTemplateId(AppProperty.EG_BETA_FEEDBACK_EMAIL_TEMPLATE_ID, 385));
			emailBuilder.addToken("feedback", feedback);
			emailBuilder.addToken("accountName", accountName);
			emailBuilder.addToken("appUserId", appUserId);
			emailBuilder.addToken("userEmailAddress", userEmailAddress);

			emailSender.sendNow(emailBuilder.build());

			return true;
		} catch (Exception e) {
			logger.error("Error Sending feedback email", e.getMessage());
			return false;
		}
	}

	private String getFromAddressIfUserIsMissingEmail(final String userEmailAddress) {
		if (Strings.isEmpty(userEmailAddress)) {
			return loadEmailAddress(AppProperty.EMAIL_FROM_INFO_AT_PICSAUDITING, "PICS <info@picsauditing.com>");
		}

		return userEmailAddress;
	}

	private int loadEmailTemplateId(final String templatePropertyName, final int defaultTemplateId) {
		return appPropertyService.getPropertyInt(templatePropertyName, defaultTemplateId);
	}

	private String loadEmailAddress(final String templateAddressPropertyName, final String defaultValue) {
		return appPropertyService.getPropertyString(templateAddressPropertyName, defaultValue);
	}
}
