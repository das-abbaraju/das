package com.picsauditing.companyfinder.controller;

import com.google.gson.Gson;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.contractors.ContractorDashboard;
import com.picsauditing.auditbuilder.util.Strings;
import com.picsauditing.companyfinder.model.*;
import com.picsauditing.companyfinder.model.TriStateFlag;
import com.picsauditing.companyfinder.model.builder.CompanyFinderFilterBuilder;
import com.picsauditing.companyfinder.service.CompanyFinderService;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.model.general.LatLong;
import com.picsauditing.util.TriState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    private Integer soleProprietor;
    private int safetySensitive;

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
        List<Integer> tradeIds = parseTradeIds(getTradeIds());

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
                .soleProprietor(TriStateFlag.fromInteger(getSoleProprietor()))
                .safetySensitive(TriStateFlag.fromInteger(getSafetySensitive()))
                .build();

        HashMap<String, String> contractorInfoProperties = buildContractorInfoProperties();

        List<ContractorLocationInfo> contractorLocationInfos = companyFinderService.findContractorLocationInfos(filter, contractorInfoProperties);

        jsonString = new Gson().toJson(contractorLocationInfos);

        return JSON_STRING;
    }

    private List<Integer> parseTradeIds(String strTradeIds) {
        if (Strings.isEmpty(strTradeIds)) return null;

        List<Integer> tradeIds = new ArrayList<>();
        String[] tradeIdList = strTradeIds.split(",");
        for (String tId : tradeIdList) {
            try {
                Integer ti = Integer.valueOf(tId);
                tradeIds.add(ti);
            } catch (Exception e) {
                logger.error("Invalid tradeIds, expected integer values.", e);
            }
        }
        return tradeIds;
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

    public Integer getSoleProprietor() {
        return soleProprietor;
    }

    public void setSoleProprietor(Integer soleProprietor) {
        this.soleProprietor = soleProprietor;
    }

    public int getSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(int safetySensitive) {
        this.safetySensitive = safetySensitive;
    }
}
