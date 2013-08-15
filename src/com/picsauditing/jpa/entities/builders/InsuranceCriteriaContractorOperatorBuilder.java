package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.InsuranceCriteriaContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class InsuranceCriteriaContractorOperatorBuilder {
    InsuranceCriteriaContractorOperator insuranceCriteriaContractorOperator = new InsuranceCriteriaContractorOperator();
    public InsuranceCriteriaContractorOperatorBuilder operator(OperatorAccount operator) {
        insuranceCriteriaContractorOperator.setOperatorAccount(operator);
        return this;
    }

    public InsuranceCriteriaContractorOperatorBuilder contractor(ContractorAccount contractor) {
        insuranceCriteriaContractorOperator.setContractorAccount(contractor);
        return this;
    }

    public InsuranceCriteriaContractorOperatorBuilder criteria(FlagCriteria flagCriteria) {
        insuranceCriteriaContractorOperator.setFlagCriteria(flagCriteria);
        return this;
    }

    public InsuranceCriteriaContractorOperatorBuilder limit(int limit) {
        insuranceCriteriaContractorOperator.setInsuranceLimit(limit);
        return this;
    }

    public InsuranceCriteriaContractorOperator build() {
        return insuranceCriteriaContractorOperator;
    }


}
