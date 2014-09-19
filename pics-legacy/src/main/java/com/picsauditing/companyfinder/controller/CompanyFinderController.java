package com.picsauditing.companyfinder.controller;

import com.google.gson.Gson;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.ContractorDashboard;
import com.picsauditing.companyfinder.model.*;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.companyfinder.service.CompanyFinderService;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.model.general.LatLong;
import com.picsauditing.util.Strings;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private String tradeIds;
    private int soleOwner = TriStateFlag.IGNORE.getValue();
    private int safetySensitive = TriStateFlag.IGNORE.getValue();
    private boolean summary;
    private String ids;

    private final Logger logger = LoggerFactory.getLogger(CompanyFinderService.class);

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
        CompanyFinderFilter filter = getCompanyFinderFilter();
        List<ContractorLocationInfo> contractorLocationInfos;

        if (getSummary()) {
            contractorLocationInfos = companyFinderService.findContractorLocationInfoSummaries(filter);
        } else {
            HashMap<String, String> contractorInfoProperties = buildContractorInfoProperties();
            contractorLocationInfos = companyFinderService.findContractorLocationInfos(filter, contractorInfoProperties);
        }
        jsonString = new Gson().toJson(contractorLocationInfos);
        return JSON_STRING;
    }

    private CompanyFinderFilter getCompanyFinderFilter() {
        List<Integer> tradeIds = parseCommaDelimitedIds(getTradeIds());
        List<Integer> contractorIds = parseCommaDelimitedIds(getIds());
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
                .tradeIds(tradeIds)
                .soleProprietor(TriStateFlag.fromInteger(getSoleOwner()))
                .safetySensitive(TriStateFlag.fromInteger(getSafetySensitive()))
                .contractorIds(contractorIds)
                .build();

        return filter;
    }

    private List<Integer> parseCommaDelimitedIds(String commaDelimitedIds) {
        return Strings.explodeCommaDelimitedStringOfIds(commaDelimitedIds);
    }

    private HashMap<String, String> buildContractorInfoProperties() {
        HashMap<String, String> contractorInfoProperties = new HashMap<>();
        contractorInfoProperties.put(CompanyFinderService.LINK_URL, ContractorDashboard.URL);
        if (permissions.isOperator()) {
            contractorInfoProperties.put(CompanyFinderService.OP_ID, String.valueOf(permissions.getAccountId()));
        }
        return contractorInfoProperties;
    }

    private String getSearchAddress() {
        return StringUtils.isEmpty(addressQuery) ? getLoggedInUserAddress() : addressQuery;
    }

    private String getLoggedInUserAddress() {
        Account userAccount = user.getAccount();
        if (StringUtils.isEmpty(userAccount.getFullAddress())) {
            return DEFAULT_ADDRESS;
        }

        return userAccount.getFullAddress();
    }

    public boolean isCompanyFinderFeatureEnabled() {
        return Features.COMPANY_FINDER.isActive();
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

    public String getTradeIds() {
        return tradeIds;
    }

    public void setTradeIds(String tradeIds) {
        this.tradeIds = tradeIds;
    }

    public int getSoleOwner() {
        return soleOwner;
    }

    public void setSoleOwner(int soleOwner) {
        this.soleOwner = soleOwner;
    }

    public int getSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(int safetySensitive) {
        this.safetySensitive = safetySensitive;
    }

    public boolean getSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
