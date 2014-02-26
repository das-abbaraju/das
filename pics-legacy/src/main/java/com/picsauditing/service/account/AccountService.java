package com.picsauditing.service.account;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.account.events.ContractorEventPublisher;
import com.picsauditing.service.account.events.ContractorEventType;
import com.picsauditing.service.account.events.SpringContractorEvent;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountService {

    private final ContractorEventPublisher eventPublisher;

    public AccountService(
            ContractorEventPublisher publisher
    ) {
        this.eventPublisher = publisher;
    }

    public void publishEvent(ContractorAccount account, ContractorEventType type) {
        eventPublisher.publishEvent(account, type);
    }

}
