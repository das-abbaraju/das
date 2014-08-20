package com.picsauditing.integration.google;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.picsauditing.companyfinder.model.MapInfo;
import com.picsauditing.companyfinder.model.ViewPort;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.model.general.LatLong;

public class Geocode extends GoogleApiOverHttp {
	private static final Logger logger = LoggerFactory.getLogger(Geocode.class);
	private static final String urlFormat = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false";
    private static final String urlFormatUnsecure = "http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false";

	public Geocode() {
	}

	// if the google api client id is supplied, we'll sign the url: however, we
	// also MUST have the google key set in system properties ("gk")
	public Geocode(String googleClientId) {
		this.googleClientId = googleClientId;
	}

    public LatLong latLongFromAddressUnsecure(String address) throws Exception {
        LatLong latLong = null;
        if (address != null) {
            String url = createUrl(urlFormatUnsecure, address);
            InputStream response = executeUrl(url);
            latLong = latLongFromResponse(response);
        }
        return latLong;
    }

    public MapInfo mapInfoFromAddressUnsecure(String address) {
        MapInfo mapInfo = null;
        if (address != null) {
            String url = createUrl(urlFormatUnsecure, address);
            InputStream response = executeUrl(url);

          try {
            mapInfo = mapInfoFromResponse(response);
          } catch (Exception e) {
            logger.error("Error extracting map info from geo response.", e);
          }
        }
        return mapInfo;
    }


    public LatLong latLongFromAddress(String address) throws Exception {
		LatLong latLong = null;
		if (address != null) {
			String url = createUrl(urlFormat, address);
			url = signUrlIfGoogleIdSet(url);
			InputStream response = executeUrl(url);
			latLong = latLongFromResponse(response);
		}
		return latLong;
	}

	private LatLong latLongFromResponse(InputStream responseStream) throws Exception {
		if (responseStream != null) {
			try {
				InputStreamReader reader = new InputStreamReader(responseStream, "UTF-8");
				JSONObject response = (JSONObject) JSONValue.parse(reader);
				String status = (String) response.get("status");
				if ("OK".equalsIgnoreCase(status)) {
					return extractLatLong(response);
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("Cannot create geocode, invalid response encoding: {}", e.getMessage());
			}
		}
		return null;
	}

    private MapInfo mapInfoFromResponse(InputStream responseStream) throws Exception {
        if (responseStream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(responseStream, "UTF-8");
                JSONObject response = (JSONObject) JSONValue.parse(reader);
                String status = (String) response.get("status");
                if ("OK".equalsIgnoreCase(status)) {

                    return MapInfo.builder()
                            .center(extractLatLong(response))
                            .viewPort(extractViewPort(response))
                            .build();
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("Cannot create geocode, invalid response encoding: {}", e.getMessage());
            }
        }
        return null;
    }

	private LatLong extractLatLong(JSONObject response) throws Exception {
		JSONArray results = (JSONArray) response.get("results");
		JSONObject data = (JSONObject) results.get(0);
		JSONObject geometry = (JSONObject) data.get("geometry");
		JSONObject location = (JSONObject) geometry.get("location");
		Double latitude = getDouble(location.get("lat"));
		Double longitude = getDouble(location.get("lng"));
		return new LatLong(latitude, longitude);
	}

    private ViewPort extractViewPort(JSONObject response) throws Exception{
        JSONArray results = (JSONArray) response.get("results");
        JSONObject data = (JSONObject) results.get(0);
        JSONObject geometry = (JSONObject) data.get("geometry");
        JSONObject viewport = (JSONObject) geometry.get("viewport");
        JSONObject northeast = (JSONObject) viewport.get("northeast");
        Double northEastLatitude = getDouble(northeast.get("lat"));
        Double northEastLongtitude = getDouble(northeast.get("lng"));

        JSONObject southWest = (JSONObject) viewport.get("southwest");
        Double southWestLatitude = getDouble(southWest.get("lat"));
        Double southWestLongtitude = getDouble(southWest.get("lng"));


        ViewPort result = ViewPort.builder()
                .northEast(LatLong.builder()
                        .lat(northEastLatitude)
                        .lng(northEastLongtitude)
                        .build())
                .southWest(LatLong.builder()
                        .lat(southWestLatitude)
                        .lng(southWestLongtitude)
                        .build())
                .build();

        return result;
    }

  private static Double getDouble(Object object) throws Exception{
    return object instanceof Number ? ((Number) object).doubleValue() : Double.parseDouble((String) object);
  }
}
