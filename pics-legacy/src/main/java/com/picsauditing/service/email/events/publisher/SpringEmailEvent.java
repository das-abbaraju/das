package com.picsauditing.service.email.events.publisher;

import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.User;
import org.springframework.context.ApplicationEvent;

public class SpringEmailEvent extends ApplicationEvent implements EmailEvent {

    public final User user;
    public final int template;
    public final EmailStatus status;
    public final String reason;

    public SpringEmailEvent(
            User u,
            int t,
            EmailStatus s,
            String r
    ) {
        super(u);
        this.user = u;
        this.template = t;
        this.status = s;
        this.reason = r;
    }

    public User getUser() {
        return user;
    }

    public int getTemplate() {
        return template;
    }

    public EmailStatus getEmailStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
