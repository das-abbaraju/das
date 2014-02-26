package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorAccount;

public interface ContractorEventPublisher {
    void publishEvent(ContractorAccount account, ContractorEventType type);
}
