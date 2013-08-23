package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;

import java.util.ArrayList;
import java.util.HashSet;

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

    public ContractorOperatorBuilder flagData(FlagData flagData) {
        if (contractorOperator.getFlagDatas() == null) {
            contractorOperator.setFlagDatas(new HashSet<FlagData>());
        }
        contractorOperator.getFlagDatas().add(flagData);
        return this;
    }
}
