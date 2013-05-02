package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

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
}
