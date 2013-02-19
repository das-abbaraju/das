package com.picsauditing.integration.google;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.model.general.LatLong;

public class TimezoneLookup extends GoogleApiOverHttp {
	private static final Logger logger = LoggerFactory.getLogger(TimezoneLookup.class);
	private static final String urlFormat = "https://maps.googleapis.com/maps/api/timezone/json?location=%s&timestamp=%s&sensor=false";

	public TimezoneLookup() {
	}

	// if the google api client id is supplied, we'll sign the url: however, we
	// also MUST have the google key set in system properties ("gk")
	public TimezoneLookup(String googleClientId) {
		this.googleClientId = googleClientId;
	}

	public String timezoneFromLatLong(LatLong latLong) {
		String timezone = "";
		if (latLong != null) {
			String epoch = (new Date().getTime() / 1000) + "";
			String url = createUrl(urlFormat, latLong.toString(), epoch);
			url = signUrlIfGoogleIdSet(url);
			InputStream response = executeUrl(url);
			timezone = timezoneFromResponse(response);
		}
		return timezone;
	}

	private String timezoneFromResponse(InputStream responseStream) {
		String timeZoneId = "";
		try {
			InputStreamReader reader = new InputStreamReader(responseStream, "UTF-8");
			JSONObject response = (JSONObject) JSONValue.parse(reader);
			String status = (String) response.get("status");
			if ("OK".equalsIgnoreCase(status)) {
				timeZoneId = (String) response.get("timeZoneId");
			} else {
				logger.error("Google returned a non-OK status, response was: {}", response);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Cannot get timezone, invalid response incoding: {}", e.getMessage());
		}
		return timeZoneId;
	}

	public static void main(String[] args) {
		TimezoneLookup timezoneLookup = new TimezoneLookup();
		String tz = timezoneLookup.timezoneFromLatLong(new LatLong(39.71732000000001, -105.1713450));
		System.out.println("timezone is " + tz);
	}

}
