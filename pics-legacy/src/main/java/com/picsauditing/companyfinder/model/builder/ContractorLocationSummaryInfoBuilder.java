package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorLocationSummaryInfoBuilder {
    private ContractorLocation contractorLocation = new ContractorLocation();

    public ContractorLocationSummaryInfoBuilder contractor(ContractorAccount contractorAccount) {
        contractorLocation.setContractor(contractorAccount);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder lat(Double lat) {
        contractorLocation.setLatitude(lat);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder lng(Double lng) {
        contractorLocation.setLongitude(lng);
        return this;
    }

    public ContractorLocation build() {
        return contractorLocation;
    }
}
