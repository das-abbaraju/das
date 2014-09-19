package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ContractorLocationSummary;

public class ContractorLocationSummaryInfoBuilder {
    private ContractorLocationSummary contractorLocationSummary = new ContractorLocationSummary();

    public ContractorLocationSummaryInfoBuilder id(int id) {
        contractorLocationSummary.setId(id);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder conId(int conId) {
        contractorLocationSummary.setConId(conId);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder lat(Double lat) {
        contractorLocationSummary.setLatitude(lat);
        return this;
    }

    public ContractorLocationSummaryInfoBuilder lng(Double lng) {
        contractorLocationSummary.setLongitude(lng);
        return this;
    }

    public ContractorLocationSummary build() {
        return contractorLocationSummary;
    }
}
