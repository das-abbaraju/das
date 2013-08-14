package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.TimeZoneListByCountry;


public class TimeZoneBuilder {

	private TimeZoneListByCountry timeZone = new TimeZoneListByCountry();

	public TimeZoneListByCountry build() {
		return timeZone;

	}

	public TimeZoneBuilder id(int i) {
		timeZone.setId(i);
		return this;
	}

	public TimeZoneBuilder countryCode(String countryCode) {
		timeZone.setCountryCode(countryCode);
		return this;
	}

	public TimeZoneBuilder timeZoneName(String timeZoneName) {
		timeZone.setTimeZoneName(timeZoneName);
		return this;
	}
}
