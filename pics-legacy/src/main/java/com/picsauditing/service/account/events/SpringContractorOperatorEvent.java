package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorOperator;
import org.springframework.context.ApplicationEvent;

public class SpringContractorOperatorEvent extends ApplicationEvent implements ContractorOperatorEvent {

    private final ContractorOperator contractorOperator;
    private final ContractorOperatorEventType event;

    public SpringContractorOperatorEvent(ContractorOperator contractorOperator, ContractorOperatorEventType event) {
        super(contractorOperator);
        this.contractorOperator = contractorOperator;
        this.event = event;
    }

    @Override
    public ContractorOperatorEventType getEvent() {
        return null;
    }

    @Override
    public ContractorOperator getContractorOperator() {
        return null;
    }
}
