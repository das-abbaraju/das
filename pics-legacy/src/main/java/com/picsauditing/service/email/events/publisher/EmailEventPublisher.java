package com.picsauditing.service.email.events.publisher;

import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;

public interface EmailEventPublisher {
    void publish(User user, int template, EmailStatus status, String reason);
}
