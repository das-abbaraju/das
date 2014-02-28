package com.picsauditing.service.user.events.publisher;

import com.picsauditing.jpa.entities.User;

public interface UserEvent {
    public User getUser();
    public UserEventType getUserEventType();
}
