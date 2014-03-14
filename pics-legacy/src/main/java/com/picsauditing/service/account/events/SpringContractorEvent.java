package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorAccount;
import org.springframework.context.ApplicationEvent;

public class SpringContractorEvent extends ApplicationEvent implements ContractorEvent {

    public final ContractorAccount account;
    public final ContractorEventType event;

    public SpringContractorEvent(ContractorAccount account, ContractorEventType event) {
        super(account);
        this.account = account;
        this.event = event;
    }

    public ContractorEventType getEvent() { return event; }

    public ContractorAccount getContractor() { return account; }

}
