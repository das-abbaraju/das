package com.picsauditing.companyfinder.service;

import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.companyfinder.model.MapInfoResponse;
import com.picsauditing.companyfinder.model.ViewPort;
import com.picsauditing.dao.companyfinder.ContractorLocationDAO;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.model.general.LatLong;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CompanyFinderService {

    @Autowired
    private ContractorLocationDAO contractorLocationDAO;

    public LatLong latLongFromAddressUnsecure(String address) {
        return new Geocode().latLongFromAddressUnsecure(address);
    }

    public MapInfoResponse mapInfoFromAddressUnsecure(String address) {
        return new Geocode().mapInfoFromAddressUnsecure(address);
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

}
