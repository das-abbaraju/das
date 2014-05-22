package com.picsauditing.struts.controller.companyFinder;

import com.google.gson.Gson;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.ContractorDashboard;
import com.picsauditing.companyfinder.model.*;
import com.picsauditing.companyfinder.service.CompanyFinderService;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyFinderController extends PicsActionSupport {
    public static final String DEFAULT_ADDRESS = "17701 Cowan Street, Irvine, CA 92614";
    public static final String COMPANY_FINDER_URL = "company-finder.action";
    @Autowired
    private CompanyFinderService companyFinderService;

    private String addressQuery;
    private double neLat;
    private double neLong;
    private double swLat;
    private double swLong;
    private Trade trade;


    public String findLocation() {
        String userAddress = getUserAddress();

        MapInfoResponse mapInfoResponse = companyFinderService.mapInfoFromAddressUnsecure(userAddress);

        if (mapInfoResponse != null) {
            LocationResponse locationResponse = LocationResponse.builder()
                    .address(userAddress)
                    .coordinates(mapInfoResponse.getCenter())
                    .viewPort(mapInfoResponse.getViewPort())
                    .build();

            jsonString = new Gson().toJson(locationResponse);
        } else {
            jsonString = "{}";
            ServletActionContext.getResponse().setStatus(HttpStatus.SC_OK);
        }

        return JSON_STRING;
    }

    private String getUserAddress() {
        return StringUtils.isEmpty(addressQuery) ? getLoggedInUserAddress() : addressQuery;
    }

    public String findByViewPort() {
        ViewPort viewPort = ViewPort.builder()
                .northEast(LatLong.builder()
                        .lat(neLat)
                        .lng(neLong)
                        .build())
                .southWest(LatLong.builder()
                        .lat(swLat)
                        .lng(swLong)
                        .build())
                .build();

        List<ContractorLocation> contractorLocations = findContractorLocations(viewPort);
        List<ContractorLocationInfo> contractorLocationInfo = getContractorLocationsInfo(contractorLocations);

        jsonString = new Gson().toJson(contractorLocationInfo);

        return JSON_STRING;
    }

    private List<ContractorLocation> findContractorLocations(ViewPort viewPort) {
        List<ContractorLocation> contractorLocations;
        if (trade == null) {
            contractorLocations = companyFinderService.findByViewPort(viewPort);
        } else {
            contractorLocations = companyFinderService.findByViewPortAndTrade(viewPort, trade);
        }
        return contractorLocations;
    }

    private List<ContractorLocationInfo> getContractorLocationsInfo(List<ContractorLocation> contractorLocations) {
        if (CollectionUtils.isEmpty(contractorLocations)) {
            return Collections.EMPTY_LIST;
        }
        List<ContractorLocationInfo> contractorLocationInfoList = new ArrayList<>();

        for (ContractorLocation contractorLocation : contractorLocations) {
            ContractorAccount contractor = contractorLocation.getContractor();
            String name = contractor.getTopTrade() == null ? null : contractor.getTopTrade().getTrade().getName();
            ContractorLocationInfo contractorLocationInfo = ContractorLocationInfo.builder()
                    .id(contractor.getId())
                    .name(contractor.getName())
                    .address(contractor.getFullAddress())
                    .cooridinates(
                            LatLong.builder()
                                    .lat(contractorLocation.getLatitude())
                                    .lng(contractorLocation.getLongitude())
                                    .build()
                    )
                    .trade(name)
                    .link(ContractorDashboard.URL + "?id=" + contractor.getId())

                    .build();


            contractorLocationInfoList.add(contractorLocationInfo);
        }
        return contractorLocationInfoList;
    }

    private String getLoggedInUserAddress() {
        Account userAccount = user.getAccount();
        if(StringUtils.isEmpty(userAccount.getFullAddress())) {
            return DEFAULT_ADDRESS;
        }

        return userAccount.getFullAddress();
    }

    public boolean isCompanyFinderFeatureEnabled() {
        return Features.COMPANY_FINDER.isActive();
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public String getAddressQuery() {
        return addressQuery;
    }

    public void setAddressQuery(String addressQuery) {
        this.addressQuery = addressQuery;
    }

    public double getNeLat() {
        return neLat;
    }

    public void setNeLat(double neLat) {
        this.neLat = neLat;
    }

    public double getNeLong() {
        return neLong;
    }

    public void setNeLong(double neLong) {
        this.neLong = neLong;
    }

    public double getSwLat() {
        return swLat;
    }

    public void setSwLat(double swLat) {
        this.swLat = swLat;
    }

    public double getSwLong() {
        return swLong;
    }

    public void setSwLong(double swLong) {
        this.swLong = swLong;
    }
}
