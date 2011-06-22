package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneUtil {

	// 73 timezones, a subset from the java library of 605 timezones. These are organized by displayed offset.
	public static List<TimeZoneOption> TimeZones = new ArrayList<TimeZoneOption>();

	static {
		TimeZones.add(new TimeZoneOption("Etc/GMT+12", "TimeZone.Etc.GMT+12"));
		TimeZones.add(new TimeZoneOption("Pacific/Samoa", "TimeZone.Pacific.Samoa"));
		TimeZones.add(new TimeZoneOption("Pacific/Honolulu", "TimeZone.Pacific.Honolulu"));
		TimeZones.add(new TimeZoneOption("Pacific/Tahiti", "TimeZone.Pacific.Tahiti"));
		TimeZones.add(new TimeZoneOption("US/Aleutian", "TimeZone.US.Aleutian"));
		TimeZones.add(new TimeZoneOption("Pacific/Marquesas", "TimeZone.Pacific.Marquesas"));
		TimeZones.add(new TimeZoneOption("SystemV/YST9", "TimeZone.SystemV.YST9"));
		TimeZones.add(new TimeZoneOption("US/Alaska", "TimeZone.US.Alaska"));
		TimeZones.add(new TimeZoneOption("Pacific/Pitcairn", "TimeZone.Pacific.Pitcairn"));
		TimeZones.add(new TimeZoneOption("US/Pacific", "TimeZone.US.Pacific"));
		TimeZones.add(new TimeZoneOption("US/Arizona", "TimeZone.US.Arizona"));
		TimeZones.add(new TimeZoneOption("US/Mountain", "TimeZone.US.Mountain"));
		TimeZones.add(new TimeZoneOption("Canada/Saskatchewan", "TimeZone.Canada.Saskatchewan"));
		TimeZones.add(new TimeZoneOption("Pacific/Galapagos", "TimeZone.Pacific.Galapagos"));
		TimeZones.add(new TimeZoneOption("Pacific/Easter", "TimeZone.Pacific.Easter"));
		TimeZones.add(new TimeZoneOption("America/Bahia_Banderas", "TimeZone.America.Bahia_Banderas"));
		TimeZones.add(new TimeZoneOption("US/Central", "TimeZone.US.Central"));
		TimeZones.add(new TimeZoneOption("America/Port-au-Prince", "TimeZone.America.Port-au-Prince"));
		TimeZones.add(new TimeZoneOption("US/Eastern", "TimeZone.US.Eastern"));
		TimeZones.add(new TimeZoneOption("America/Caracas", "TimeZone.America.Caracas"));
		TimeZones.add(new TimeZoneOption("America/La_Paz", "TimeZone.America.La_Paz"));
		TimeZones.add(new TimeZoneOption("America/Santiago", "TimeZone.America.Santiago"));
		TimeZones.add(new TimeZoneOption("Atlantic/Bermuda", "TimeZone.Atlantic.Bermuda"));
		TimeZones.add(new TimeZoneOption("America/Argentina/Buenos_Aires", "TimeZone.America.Argentina.Buenos_Aires"));
		TimeZones.add(new TimeZoneOption("America/Sao_Paulo", "TimeZone.America.Sao_Paulo"));
		TimeZones.add(new TimeZoneOption("Canada/Newfoundland", "TimeZone.Canada.Newfoundland"));
		TimeZones.add(new TimeZoneOption("America/Godthab", "TimeZone.America.Godthab"));
		TimeZones.add(new TimeZoneOption("Brazil/DeNoronha", "TimeZone.Brazil.DeNoronha"));
		TimeZones.add(new TimeZoneOption("Atlantic/Cape_Verde", "TimeZone.Atlantic.Cape_Verde"));
		TimeZones.add(new TimeZoneOption("Atlantic/Azores", "TimeZone.Atlantic.Azores"));
		TimeZones.add(new TimeZoneOption("Greenwich", "TimeZone.Greenwich"));
		TimeZones.add(new TimeZoneOption("Etc/UTC", "TimeZone.Etc.UTC"));
		TimeZones.add(new TimeZoneOption("Europe/London", "TimeZone.Europe.London"));
		TimeZones.add(new TimeZoneOption("Africa/Lagos", "TimeZone.Africa.Lagos"));
		TimeZones.add(new TimeZoneOption("Africa/Windhoek", "TimeZone.Africa.Windhoek"));
		TimeZones.add(new TimeZoneOption("Europe/Paris", "TimeZone.Europe.Paris"));
		TimeZones.add(new TimeZoneOption("Africa/Tripoli", "TimeZone.Africa.Tripoli"));
		TimeZones.add(new TimeZoneOption("Asia/Jerusalem", "TimeZone.Asia.Jerusalem"));
		TimeZones.add(new TimeZoneOption("Asia/Riyadh", "TimeZone.Asia.Riyadh"));
		TimeZones.add(new TimeZoneOption("Europe/Moscow", "TimeZone.Europe.Moscow"));
		TimeZones.add(new TimeZoneOption("Asia/Tehran", "TimeZone.Asia.Tehran"));
		TimeZones.add(new TimeZoneOption("Asia/Dubai", "TimeZone.Asia.Dubai"));
		TimeZones.add(new TimeZoneOption("Asia/Tbilisi", "TimeZone.Asia.Tbilisi"));
		TimeZones.add(new TimeZoneOption("Asia/Kabul", "TimeZone.Asia.Kabul"));
		TimeZones.add(new TimeZoneOption("Asia/Karachi", "TimeZone.Asia.Karachi"));
		TimeZones.add(new TimeZoneOption("Asia/Kolkata", "TimeZone.Asia.Kolkata"));
		TimeZones.add(new TimeZoneOption("Asia/Kathmandu", "TimeZone.Asia.Kathmandu"));
		TimeZones.add(new TimeZoneOption("Asia/Colombo", "TimeZone.Asia.Colombo"));
		TimeZones.add(new TimeZoneOption("Asia/Bishkek", "TimeZone.Asia.Bishkek"));
		TimeZones.add(new TimeZoneOption("Asia/Almaty", "TimeZone.Asia.Almaty"));
		TimeZones.add(new TimeZoneOption("Asia/Rangoon", "TimeZone.Asia.Rangoon"));
		TimeZones.add(new TimeZoneOption("Asia/Bangkok", "TimeZone.Asia.Bangkok"));
		TimeZones.add(new TimeZoneOption("Asia/Hovd", "TimeZone.Asia.Hovd"));
		TimeZones.add(new TimeZoneOption("Asia/Shanghai", "TimeZone.Asia.Shanghai"));
		TimeZones.add(new TimeZoneOption("Australia/Eucla", "TimeZone.Australia.Eucla"));
		TimeZones.add(new TimeZoneOption("Asia/Irkutsk", "TimeZone.Asia.Irkutsk"));
		TimeZones.add(new TimeZoneOption("Asia/Tokyo", "TimeZone.Asia.Tokyo"));
		TimeZones.add(new TimeZoneOption("Australia/Darwin", "TimeZone.Australia.Darwin"));
		TimeZones.add(new TimeZoneOption("Australia/Adelaide", "TimeZone.Australia.Adelaide"));
		TimeZones.add(new TimeZoneOption("Asia/Yakutsk", "TimeZone.Asia.Yakutsk"));
		TimeZones.add(new TimeZoneOption("Australia/Brisbane", "TimeZone.Australia.Brisbane"));
		TimeZones.add(new TimeZoneOption("Australia/Sydney", "TimeZone.Australia.Sydney"));
		TimeZones.add(new TimeZoneOption("Australia/Lord_Howe", "TimeZone.Australia.Lord_Howe"));
		TimeZones.add(new TimeZoneOption("Asia/Vladivostok", "TimeZone.Asia.Vladivostok"));
		TimeZones.add(new TimeZoneOption("Pacific/Pohnpei", "TimeZone.Pacific.Pohnpei"));
		TimeZones.add(new TimeZoneOption("Pacific/Norfolk", "TimeZone.Pacific.Norfolk"));
		TimeZones.add(new TimeZoneOption("Asia/Magadan", "TimeZone.Asia.Magadan"));
		TimeZones.add(new TimeZoneOption("Pacific/Fiji", "TimeZone.Pacific.Fiji"));
		TimeZones.add(new TimeZoneOption("Pacific/Auckland", "TimeZone.Pacific.Auckland"));
		TimeZones.add(new TimeZoneOption("Asia/Anadyr", "TimeZone.Asia.Anadyr"));
		TimeZones.add(new TimeZoneOption("Pacific/Chatham", "TimeZone.Pacific.Chatham"));
		TimeZones.add(new TimeZoneOption("Pacific/Tongatapu", "TimeZone.Pacific.Tongatapu"));
		TimeZones.add(new TimeZoneOption("Pacific/Kiritimati", "TimeZone.Pacific.Kiritimati"));
	}

	static public int getOffset(TimeZone tz, Date today) {
		int rawOffset = tz.getRawOffset();
		if (tz.inDaylightTime(today)) {
			rawOffset += (60 * 60 * 1000);
		}
		return rawOffset;
	}

	static public List<TimeZoneOption> getTimeZoneSelector() {
		return TimeZones;
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
