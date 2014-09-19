package com.picsauditing.companyfinder.service;

import com.picsauditing.PICS.FlagCalculatorFactory;
import com.picsauditing.companyfinder.dao.ContractorLocationDAO;
import com.picsauditing.companyfinder.model.ContractorLocationSummary;
import com.picsauditing.companyfinder.model.CompanyFinderFilter;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.companyfinder.model.ContractorLocationInfo;
import com.picsauditing.companyfinder.model.MapInfo;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.companyfinder.model.ViewportLocation;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.flagcalculator.FlagCalculator;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.model.general.LatLong;
import com.picsauditing.service.account.AddressService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CompanyFinderService {
    private static final int INFORMATION_DISPLAY_THRESHOLD = 10;
    public static final String OP_ID = "opId";
    public static final String LINKURL = "linkurl";
    public static final String LINK_URL = LINKURL;
    @Autowired
    private ContractorLocationDAO contractorLocationDAO;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Autowired
    private FlagCalculatorFactory flagCalculatorFactory;

    private final Logger logger = LoggerFactory.getLogger(CompanyFinderService.class);

    public ViewportLocation buildViewportLocationFromAddressUnsecure(String address) {
        ViewportLocation viewportLocation = null;

        MapInfo mapInfo = new Geocode().mapInfoFromAddressUnsecure(address);
        if (mapInfo != null) {
            viewportLocation = ViewportLocation.builder()
                    .address(address)
                    .coordinates(mapInfo.getCenter())
                    .viewPort(mapInfo.getViewPort())
                    .build();
        }
        return viewportLocation;
    }

    public List<ContractorLocationInfo> findContractorLocationInfos(CompanyFinderFilter companyFinderFilter, HashMap<String, String> contractorInfoProps) {

        OperatorAccount operator = null;

        List<ContractorLocation> contractorLocations = contractorLocationDAO.findContractorLocations(companyFinderFilter);

        String operatorIdStr = contractorInfoProps.get(OP_ID);

        if(shouldComputeOperatorData(contractorLocations, operatorIdStr)) {
            operator = operatorAccountDAO.find(Integer.parseInt(operatorIdStr));
        }

        return buildContractorLocationsInfos(contractorLocations, contractorInfoProps, operator);
    }

    public List<ContractorLocationInfo> findContractorLocationInfoSummaries(CompanyFinderFilter companyFinderFilter) {
        List<ContractorLocationInfo> contractorLocationInfoSummaries = new ArrayList<>();
        if (isViewPortEmpty(companyFinderFilter.getViewPort())) {
            return contractorLocationInfoSummaries;
        }

        List<ContractorLocationSummary> contractorLocationSummaries = contractorLocationDAO.findContractorLocationsSummary(companyFinderFilter);

        return buildContractorLocationsInfos(contractorLocationSummaries);
    }

    private boolean isViewPortEmpty(ViewPort viewPort) {
        return viewPort == null || viewPort.isEmpty();
    }

    private boolean shouldComputeOperatorData(List<ContractorLocation> contractorLocations, String operatorIdStr) {
        return contractorLocations.size() <= INFORMATION_DISPLAY_THRESHOLD && StringUtils.isNotEmpty(operatorIdStr);
    }

    private List<ContractorLocationInfo> buildContractorLocationsInfos(List<ContractorLocation> contractorLocations, HashMap<String, String> contractorLocationProps, OperatorAccount operator) {
        if (CollectionUtils.isEmpty(contractorLocations)) {
            return Collections.EMPTY_LIST;
        }
        List<ContractorLocationInfo> contractorLocationInfos = new ArrayList<>();
        for (ContractorLocation contractorLocation : contractorLocations) {
            ContractorLocationInfo contractorLocationInfo = buildContractorLocationInfo(contractorLocation, contractorLocationProps, operator);
            contractorLocationInfos.add(contractorLocationInfo);
        }
        return contractorLocationInfos;
    }

    private List<ContractorLocationInfo> buildContractorLocationsInfos(List<ContractorLocationSummary> contractorLocationSummaries) {
        if (CollectionUtils.isEmpty(contractorLocationSummaries)) {
            return Collections.EMPTY_LIST;
        }
        List<ContractorLocationInfo> contractorLocationInfos = new ArrayList<>();
        for (ContractorLocationSummary contractorLocationSummary : contractorLocationSummaries) {
            ContractorLocationInfo contractorLocationInfo = buildContractorLocationInfo(contractorLocationSummary);
            contractorLocationInfos.add(contractorLocationInfo);
        }
        return contractorLocationInfos;
    }

    private ContractorLocationInfo buildContractorLocationInfo(ContractorLocation contractorLocation, HashMap<String, String> contractorLocationProps, OperatorAccount operator) {
        ContractorAccount contractor = contractorLocation.getContractor();
        String primaryTradeName = getPrimaryTradeName(contractor);
        List<String> tradeNames = getTradeNames(contractor);
        boolean isWorksForOperator = isWorksForOperator(contractor, operator);

        FlagColor flagColor = calculateFlagColor(contractor, operator, isWorksForOperator);

        ContractorLocationInfo contractorLocationInfo = ContractorLocationInfo.builder()
                .id(contractor.getId())
                .name(contractor.getName())
                .address(contractor.getFullAddress())
                .formattedAddressBlock(addressService.formatAddressAsBlock(contractor))
                .coordinates(
                        LatLong.builder()
                                .lat(contractorLocation.getLatitude())
                                .lng(contractorLocation.getLongitude())
                                .build()
                )
                .primaryTrade(primaryTradeName)
                .trades(tradeNames)
                .link(contractorLocationProps.get(LINK_URL) + "?id=" + contractor.getId())
                .worksForOperator(isWorksForOperator)
                .flagColor(flagColor)
                .build();
        return contractorLocationInfo;
    }

    private ContractorLocationInfo buildContractorLocationInfo(ContractorLocationSummary contractorLocationSummary) {
        ContractorLocationInfo contractorLocationInfo = ContractorLocationInfo.builder()
                .id(contractorLocationSummary.getConId())
                .coordinates(
                        LatLong.builder()
                                .lat(contractorLocationSummary.getLatitude())
                                .lng(contractorLocationSummary.getLongitude())
                                .build()
                )
                .build();
        return contractorLocationInfo;
    }

    private boolean isWorksForOperator(ContractorAccount contractor, OperatorAccount operator) {
        if(operator != null) {
            return contractor.isWorksForOperator(operator.getId());
        }
        return false;
    }

    private String getPrimaryTradeName(ContractorAccount contractor) {
        return contractor.getTopTrade() == null ? null : contractor.getTopTrade().getTrade().getName();
    }

    private List<String> getTradeNames(ContractorAccount contractor) {
        List<String> tradeNames = new ArrayList<>();
        for (ContractorTrade contractorTrade : contractor.getTradesSorted()) {
            tradeNames.add(contractorTrade.getTrade().getName());
        }
        return tradeNames;
    }

    private FlagColor calculateFlagColor(ContractorAccount contractor, OperatorAccount operator, boolean isWorksForOperator) {
        if(operator == null) {
           return null;
        }

        ContractorOperator contractorOperator = buildContractorOperator(contractor, operator);

        FlagColor flagColor = null;
        try {
            FlagCalculator calculator = flagCalculatorFactory.flagCalculator(contractorOperator, null);
            calculator.setWorksForOperator(isWorksForOperator);
            flagColor = getWorstColor(calculator.calculate());
        } catch (Exception e) {
            logger.warn("Unable to calculate flag color", e);
        }

        return flagColor;
    }

    private ContractorOperator buildContractorOperator(ContractorAccount contractor, OperatorAccount operator) {
        ContractorOperator contractorOperator = new ContractorOperator();
        contractorOperator.setContractorAccount(contractor);
        contractorOperator.setOperatorAccount(operator);
        return contractorOperator;
    }

    /**
     * We may want to consider moving this into FlagDataCalculator
     *
     * @param flagData
     * @return
     */
    private FlagColor getWorstColor(List<com.picsauditing.flagcalculator.FlagData> flagData) {
        if (flagData == null)
            return null;
        com.picsauditing.flagcalculator.entities.FlagColor worst = com.picsauditing.flagcalculator.entities.FlagColor.Green;
        for (com.picsauditing.flagcalculator.FlagData flagDatum : flagData) {
            com.picsauditing.flagcalculator.entities.FlagData data = (com.picsauditing.flagcalculator.entities.FlagData)flagDatum;
            if (data.getFlag() == com.picsauditing.flagcalculator.entities.FlagColor.Red)
                return FlagColor.valueOf(data.getFlag().toString());
            if (data.getFlag() == com.picsauditing.flagcalculator.entities.FlagColor.Amber)
                worst = data.getFlag();
        }

        return FlagColor.valueOf(worst.toString());
    }

}
