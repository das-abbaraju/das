package com.picsauditing.model.general;

import java.util.TimeZone;

import com.google.common.base.Strings;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.integration.google.TimezoneLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleApiTimezoneFinder implements TimezoneFinder {
	private Geocode geocode;
	private TimezoneLookup timezoneLookup;
	private String googleClientId;

  private static final Logger logger = LoggerFactory.getLogger(GoogleApiTimezoneFinder.class);

	@Override
	public TimeZone timezoneFromAddress(String address) {
		Geocode geocode = geocode();
		TimezoneLookup timezoneLookup = timezoneLookup();
    LatLong latLong = null;
    try {
      latLong = geocode.latLongFromAddress(address);
    } catch (Exception e) {
      logger.error("Error creating timezone from address.", e);
      return null;
    }
    String tz = timezoneLookup.timezoneFromLatLong(latLong);
		if (!Strings.isNullOrEmpty(tz)) {
			return TimeZone.getTimeZone(tz);
		} else {
			return null;
		}
	}

	public String getGoogleClientId() {
		return googleClientId;
	}

	public void setGoogleClientId(String googleClientId) {
		this.googleClientId = googleClientId;
	}

	private Geocode geocode() {
		if (geocode == null) {
			return new Geocode(googleClientId);
		}
		return geocode;
	}

	private TimezoneLookup timezoneLookup() {
		if (timezoneLookup == null) {
			return new TimezoneLookup(googleClientId);
		}
		return timezoneLookup;
	}
}
