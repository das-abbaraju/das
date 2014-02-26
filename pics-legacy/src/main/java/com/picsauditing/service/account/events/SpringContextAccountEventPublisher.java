package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorAccount;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class SpringContextAccountEventPublisher implements ApplicationEventPublisherAware, ContractorEventPublisher {

    private ApplicationEventPublisher context;

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) { this.context = applicationEventPublisher; }

    public void publishEvent(ContractorAccount account, ContractorEventType type) {
        context.publishEvent(new SpringContractorEvent(account, type));
    }
}
