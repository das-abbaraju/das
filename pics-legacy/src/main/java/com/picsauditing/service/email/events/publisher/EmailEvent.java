package com.picsauditing.service.email.events.publisher;

import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.User;

public interface EmailEvent {
    public User getUser();
    public int getTemplate();
    public EmailStatus getEmailStatus();
    public String getReason();
}
