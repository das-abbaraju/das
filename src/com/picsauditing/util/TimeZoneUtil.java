package com.picsauditing.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeZoneUtil {

	/*
	 * 
	 */
	public static Map<String, String> TIME_ZONES;

	static {
		Map<String, String> timeZones = new LinkedHashMap<String, String>();
		timeZones.put("Etc/GMT+12", "TimeZone.Etc.GMT+12");
		timeZones.put("Pacific/Samoa", "TimeZone.Pacific.Samoa");
		timeZones.put("Pacific/Honolulu", "TimeZone.Pacific.Honolulu");
		timeZones.put("Pacific/Tahiti", "TimeZone.Pacific.Tahiti");
		timeZones.put("US/Aleutian", "TimeZone.US.Aleutian");
		timeZones.put("Pacific/Marquesas", "TimeZone.Pacific.Marquesas");
		timeZones.put("SystemV/YST9", "TimeZone.SystemV.YST9");
		timeZones.put("US/Alaska", "TimeZone.US.Alaska");
		timeZones.put("Pacific/Pitcairn", "TimeZone.Pacific.Pitcairn");
		timeZones.put("US/Pacific", "TimeZone.US.Pacific");
		timeZones.put("US/Arizona", "TimeZone.US.Arizona");
		timeZones.put("US/Mountain", "TimeZone.US.Mountain");
		timeZones.put("Canada/Saskatchewan", "TimeZone.Canada.Saskatchewan");
		timeZones.put("Pacific/Galapagos", "TimeZone.Pacific.Galapagos");
		timeZones.put("Pacific/Easter", "TimeZone.Pacific.Easter");
		timeZones.put("America/Bahia_Banderas", "TimeZone.America.Bahia_Banderas");
		timeZones.put("US/Central", "TimeZone.US.Central");
		timeZones.put("America/Port-au-Prince", "TimeZone.America.Port-au-Prince");
		timeZones.put("US/Eastern", "TimeZone.US.Eastern");
		timeZones.put("America/Caracas", "TimeZone.America.Caracas");
		timeZones.put("America/La_Paz", "TimeZone.America.La_Paz");
		timeZones.put("America/Santiago", "TimeZone.America.Santiago");
		timeZones.put("Atlantic/Bermuda", "TimeZone.Atlantic.Bermuda");
		timeZones.put("America/Argentina/Buenos_Aires", "TimeZone.America.Argentina.Buenos_Aires");
		timeZones.put("America/Sao_Paulo", "TimeZone.America.Sao_Paulo");
		timeZones.put("Canada/Newfoundland", "TimeZone.Canada.Newfoundland");
		timeZones.put("America/Godthab", "TimeZone.America.Godthab");
		timeZones.put("Brazil/DeNoronha", "TimeZone.Brazil.DeNoronha");
		timeZones.put("Atlantic/Cape_Verde", "TimeZone.Atlantic.Cape_Verde");
		timeZones.put("Atlantic/Azores", "TimeZone.Atlantic.Azores");
		timeZones.put("Greenwich", "TimeZone.Greenwich");
		timeZones.put("Etc/UTC", "TimeZone.Etc.UTC");
		timeZones.put("Europe/London", "TimeZone.Europe.London");
		timeZones.put("Africa/Lagos", "TimeZone.Africa.Lagos");
		timeZones.put("Africa/Windhoek", "TimeZone.Africa.Windhoek");
		timeZones.put("Europe/Paris", "TimeZone.Europe.Paris");
		timeZones.put("Africa/Tripoli", "TimeZone.Africa.Tripoli");
		timeZones.put("Asia/Jerusalem", "TimeZone.Asia.Jerusalem");
		timeZones.put("Asia/Riyadh", "TimeZone.Asia.Riyadh");
		timeZones.put("Europe/Moscow", "TimeZone.Europe.Moscow");
		timeZones.put("Asia/Tehran", "TimeZone.Asia.Tehran");
		timeZones.put("Asia/Dubai", "TimeZone.Asia.Dubai");
		timeZones.put("Asia/Tbilisi", "TimeZone.Asia.Tbilisi");
		timeZones.put("Asia/Kabul", "TimeZone.Asia.Kabul");
		timeZones.put("Asia/Karachi", "TimeZone.Asia.Karachi");
		timeZones.put("Asia/Kolkata", "TimeZone.Asia.Kolkata");
		timeZones.put("Asia/Kathmandu", "TimeZone.Asia.Kathmandu");
		timeZones.put("Asia/Colombo", "TimeZone.Asia.Colombo");
		timeZones.put("Asia/Bishkek", "TimeZone.Asia.Bishkek");
		timeZones.put("Asia/Almaty", "TimeZone.Asia.Almaty");
		timeZones.put("Asia/Rangoon", "TimeZone.Asia.Rangoon");
		timeZones.put("Asia/Bangkok", "TimeZone.Asia.Bangkok");
		timeZones.put("Asia/Hovd", "TimeZone.Asia.Hovd");
		timeZones.put("Asia/Shanghai", "TimeZone.Asia.Shanghai");
		timeZones.put("Australia/Eucla", "TimeZone.Australia.Eucla");
		timeZones.put("Asia/Irkutsk", "TimeZone.Asia.Irkutsk");
		timeZones.put("Asia/Tokyo", "TimeZone.Asia.Tokyo");
		timeZones.put("Australia/Darwin", "TimeZone.Australia.Darwin");
		timeZones.put("Australia/Adelaide", "TimeZone.Australia.Adelaide");
		timeZones.put("Asia/Yakutsk", "TimeZone.Asia.Yakutsk");
		timeZones.put("Australia/Brisbane", "TimeZone.Australia.Brisbane");
		timeZones.put("Australia/Sydney", "TimeZone.Australia.Sydney");
		timeZones.put("Australia/Lord_Howe", "TimeZone.Australia.Lord_Howe");
		timeZones.put("Asia/Vladivostok", "TimeZone.Asia.Vladivostok");
		timeZones.put("Pacific/Pohnpei", "TimeZone.Pacific.Pohnpei");
		timeZones.put("Pacific/Norfolk", "TimeZone.Pacific.Norfolk");
		timeZones.put("Asia/Magadan", "TimeZone.Asia.Magadan");
		timeZones.put("Pacific/Fiji", "TimeZone.Pacific.Fiji");
		timeZones.put("Pacific/Auckland", "TimeZone.Pacific.Auckland");
		timeZones.put("Asia/Anadyr", "TimeZone.Asia.Anadyr");
		timeZones.put("Pacific/Chatham", "TimeZone.Pacific.Chatham");
		timeZones.put("Pacific/Tongatapu", "TimeZone.Pacific.Tongatapu");
		timeZones.put("Pacific/Kiritimati", "TimeZone.Pacific.Kiritimati");

		TIME_ZONES = Collections.unmodifiableMap(timeZones);
	}
}
