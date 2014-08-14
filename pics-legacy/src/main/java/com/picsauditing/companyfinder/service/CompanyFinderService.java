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

    private List<ContractorLocation> findByViewPort(ViewPort viewPort) {
        return contractorLocationDAO.findByViewPort(
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude());
    }

    private List<ContractorLocation> findByViewPortAndSafety(ViewPort viewPort, boolean safetySensitive) {
        return contractorLocationDAO.findByViewPort(
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude(),
                safetySensitive);
    }

    public List<ContractorLocation> findByViewPortAndTrade(ViewPort viewPort, Trade trade) {
        return contractorLocationDAO.findByViewPortAndTrade(
                trade,
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude());
    }

    private List<ContractorLocation> findByViewPortAndTradeAndSafety(ViewPort viewPort, Trade trade, boolean safetySensitive) {
        return contractorLocationDAO.findByViewPortAndTradeAndSafety(
                trade,
                viewPort.getNorthEast().getLatitude(),
                viewPort.getNorthEast().getLongitude(),
                viewPort.getSouthWest().getLatitude(),
                viewPort.getSouthWest().getLongitude(), safetySensitive);
    }

    public List<ContractorLocationInfo> findContractorLocationInfos(ViewPort viewPort, Trade trade, String contractorLocationLinkUrl) {
        return findContractorLocationInfos(viewPort, trade, -1, contractorLocationLinkUrl);
    }

    public List<ContractorLocationInfo> findContractorLocationInfos(ViewPort viewPort, Trade trade, int safetySensitive, String contractorLocationLinkUrl) {
        return buildContractorLocationsInfos(
                findContractorLocations(viewPort,trade, safetySensitive),
                contractorLocationLinkUrl);
    }

    private List<ContractorLocation> findContractorLocations(ViewPort viewPort, Trade trade, int safetySensitive) {
        List<ContractorLocation> contractorLocations;
        if (trade != null) {
            contractorLocations = (safetySensitive != -1) ? findByViewPortAndTradeAndSafety(viewPort, trade, (safetySensitive == 1)) : findByViewPortAndTrade(viewPort, trade);
        } else {
            contractorLocations = (safetySensitive != -1) ? findByViewPortAndSafety(viewPort, (safetySensitive == 1)) : findByViewPort(viewPort);
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
