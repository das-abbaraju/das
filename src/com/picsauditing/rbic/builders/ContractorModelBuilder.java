package com.picsauditing.rbic.builders;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.rbic.ContractorModel;

public class ContractorModelBuilder {

    private ContractorModel model = new ContractorModel();

    public ContractorModelBuilder contractor(ContractorAccount contractor) {
        model.setContractor(contractor);
        return this;
    }

    public ContractorModel build() {
        return  model;
    }
}
