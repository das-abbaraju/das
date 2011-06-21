package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneUtil {

	// 73 timezones, a subset from the java library of 605 timezones. These are organized by displayed offset.
	public static String[] TimeZones = { "Etc/GMT+12", "Pacific/Samoa", "Pacific/Honolulu", "Pacific/Tahiti",
			"US/Aleutian", "Pacific/Marquesas", "SystemV/YST9", "US/Alaska", "Pacific/Pitcairn", "US/Pacific",
			"US/Arizona", "US/Mountain", "Canada/Saskatchewan", "Pacific/Galapagos", "Pacific/Easter",
			"America/Bahia_Banderas", "US/Central", "America/Port-au-Prince", "US/Eastern", "America/Caracas",
			"America/La_Paz", "America/Santiago", "Atlantic/Bermuda", "America/Argentina/Buenos_Aires",
			"America/Sao_Paulo", "Canada/Newfoundland", "America/Godthab", "Brazil/DeNoronha", "Atlantic/Cape_Verde",
			"Atlantic/Azores", "Greenwich", "Etc/UTC", "Europe/London", "Africa/Lagos", "Africa/Windhoek",
			"Europe/Paris", "Africa/Tripoli", "Asia/Jerusalem", "Asia/Riyadh", "Europe/Moscow", "Asia/Tehran",
			"Asia/Dubai", "Asia/Tbilisi", "Asia/Kabul", "Asia/Karachi", "Asia/Kolkata", "Asia/Kathmandu",
			"Asia/Colombo", "Asia/Bishkek", "Asia/Almaty", "Asia/Rangoon", "Asia/Bangkok", "Asia/Hovd",
			"Asia/Shanghai", "Australia/Eucla", "Asia/Irkutsk", "Asia/Tokyo", "Australia/Darwin", "Australia/Adelaide",
			"Asia/Yakutsk", "Australia/Brisbane", "Australia/Sydney", "Australia/Lord_Howe", "Asia/Vladivostok",
			"Pacific/Pohnpei", "Pacific/Norfolk", "Asia/Magadan", "Pacific/Fiji", "Pacific/Auckland", "Asia/Anadyr",
			"Pacific/Chatham", "Pacific/Tongatapu", "Pacific/Kiritimati" };

	/*
	 * public static String[] TimeZones = { };
	 */
	static public List<TimeZone> getTimeZones() {
		List<TimeZone> timezones = new ArrayList<TimeZone>();

		for (int i = 0; i < TimeZones.length; i++) {
			timezones.add(TimeZone.getTimeZone(TimeZones[i]));
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
		out.append("(GMT ");
		out.append(tz.getOffset(today.getTime()) / (60 * 60 * 1000));
		out.append(") ");
		String longName = tz.getDisplayName(tz.inDaylightTime(today), TimeZone.LONG);
		out.append(longName);
		if (tz.getID().contains("/")) {
			String city = tz.getID().substring(tz.getID().lastIndexOf("/") + 1);
			String country = tz.getID().substring(0, tz.getID().indexOf("/"));
			if (!longName.contains(city))
				out.append(" (").append(city).append(")");
			else
				out.append(" (").append(country).append(")");
		}
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
