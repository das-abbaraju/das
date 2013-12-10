package com.picsauditing.mail;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.mail.subscription.SubscriptionValidationException;
import com.picsauditing.service.mail.MailCronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class MailCron extends ActionSupport {

	private static final Logger logger = LoggerFactory.getLogger(MailCron.class);

	@Autowired
    MailCronService mailCronService;
	private String output = null;
    private int subscriptionID = 0;

	@Anonymous
    public String execute() {
        // TODO Don't call this anymore to send mail or create email subscriptions, call subscription() and send() directly
        subscription();
        send();
        return PicsActionSupport.ACTION_MESSAGES;
    }

    @Anonymous
    public String subscription() {
        try {
            mailCronService.processEmailSubscription(subscriptionID);
        } catch (SubscriptionValidationException e) {
	        logger.error(e.getMessage());
            addActionError(e.getMessage());
        }
        return PicsActionSupport.ACTION_MESSAGES;
    }

    @Anonymous
    public String send() {
        String statusMessage = mailCronService.processPendingEmails();
        addActionMessage(statusMessage);
        return PicsActionSupport.ACTION_MESSAGES;
    }

    @Anonymous
    public String listAjax() {
        output = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();
        return PicsActionSupport.PLAIN_TEXT;
    }

    public int getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public String getOutput() {
        return output;
    }

}
