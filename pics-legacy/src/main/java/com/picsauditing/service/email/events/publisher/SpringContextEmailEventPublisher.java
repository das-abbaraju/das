package com.picsauditing.service.email.events.publisher;

import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class SpringContextEmailEventPublisher implements ApplicationEventPublisherAware, EmailEventPublisher {
    private ApplicationEventPublisher publisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

    public void publish(User user, EmailTemplate template, EmailStatus status, String reason) {
        publisher.publishEvent(new SpringEmailEvent(user, template, status, reason));
    }
}
