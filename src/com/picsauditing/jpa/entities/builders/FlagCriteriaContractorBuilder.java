package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;

public class FlagCriteriaContractorBuilder {
    private FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor();

    public FlagCriteriaContractorBuilder contractor(ContractorAccount contractor) {
        flagCriteriaContractor.setContractor(contractor);
        return this;
    }

    public FlagCriteriaContractorBuilder criteria(FlagCriteria criteria) {
        flagCriteriaContractor.setCriteria(criteria);
        return this;
    }

    public FlagCriteriaContractor build() {
        return flagCriteriaContractor;
    }
}
