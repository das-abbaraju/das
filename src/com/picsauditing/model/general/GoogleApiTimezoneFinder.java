package com.picsauditing.model.general;

import java.util.TimeZone;

import com.google.common.base.Strings;
import com.picsauditing.integration.google.Geocode;
import com.picsauditing.integration.google.TimezoneLookup;

public class GoogleApiTimezoneFinder implements TimezoneFinder {
	private static Geocode geocode;
	private static TimezoneLookup timezoneLookup;

	@Override
	public TimeZone timezoneIdFromAddress(String address) {
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

	private Geocode geocode() {
		if (geocode == null) {
			return new Geocode();
		}
		return geocode;
	}

	private TimezoneLookup timezoneLookup() {
		if (timezoneLookup == null) {
			return new TimezoneLookup();
		}
		return timezoneLookup;
	}
}
