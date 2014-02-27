package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorOperator;

public interface ContractorOperatorEventPublisher {
    void publishEvent(ContractorOperator contractorOperator, ContractorOperatorEventType type);
}
