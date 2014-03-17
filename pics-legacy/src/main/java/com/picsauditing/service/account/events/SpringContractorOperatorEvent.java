package com.picsauditing.service.account.events;

import com.picsauditing.jpa.entities.ContractorOperator;
import org.springframework.context.ApplicationEvent;

public class SpringContractorOperatorEvent extends ApplicationEvent implements ContractorOperatorEvent {

    private final ContractorOperator contractorOperator;
    private final ContractorOperatorEventType event;
    private final Integer generatingEventUserID;

    public SpringContractorOperatorEvent(ContractorOperator contractorOperator, ContractorOperatorEventType event, Integer generatingEventUserID) {
        super(contractorOperator);
        this.contractorOperator = contractorOperator;
        this.event = event;
        this.generatingEventUserID = generatingEventUserID;
    }

    @Override
    public ContractorOperatorEventType getEvent() {
        return event;
    }

    @Override
    public ContractorOperator getContractorOperator() {
        return contractorOperator;
    }

    @Override
    public Integer getGeneratingEventUserID() {
        return generatingEventUserID;
    }
}
