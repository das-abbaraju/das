package com.picsauditing.service.user.events.publisher;

import com.picsauditing.jpa.entities.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class SpringContextUserEventPublisher implements ApplicationEventPublisherAware, UserEventPublisher {
    private ApplicationEventPublisher publisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

    public void publish(User user, UserEventType userEventType) {
        publisher.publishEvent(new SpringUserEvent(user, userEventType));
    }
}
