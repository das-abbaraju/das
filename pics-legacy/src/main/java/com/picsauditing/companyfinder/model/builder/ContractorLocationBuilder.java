package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorLocationBuilder {
    private ContractorLocation contractorLocation = new ContractorLocation();

    public ContractorLocationBuilder contractor(ContractorAccount contractorAccount) {
        contractorLocation.setContractor(contractorAccount);
        return this;
    }

    public ContractorLocationBuilder lat(Double lat) {
        contractorLocation.setLatitude(lat);
        return this;
    }

    public ContractorLocationBuilder lng(Double lng) {
        contractorLocation.setLongitude(lng);
        return this;
    }

    public ContractorLocation build() {
        return contractorLocation;
    }
}
