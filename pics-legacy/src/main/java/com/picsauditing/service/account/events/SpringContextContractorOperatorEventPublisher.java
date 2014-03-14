package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorOperator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class SpringContextContractorOperatorEventPublisher implements ApplicationEventPublisherAware, ContractorOperatorEventPublisher {
    private ApplicationEventPublisher context;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.context = applicationEventPublisher;
    }

    @Override
    public void publishEvent(ContractorOperator contractorOperator, ContractorOperatorEventType type, Integer generatingEventUserID) {
        context.publishEvent(new SpringContractorOperatorEvent(contractorOperator, type, generatingEventUserID));
    }
}
