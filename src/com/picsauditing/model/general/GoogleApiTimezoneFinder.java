package com.picsauditing.model.general;

import java.util.TimeZone;

import com.google.common.base.Strings;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.integration.google.TimezoneLookup;

public class GoogleApiTimezoneFinder implements TimezoneFinder {
	private Geocode geocode;
	private TimezoneLookup timezoneLookup;
	private String googleClientId;

	@Override
	public TimeZone timezoneFromAddress(String address) {
		Geocode geocode = geocode();
		TimezoneLookup timezoneLookup = timezoneLookup();
		LatLong latLong = geocode.latLongFromAddress(address);
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
