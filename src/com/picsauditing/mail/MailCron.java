package com.picsauditing.mail;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.service.mail.MailCronService;
import com.picsauditing.validator.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Run the email task every minute Send 5 emails at a time This translates into 300/hour or 7200/day If this seems slow,
 * it is because of strict limits by gmail
 *
 * @author Trevor
 *
 */
@SuppressWarnings("serial")
public class MailCron extends PicsActionSupport {

	@Autowired
	private MailCronService mailCronService;
	private int subscriptionID = 0;

	private final Logger logger = LoggerFactory.getLogger(MailCron.class);
	
	@Anonymous
	public String execute() {

		try {
			mailCronService.processEmailSubscription(subscriptionID, getPermissions());
		} catch (ValidationException e) {
			addActionError(e.getMessage());
			// todo: Consider not returning and instead, continue with processPendingEmails()
			return ACTION_MESSAGES;
		}

		String statusMessage = mailCronService.processPendingEmails(permissions);
		addActionMessage(statusMessage);
		return ACTION_MESSAGES;
	}

	@Anonymous
	public String listAjax() {
		output = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();
		return PLAIN_TEXT;
	}

	public void setSubscriptionID(int subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public int getSubscriptionID() {
		return subscriptionID;
	}

}
