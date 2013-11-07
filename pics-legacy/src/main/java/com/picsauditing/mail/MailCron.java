package com.picsauditing.mail;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.service.mail.MailCronService;
import com.picsauditing.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class MailCron extends PicsActionSupport {

    @Autowired
    MailCronService mailCronService;
    private int subscriptionID = 0;

    @Anonymous
    public String execute() {
        // TODO Don't call this anymore to send mail or create email subscriptions, call subscription() and send() directly
        subscription();
        send();
        return ACTION_MESSAGES;
    }

    @Anonymous
    public String subscription() {
        try {
            mailCronService.processEmailSubscription(subscriptionID, getPermissions());
        } catch (ValidationException e) {
            addActionError(e.getMessage());
        }
        return ACTION_MESSAGES;
    }

    @Anonymous
    public String send() {
        String statusMessage = mailCronService.processPendingEmails(permissions);
        addActionMessage(statusMessage);
        return ACTION_MESSAGES;
    }

    @Anonymous
    public String listAjax() {
        output = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();
        return PLAIN_TEXT;
    }

    public int getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

}
