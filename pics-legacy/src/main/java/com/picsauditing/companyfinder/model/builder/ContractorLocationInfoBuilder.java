package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ContractorLocationInfo;
import com.picsauditing.model.general.LatLong;

import java.util.List;

public class ContractorLocationInfoBuilder {
    private ContractorLocationInfo contractorLocationInfo = new ContractorLocationInfo();

    public ContractorLocationInfoBuilder id(int id) {
        contractorLocationInfo.setId(id);
        return this;
    }

    public ContractorLocationInfoBuilder name(String name) {
        contractorLocationInfo.setName(name);
        return this;
    }

    public ContractorLocationInfoBuilder address(String fullAddress) {
        contractorLocationInfo.setAddress(fullAddress);
        return this;
    }

    public ContractorLocationInfoBuilder coordinates(LatLong coordinates) {
        contractorLocationInfo.setCoordinates(coordinates);
        return this;
    }

    public ContractorLocationInfoBuilder primaryTrade(String primaryTrade) {
        contractorLocationInfo.setPrimaryTrade(primaryTrade);
        return this;
    }

    public ContractorLocationInfoBuilder trades(List<String> trades) {
        contractorLocationInfo.setTrades(trades);
        return this;
    }

    public ContractorLocationInfoBuilder link(String link) {
        contractorLocationInfo.setLink(link);
        return this;
    }

    public ContractorLocationInfo build() {
        return contractorLocationInfo;
    }
}
