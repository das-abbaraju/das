package com.picsauditing.companyfinder.service;


import com.picsauditing.companyfinder.dao.ContractorLocationDAO;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.mail.NoUsersDefinedException;
import com.picsauditing.util.GoogleApiService;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ContractorLocationService {

    public static final JSONParser JSON_PARSER = new JSONParser();

    private static final Logger logger = LoggerFactory.getLogger(ContractorLocationService.class);

    @Autowired
    private ContractorLocationDAO contractorLocationDAO;

    @Autowired
    private GoogleApiService googleApiService;

    public void saveLocation(final ContractorAccount contractorAccount) {
        try {
            if (contractorAccount == null) {
                return;
            }
            String address = parseAddress(contractorAccount);
            LatLong latLong = getLatLong(address);
            if (latLong != null) {
                persistToDatabase(latLong, contractorAccount);
            } else {
                logger.error("Could not fetch geoLocation from Google Api");
            }
        } catch (Exception e) {
            logger.error("Error in saveLocation()", e);
        }
    }

    private String parseAddress(ContractorAccount contractorAccount) {
        String street = contractorAccount.getAddress();
        street = street != null ? street.replaceAll(" ", "+") : street;
        String city = contractorAccount.getCity();
        city = city != null ? city.replaceAll(" ", "+") : "";
        String countrySubdivision = "";
        if (contractorAccount.getCountrySubdivision() != null) {
            countrySubdivision = contractorAccount.getCountrySubdivision().getName();
            countrySubdivision = countrySubdivision != null ? countrySubdivision.replaceAll(" ", "+").replace("US-", "") : "";
        }
        String zip = contractorAccount.getZip();
        zip = zip != null ? zip.replaceAll(" ", "+") : "";

        String country = "";
        if (contractorAccount.getCountry() != null) {
            country = contractorAccount.getCountry().getName();
            country = country != null ? country.replaceAll(" ", "+") : "";
        }
        String address = street +
                "+" + city +
                "+" + countrySubdivision +
                "+" + zip +
                "+" + country;
        address = address.replaceAll("\\++", "+").toLowerCase().replaceFirst("no\\.", "");
        return StringUtils.removeEnd(address, "+");
    }

    class LatLong {
        Double latitude, longitude;
        LatLong(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    
    public LatLong getLatLong(String address) throws Exception {
        if (StringUtils.isNotEmpty(address)) {
            String body = googleApiService.sendRequestToTheGoogles(address);
            if (StringUtils.isNotEmpty(body)) {
                JSONObject location = findLocationObject(body);
                if (location != null) {
                    Double lng = (Double) location.get("lng");
                    Double lat = (Double) location.get("lat");
                    if (lng != null && lat != null) {
                        logger.debug(address);
                        return new LatLong(lat, lng);
                    }
                } else {
                    logger.debug("location null for: " + address);
                }
            }
        }
        return null;
    }

    private void persistToDatabase(LatLong latLong, ContractorAccount contractorAccount) throws NoUsersDefinedException {
        ContractorLocation contractorLocation = contractorLocationDAO.findById(contractorAccount.getId());
        contractorLocation = buildContratorLocation(contractorAccount, contractorLocation, latLong);
        contractorLocationDAO.save(contractorLocation);
    }

    private ContractorLocation buildContratorLocation(ContractorAccount contractorAccount, ContractorLocation conLoc, LatLong latLong) throws NoUsersDefinedException {
        ContractorLocation contractorLocation = (conLoc != null) ? conLoc : new ContractorLocation();
        contractorLocation.setContractor(contractorAccount);
        contractorLocation.setLatitude(latLong.latitude);
        contractorLocation.setLongitude(latLong.longitude);
        contractorLocation.setCreatedBy(contractorAccount.getActiveUser());
        contractorLocation.setUpdatedBy(contractorAccount.getActiveUser());
        contractorLocation.setCreationDate(new Date(System.currentTimeMillis()));
        contractorLocation.setUpdateDate(new Date(System.currentTimeMillis()));
        return contractorLocation;
    }

    private JSONObject findLocationObject(String body) throws Exception {
        JSONObject geometry;
        try {
            JSONObject object = (JSONObject) JSON_PARSER.parse(body);
            JSONArray results = (JSONArray) object.get("results");
            JSONObject firstResult = (JSONObject) results.get(0);
            geometry = (JSONObject) firstResult.get("geometry");
        } catch (Exception e) {
            logger.error("Error in findLocationObject() ", e);
            return null;
        }
        return (JSONObject) geometry.get("location");
    }

}
