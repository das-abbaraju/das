package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;

import java.util.ArrayList;

public class ContractorAccountBuilder {
    private ContractorAccount contractor = new ContractorAccount();

    public ContractorAccount build() {
        return contractor;
    }

    public ContractorAccountBuilder audit(ContractorAudit audit) {
        contractor.getAudits().add(audit);
        audit.setContractorAccount(contractor);
        return this;
    }

    public ContractorAccountBuilder operator(OperatorAccount operator) {
        ContractorOperator joinTable = new ContractorOperator();
        joinTable.setOperatorAccount(operator);
        joinTable.setContractorAccount(contractor);
        contractor.getOperators().add(joinTable);

        return this;
    }

    public ContractorAccountBuilder id(int i) {
        contractor.setId(i);
        return this;
    }

    public ContractorAccountBuilder insuranceCriteriaOperator(FlagCriteria flagCriteria, OperatorAccount operatorAccount, int limit) {
        if (contractor.getInsuranceCriteriaContractorOperators() == null) {
            contractor.setInsuranceCriteriaContractorOperators(new ArrayList<InsuranceCriteriaContractorOperator>());
        }
        InsuranceCriteriaContractorOperator criteria = new InsuranceCriteriaContractorOperator();
        criteria.setFlagCriteria(flagCriteria);
        criteria.setOperatorAccount(operatorAccount);
        criteria.setInsuranceLimit(limit);
        contractor.getInsuranceCriteriaContractorOperators().add(criteria);
        return this;
    }
}
