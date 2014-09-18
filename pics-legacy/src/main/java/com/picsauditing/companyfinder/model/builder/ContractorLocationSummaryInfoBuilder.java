package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.dao.ContractorLocationSummaryInfo;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorLocationSummaryInfoBuilder {
    private ContractorLocationSummaryInfo contractorLocationSummaryInfo = new ContractorLocationSummaryInfo();

    public ContractorLocationSummaryInfoBuilder id(int id) {
        contractorLocationSummaryInfo.setId(id);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder conId(int conId) {
        contractorLocationSummaryInfo.setConId(conId);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder lat(Double lat) {
        contractorLocationSummaryInfo.setLatitude(lat);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder lng(Double lng) {
        contractorLocationSummaryInfo.setLongitude(lng);
        return this;
    }

    public ContractorLocationSummaryInfo build() {
        return contractorLocationSummaryInfo;
    }
}
