package com.picsauditing.mail;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.service.mail.MailCronService;
import com.picsauditing.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class MailCron extends ActionSupport {

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
        } catch (ValidationException e) {
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
