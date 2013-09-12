package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;

public class EmailQueueBuilder {
    private EmailQueue email = new EmailQueue();

    public EmailQueue build() {
        return email;
    }

    public EmailQueueBuilder toAddress(String toAddress) {
        email.setToAddresses(toAddress);
        return this;
    }

    public EmailQueueBuilder fromAddress(String fromAddress) {
        email.setFromAddress(fromAddress);
        return this;
    }

    public EmailQueueBuilder emailTemplate(EmailTemplate emailTemplate) {
        email.setEmailTemplate(emailTemplate);
        return this;
    }
}
