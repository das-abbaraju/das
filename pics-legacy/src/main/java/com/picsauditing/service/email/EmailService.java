package com.picsauditing.service.email;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.email.events.publisher.EmailEventPublisher;

public class EmailService {

    private final EmailEventPublisher eventPublisher;

    public EmailService(
            EmailEventPublisher publisher
    ) {
        this.eventPublisher = publisher;
    }

    public void publishEvent(User user, EmailTemplate template, EmailStatus status, String reason) {
        eventPublisher.publish(user, template, status, reason);
    }
}
