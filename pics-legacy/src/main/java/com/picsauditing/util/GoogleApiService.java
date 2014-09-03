package com.picsauditing.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Scanner;

public class GoogleApiService {

    public static final String KEY = "AIzaSyDm5m03u2TZPDqKjXY94yTXquL9raGgYTI";
    public static final String GOOGLE_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    private static final Logger logger = LoggerFactory.getLogger(GoogleApiService.class);

    public GoogleApiService() {

    }

    public String sendRequestToTheGoogles(String address) throws Exception {
        String urlStr = GOOGLE_GEOCODE_URL + address + "&key=" + KEY;
        return processURL(urlStr);
    }

    private String processURL(String urlStr) throws Exception {
        if (StringUtils.isNotEmpty(urlStr)) {
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
