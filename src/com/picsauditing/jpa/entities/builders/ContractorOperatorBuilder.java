package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ContractorOperatorBuilder{

    private ContractorOperator contractorOperator = new ContractorOperator();

    public ContractorOperatorBuilder contractor(ContractorAccount account) {
        account.getOperators().add(contractorOperator);
        contractorOperator.setContractorAccount(account);
        return this;
    }

    public ContractorOperatorBuilder operator(OperatorAccount operator) {
        contractorOperator.setOperatorAccount(operator);
        operator.getContractorOperators().add(contractorOperator);
        return this;
    }

    public ContractorOperatorBuilder workStatus(ApprovalStatus status) {
        contractorOperator.setWorkStatus(status);
        return this;
    }

    public ContractorOperator build() {
        return contractorOperator;
    }
}
