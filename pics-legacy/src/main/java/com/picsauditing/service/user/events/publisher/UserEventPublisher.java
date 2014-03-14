package com.picsauditing.service.user.events.publisher;

import com.picsauditing.jpa.entities.User;

public interface UserEventPublisher {
    void publish(User user, UserEventType userEventType);
}
