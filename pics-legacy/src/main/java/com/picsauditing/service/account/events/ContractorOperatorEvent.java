package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorOperator;

public interface ContractorOperatorEvent {
    ContractorOperatorEventType getEvent();
    ContractorOperator getContractorOperator();
}
