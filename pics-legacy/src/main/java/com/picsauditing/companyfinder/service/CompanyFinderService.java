package com.picsauditing.companyfinder.service;

import com.picsauditing.companyfinder.model.*;
import com.picsauditing.dao.companyfinder.ContractorLocationDAO;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import com.picsauditing.service.account.AddressService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyFinderService {

    public static final int IGNORE_SAFETY_SENSITIVE = -1;
    public static final int FILTER_BY_SAFETY_SENSITIVE = 1;
    @Autowired
    private ContractorLocationDAO contractorLocationDAO;
    @Autowired
    private AddressService addressService;

    public LatLong latLongFromAddressUnsecure(String address) {
        return new Geocode().latLongFromAddressUnsecure(address);
    }

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

/*
    private List<ContractorLocation> findByViewPort(ViewPort viewPort) {
        return contractorLocationDAO.findContractorLocations(
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude());
    }

    private List<ContractorLocation> findByViewPortAndSafety(ViewPort viewPort, boolean safetySensitive) {
        return contractorLocationDAO.findContractorLocations(
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude(),
                safetySensitive);
    }
*/

    public List<ContractorLocation> findContractorLocations(ViewPort viewPort, Trade trade, int safetySensitive) {
        List<ContractorLocation> contractorLocations;

        if (shouldIncludeSafetySensitiveFilter(safetySensitive)) {

            boolean safetySensitiveFlag = filterSafetySensitive(safetySensitive);

            if (trade != null) {
                contractorLocations = contractorLocationDAO.findContractorLocations(
                        viewPort.getNorthEast().getLatitude(),
                        viewPort.getNorthEast().getLongitude(),
                        viewPort.getSouthWest().getLatitude(),
                        viewPort.getSouthWest().getLongitude(),
                        trade,
                        safetySensitiveFlag);
            } else {
                contractorLocations = contractorLocationDAO.findContractorLocations(
                        viewPort.getNorthEast().getLatitude(),
                        viewPort.getNorthEast().getLongitude(),
                        viewPort.getSouthWest().getLatitude(),
                        viewPort.getSouthWest().getLongitude(),
                        safetySensitiveFlag);
            }

        } else {

            contractorLocations = contractorLocationDAO.findContractorLocations(
                    viewPort.getNorthEast().getLatitude(),
                    viewPort.getNorthEast().getLongitude(),
                    viewPort.getSouthWest().getLatitude(),
                    viewPort.getSouthWest().getLongitude()
                    ,trade);

        }
        return contractorLocations;
    }

    private boolean filterSafetySensitive(int safetySensitive) {
        return (safetySensitive == FILTER_BY_SAFETY_SENSITIVE);
    }

    private boolean shouldIncludeSafetySensitiveFilter(int safetySensitive) {
        return IGNORE_SAFETY_SENSITIVE != safetySensitive;
    }

    public List<ContractorLocationInfo> findContractorLocationInfos(ViewPort viewPort, Trade trade, String contractorLocationLinkUrl, int safetySensitive) {
        List<ContractorLocation> contractorLocations = findContractorLocations(viewPort, trade, safetySensitive);
        return buildContractorLocationsInfos(contractorLocations, contractorLocationLinkUrl);
    }

    private List<ContractorLocationInfo> buildContractorLocationsInfos(List<ContractorLocation> contractorLocations, String contractorLocationLinkUrl) {
        if (CollectionUtils.isEmpty(contractorLocations)) {
            return Collections.EMPTY_LIST;
        }
        List<ContractorLocationInfo> contractorLocationInfos = new ArrayList<>();
        for (ContractorLocation contractorLocation : contractorLocations) {
            ContractorLocationInfo contractorLocationInfo = buildContractorLocationInfo(contractorLocation, contractorLocationLinkUrl);
            contractorLocationInfos.add(contractorLocationInfo);
        }
        return contractorLocationInfos;
    }

    private ContractorLocationInfo buildContractorLocationInfo(ContractorLocation contractorLocation, String contractorLocationLinkUrl) {
        ContractorAccount contractor = contractorLocation.getContractor();
        String primaryTradeName = getPrimaryTradeName(contractor);
        List<String> tradeNames = getTradeNames(contractor);
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
                .link(contractorLocationLinkUrl + "?id=" + contractor.getId())

                .build();
        return contractorLocationInfo;
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

}
