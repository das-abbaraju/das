package com.picsauditing.service.user.events.publisher;

import com.picsauditing.jpa.entities.User;
import org.springframework.context.ApplicationEvent;

public class SpringUserEvent extends ApplicationEvent implements UserEvent {
    public final User user;
    public final UserEventType userEventType;

    public SpringUserEvent(User u, UserEventType uet) {
        super(u);
        this.userEventType = uet;
        this.user = u;
    }

    public User getUser() {
        return user;
    }

    public UserEventType getUserEventType() {
        return userEventType;
    }
}
