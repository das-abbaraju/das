package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneUtil {

	static public List<TimeZone> getTimeZones() {
		String[] zoneIds = TimeZone.getAvailableIDs();

		List<TimeZone> timezones = new ArrayList<TimeZone>();
		for (int i = 0; i < zoneIds.length; i++) {
			String zoneId = zoneIds[i];
			if (zoneId.startsWith("US/") || zoneId.startsWith("Canada/") || zoneId.startsWith("Asia/Dubai")) {
				timezones.add(TimeZone.getTimeZone(zoneIds[i]));
			}
		}

		return timezones;
	}

	static public int getOffset(TimeZone tz, Date today) {
		int rawOffset = tz.getRawOffset();
		if (tz.inDaylightTime(today)) {
			rawOffset += (60 * 60 * 1000);
		}
		return rawOffset;
	}

	static public String getDisplayName(TimeZone tz, Date today) {
		StringBuffer out = new StringBuffer();
		out.append("(GMT");
		out.append(tz.getOffset(today.getTime()) / (60 * 60 * 1000));
		out.append(") ");
		String longName = tz.getDisplayName(tz.inDaylightTime(today), TimeZone.LONG);
		out.append(longName);
		String city = tz.getID().substring(tz.getID().lastIndexOf("/") + 1);
		String country = tz.getID().substring(0, tz.getID().indexOf("/"));
		if (!longName.contains(city))
			out.append(" (").append(city).append(")");
		else
			out.append(" (").append(country).append(")");
		return out.toString();
	}

	static public List<TimeZoneOption> getTimeZoneSelector() {
		List<TimeZoneOption> timezones = new ArrayList<TimeZoneOption>();

		Date today = new Date();
		for (TimeZone tz : getTimeZones()) {
			timezones.add(new TimeZoneOption(tz.getID(), getDisplayName(tz, today)));
		}

		return timezones;

	}

	static public class TimeZoneOption {
		public String key;
		public String value;

		public TimeZoneOption(String tzShort, String tzLong) {
			this.key = tzShort;
			this.value = tzLong;
		}
	}

}
