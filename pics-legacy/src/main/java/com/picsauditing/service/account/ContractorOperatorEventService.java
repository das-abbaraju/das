package com.picsauditing.service.account;

import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.service.account.events.*;

public class ContractorOperatorEventService {
    private final ContractorOperatorEventPublisher eventPublisher;

    public ContractorOperatorEventService(ContractorOperatorEventPublisher publisher) {
        this.eventPublisher = publisher;
    }

    public void publishEvent(ContractorOperator contractorOperator, ContractorOperatorEventType type, Integer generatingEventUserID) {
        eventPublisher.publishEvent(contractorOperator, type, generatingEventUserID);
    }
}
