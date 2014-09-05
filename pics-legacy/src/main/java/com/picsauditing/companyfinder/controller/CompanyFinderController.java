package com.picsauditing.companyfinder.controller;

import com.google.gson.Gson;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.ContractorDashboard;
import com.picsauditing.companyfinder.model.*;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.companyfinder.service.CompanyFinderService;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

public class CompanyFinderController extends PicsActionSupport {

    public static final String DEFAULT_ADDRESS = "17701 Cowan Street, Irvine, CA 92614";
    @Autowired
    private CompanyFinderService companyFinderService;

    private String addressQuery;
    private double neLat;
    private double neLong;
    private double swLat;
    private double swLong;
    private Trade trade;
    private CompanyFinderFilter filter;


    public String findLocation() {
        String address = getSearchAddress();
        ViewportLocation viewportLocation = companyFinderService.buildViewportLocationFromAddressUnsecure(address);

        if (viewportLocation != null) {
            jsonString = new Gson().toJson(viewportLocation);
        } else {
            jsonString = "{}";
            ServletActionContext.getResponse().setStatus(HttpStatus.SC_OK);
        }

        return JSON_STRING;
    }

    public String findContractorLocationInfos() {
        CompanyFinderFilter filter = new CompanyFinderFilterBuilder()
                .viewPort(
                        ViewPort.builder()
                        .northEast(LatLong.builder()
                                .lat(neLat)
                                .lng(neLong)
                                .build())
                        .southWest(LatLong.builder()
                                .lat(swLat)
                                .lng(swLong)
                                .build())
                        .build())
                .trade(trade)
                .safetySensitive(SafetySensitive.IGNORE)
                .build();

        HashMap<String, String> contractorInfoProperties = buildContractorInfoProperties();

        List<ContractorLocationInfo> contractorLocationInfos = companyFinderService.findContractorLocationInfos(filter, contractorInfoProperties);

        jsonString = new Gson().toJson(contractorLocationInfos);

        return JSON_STRING;
    }

    private HashMap<String, String> buildContractorInfoProperties() {
        HashMap<String, String> contractorInfoProperties = new HashMap<>();
        contractorInfoProperties.put("linkurl", ContractorDashboard.URL);
        if(permissions.isOperator()) {
            contractorInfoProperties.put("opId", String.valueOf(permissions.getAccountId()));
        }
        return contractorInfoProperties;
    }

    private String getSearchAddress() {
        return StringUtils.isEmpty(addressQuery) ? getLoggedInUserAddress() : addressQuery;
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
        return filter.getTrade();
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

    public int getSafetySensitive() {
        return filter.getSafetySensitive().getSafetySensitiveValue();
    }

    public void setSafetySensitive(int safetySensitive) {
        this.filter.setSafetySensitive(SafetySensitive.fromInteger(safetySensitive));
    }
}
