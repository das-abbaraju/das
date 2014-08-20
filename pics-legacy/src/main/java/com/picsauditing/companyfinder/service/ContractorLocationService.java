package com.picsauditing.companyfinder.service;


import com.picsauditing.companyfinder.dao.ContractorLocationDao;
import com.picsauditing.companyfinder.model.ContractorGeoLocation;
import com.picsauditing.jpa.entities.ContractorAccount;
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

    private static final Logger logger = LoggerFactory.getLogger(ContractorLocationService.class);

    @Autowired
    private ContractorLocationDao contractorLocationDao;

    public void saveLocation(ContractorAccount contractor) {
        String address = parseAddress(contractor);
        ContractorGeoLocation geoLocation = fetchGeoLocation(contractor.getId(), address);
        if (geoLocation == null) {
            logger.error("Could not fetch geoLocation from Google Api");
        } else {
            persistToDatabase(geoLocation);
        }
    }

    private String parseAddress(ContractorAccount ca) {
        String address;
        String street = (String) ca.getAddress();
        street = street != null ? street.replaceAll(" ", "+") : street;
        String city = (String) ca.getCity();
        city = city != null ? city.replaceAll(" ", "+") : "";
        String countrySubdivision = ca.getCountrySubdivision().getName();
        countrySubdivision = countrySubdivision != null ? countrySubdivision.replaceAll(" ", "+").replace("US-", "") : "";
        String zip = ca.getZip();
        zip = zip != null ? zip.replaceAll(" ", "+") : "";
        String country = (String) ca.getCountry().getName();
        country = country != null ? country.replaceAll(" ", "+") : "";
        address = street +
                "+" + city +
                "+" + countrySubdivision +
                "+" + zip +
                "+" + country;
        return address.replaceAll("\\++", "+").toLowerCase().replaceFirst("no\\.", "");
    }


    public ContractorGeoLocation fetchGeoLocation(int contractorId, String address) {

        try {
            Double lng;
            Double lat;
            try {
                if (address != null && !address.isEmpty()) {
                    String body = null;
                    try {
                        body = sendRequestToTheGoogles(address);
                    } catch (IOException e) {
                        logger.error("Unable to send request to Google geolocation service", e);
                    }

                    if (body != null && !body.isEmpty()) {
                        JSONObject location = findLocationObject(body);

                        if (location != null) {
                            lng = (Double) location.get("lng");
                            lat = (Double) location.get("lat");

                            if (lng != null && lat != null) {
                                logger.info(address);
                                return ContractorGeoLocation.createFrom(contractorId, lat.floatValue(), lng.floatValue(), 38586);
                            }
                        } else {
                            logger.info("location null for: " + address);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return null;
        }
        return (JSONObject) geometry.get("location");
    }

    private String sendRequestToTheGoogles(String address) throws Exception {
        String body;
        String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + KEY;
        body = processURL(urlStr);
        return body;
    }

    private String processURL(String urlStr) throws Exception {

        urlStr = urlStr.replaceAll("\\s+", "+").replaceFirst("#", "");

        URL url = new URL(urlStr);
        // URLConnection conn = url.openConnection();
        String output = "";
        try {
            output = new Scanner(url.openStream()).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            //empty result
        }
        logger.info(output);
        return output;
    }
}
