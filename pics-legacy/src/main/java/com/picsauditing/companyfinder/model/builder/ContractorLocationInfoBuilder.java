package com.picsauditing.companyfinder.model.builder;

import com.picsauditing.companyfinder.model.ContractorLocationInfo;
import com.picsauditing.model.general.LatLong;

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

    public ContractorLocationInfoBuilder cooridinates(LatLong coordinates) {
        contractorLocationInfo.setCoordinates(coordinates);
        return this;
    }

    public ContractorLocationInfoBuilder trade(String trade) {
        contractorLocationInfo.setTrade(trade);
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
