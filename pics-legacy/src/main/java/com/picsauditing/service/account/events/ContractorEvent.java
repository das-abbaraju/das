package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorAccount;

public interface ContractorEvent {
    public ContractorEventType getEvent();
    public ContractorAccount getContractor();

}
