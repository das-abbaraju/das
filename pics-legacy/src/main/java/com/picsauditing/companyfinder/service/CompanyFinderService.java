package com.picsauditing.companyfinder.service;

import com.picsauditing.companyfinder.model.*;
import com.picsauditing.dao.companyfinder.ContractorLocationDAO;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyFinderService {

    @Autowired
    private ContractorLocationDAO contractorLocationDAO;

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

    public List<ContractorLocation> findByViewPort(ViewPort viewPort){
       return contractorLocationDAO.findByViewPort(
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude());
    }

    public List<ContractorLocation> findByViewPortAndTrade(ViewPort viewPort, Trade trade){
        return contractorLocationDAO.findByViewPortAndTrade(
                trade,
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude());
    }

    public List<ContractorLocationInfo> findContractorLocationInfos(ViewPort viewPort, Trade trade, String contractorLocationLinkUrl) {
        List<ContractorLocation> contractorLocations = findContractorLocations(viewPort, trade);
        List<ContractorLocationInfo> contractorLocationInfos = buildContractorLocationsInfos(contractorLocations, contractorLocationLinkUrl);
        return contractorLocationInfos;
    }

    private List<ContractorLocation> findContractorLocations(ViewPort viewPort, Trade trade) {
        List<ContractorLocation> contractorLocations;
        if (trade != null) {
            contractorLocations = findByViewPortAndTrade(viewPort, trade);
        } else {
            contractorLocations = findByViewPort(viewPort);
        }
        return contractorLocations;
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
        String primaryTrade = getPrimaryTrade(contractor);
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
                .trade(primaryTrade)
                .link(contractorLocationLinkUrl + "?id=" + contractor.getId())

                .build();
        return contractorLocationInfo;
    }

    private String getPrimaryTrade(ContractorAccount contractor) {
        return contractor.getTopTrade() == null ? null : contractor.getTopTrade().getTrade().getName();
    }

}
