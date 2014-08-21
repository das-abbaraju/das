package com.picsauditing.companyfinder.service;


import com.picsauditing.companyfinder.dao.ContractorLocationDao;
import com.picsauditing.companyfinder.model.ContractorGeoLocation;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ContractorLocationService {

    public static final JSONParser JSON_PARSER = new JSONParser();
    public static final String KEY = "AIzaSyDm5m03u2TZPDqKjXY94yTXquL9raGgYTI";
    public static final String GOOGLE_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    private static final Logger logger = LoggerFactory.getLogger(ContractorLocationService.class);

    @Autowired
    private ContractorLocationDao contractorLocationDao;

    public void saveLocation(ContractorAccount contractorAccount) {
        if (contractorAccount == null) {
            return;
        }
        String address = parseAddress(contractorAccount);
        ContractorGeoLocation geoLocation = fetchGeoLocation(contractorAccount, address);
        if (geoLocation != null) {
            persistToDatabase(geoLocation);
        } else {
            logger.error("Could not fetch geoLocation from Google Api");
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
        return StringUtils.removeEnd(address,"+");

    }

    public ContractorGeoLocation fetchGeoLocation(ContractorAccount contractorAccount, String address) {
        try {
            if (StringUtils.isNotEmpty(address)) {
                String body = sendRequestToTheGoogles(address);
                if (StringUtils.isNotEmpty(body)) {
                    JSONObject location = findLocationObject(body);
                    if (location != null) {
                        Double lng = (Double) location.get("lng");
                        Double lat = (Double) location.get("lat");
                        if (lng != null && lat != null) {
                            logger.info(address);
                            return ContractorGeoLocation.createFrom(contractorAccount.getId(), lat.floatValue(), lng.floatValue(), contractorAccount.getActiveUser().getId());
                        }
                    } else {
                        logger.info("location null for: " + address);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in fetchGeoLocation()", e);
        }
        return null;
    }

    private void persistToDatabase(ContractorGeoLocation contractorLocation) {
        contractorLocationDao.insertContractorLocation(contractorLocation);
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

    private String sendRequestToTheGoogles(String address) throws Exception {
        String urlStr = GOOGLE_GEOCODE_URL + address + "&key=" + KEY;
        return processURL(urlStr);
    }

    private String processURL(String urlStr) throws Exception {
        if(StringUtils.isNotEmpty(urlStr)) {
            urlStr = urlStr.replaceAll("\\s+", "+").replaceFirst("#", "");
            URL url = new URL(urlStr);
            try {
                return new Scanner(url.openStream()).useDelimiter("\\A").next();
            } catch (java.util.NoSuchElementException e) {
                logger.error("Error in processURL() ", e);
            }
        }
        return null;
    }
}
