package com.picsauditing.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.picsauditing.toggle.FeatureToggle;

public class TimeZoneUtil {
	public static Map<String, String> TIME_ZONES_CANONICAL;
	public static Map<String, String> TIME_ZONES;
	public static Map<String, String> TIME_ZONES_SHORT;
	public static Map<String, String> COUNTRY_TO_TIME_ZONE;
	public static Map<String, String> COUNTRY_SUB_TO_TIME_ZONE;

	static {
		Map<String, String> timeZones = new LinkedHashMap<String, String>();

		timeZones.put("Africa/Cairo", "TimeZone.Africa.Central"); // needs translation
		timeZones.put("Africa/Lagos", "TimeZone.Africa.Lagos");
		timeZones.put("Africa/Nairobi", "TimeZone.Africa.East"); // needs translation
		timeZones.put("Africa/Tripoli", "TimeZone.Africa.Tripoli");
		timeZones.put("Africa/Windhoek", "TimeZone.Africa.Windhoek");
		
		timeZones.put("America/Adak", "TimeZone.US.Aleutian");
		timeZones.put("America/Anchorage", "TimeZone.US.Alaska");
		timeZones.put("America/Argentina/Buenos_Aires", "TimeZone.America.Argentina.Buenos_Aires");
		timeZones.put("America/Bahia_Banderas", "TimeZone.America.Bahia_Banderas");
		timeZones.put("America/Caracas", "TimeZone.America.Caracas");
		timeZones.put("America/Chicago", "TimeZone.US.Central");
		timeZones.put("America/Denver", "TimeZone.US.Mountain");
		timeZones.put("America/Godthab", "TimeZone.America.Godthab");
		timeZones.put("America/La_Paz", "TimeZone.America.La_Paz");
		timeZones.put("America/Los_Angeles", "TimeZone.US.Pacific");
		timeZones.put("America/New_York", "TimeZone.US.Eastern");
		timeZones.put("America/Noronha", "TimeZone.Brazil.DeNoronha");
		timeZones.put("America/Phoenix", "TimeZone.US.Arizona");
		timeZones.put("America/Port-au-Prince", "TimeZone.America.Port-au-Prince");
		timeZones.put("America/Regina", "TimeZone.Canada.Saskatchewan");
		timeZones.put("America/Santiago", "TimeZone.America.Santiago");
		timeZones.put("America/Sao_Paulo", "TimeZone.America.Sao_Paulo");
		timeZones.put("America/St_Johns", "TimeZone.Canada.Newfoundland");
		
		timeZones.put("Etc/GMT", "TimeZone.Greenwich");
		timeZones.put("Etc/UTC", "TimeZone.Etc.UTC");
		timeZones.put("Etc/GMT-4", "TimeZone.Atlantic.Mid"); // needs translation
		timeZones.put("Etc/GMT+12", "TimeZone.Etc.GMT+12");

		timeZones.put("Europe/Helsinki", "TimeZone.Europe.Eastern"); // needs translation
		timeZones.put("Europe/London", "TimeZone.Europe.London");
		timeZones.put("Europe/Moscow", "TimeZone.Europe.Moscow");
		timeZones.put("Europe/Paris", "TimeZone.Europe.Paris");

		timeZones.put("Asia/Almaty", "TimeZone.Asia.Almaty");
		timeZones.put("Asia/Bangkok", "TimeZone.Asia.Bangkok");
		timeZones.put("Asia/Bishkek", "TimeZone.Asia.Bishkek");
		timeZones.put("Asia/Dubai", "TimeZone.Asia.Dubai");
		timeZones.put("Asia/Hovd", "TimeZone.Asia.Hovd");
		timeZones.put("Asia/Irkutsk", "TimeZone.Asia.Irkutsk");
		timeZones.put("Asia/Jerusalem", "TimeZone.Asia.Jerusalem");
		timeZones.put("Asia/Kabul", "TimeZone.Asia.Kabul");
		timeZones.put("Asia/Karachi", "TimeZone.Asia.Karachi");
		timeZones.put("Asia/Kathmandu", "TimeZone.Asia.Kathmandu");
		timeZones.put("Asia/Kolkata", "TimeZone.Asia.Kolkata");
		timeZones.put("Asia/Magadan", "TimeZone.Asia.Magadan");
		timeZones.put("Asia/Rangoon", "TimeZone.Asia.Rangoon");
		timeZones.put("Asia/Riyadh", "TimeZone.Asia.Riyadh");
		timeZones.put("Asia/Shanghai", "TimeZone.Asia.Shanghai");
		timeZones.put("Asia/Tbilisi", "TimeZone.Asia.Tbilisi");
		timeZones.put("Asia/Tehran", "TimeZone.Asia.Tehran");
		timeZones.put("Asia/Tokyo", "TimeZone.Asia.Tokyo");
		
		timeZones.put("Australia/Adelaide", "TimeZone.Australia.Adelaide");
		timeZones.put("Australia/Brisbane", "TimeZone.Australia.Brisbane");
		timeZones.put("Australia/Darwin", "TimeZone.Australia.Darwin");
		timeZones.put("Australia/Eucla", "TimeZone.Australia.Eucla");
		timeZones.put("Australia/Lord_Howe", "TimeZone.Australia.Lord_Howe");
		timeZones.put("Australia/Sydney", "TimeZone.Australia.Sydney");

		timeZones.put("Pacific/Auckland", "TimeZone.Pacific.Auckland");
		timeZones.put("Pacific/Fiji", "TimeZone.Pacific.Fiji");
		timeZones.put("Pacific/Honolulu", "TimeZone.Pacific.Honolulu");
		timeZones.put("Pacific/Pago_Pago", "TimeZone.Pacific.Samoa");
		timeZones.put("Pacific/Tahiti", "TimeZone.Pacific.Tahiti");

		TIME_ZONES_CANONICAL = Collections.unmodifiableMap(timeZones);
	}
	
	static {
		Map<String, String> timeZones = new LinkedHashMap<String, String>();
		timeZones.put("Etc/GMT+12", "TimeZone.Etc.GMT+12");
		timeZones.put("Pacific/Samoa", "TimeZone.Pacific.Samoa");
		timeZones.put("Pacific/Honolulu", "TimeZone.Pacific.Honolulu");
		timeZones.put("Pacific/Tahiti", "TimeZone.Pacific.Tahiti");
		timeZones.put("US/Aleutian", "TimeZone.US.Aleutian");
		timeZones.put("SystemV/YST9", "TimeZone.SystemV.YST9");
		timeZones.put("US/Alaska", "TimeZone.US.Alaska");
		timeZones.put("US/Pacific", "TimeZone.US.Pacific");
		timeZones.put("US/Arizona", "TimeZone.US.Arizona");
		timeZones.put("US/Mountain", "TimeZone.US.Mountain");
		timeZones.put("Canada/Saskatchewan", "TimeZone.Canada.Saskatchewan");
		timeZones.put("America/Bahia_Banderas", "TimeZone.America.Bahia_Banderas");
		timeZones.put("US/Central", "TimeZone.US.Central");
		timeZones.put("America/Port-au-Prince", "TimeZone.America.Port-au-Prince");
		timeZones.put("US/Eastern", "TimeZone.US.Eastern");
		timeZones.put("America/Caracas", "TimeZone.America.Caracas");
		timeZones.put("America/La_Paz", "TimeZone.America.La_Paz");
		timeZones.put("America/Santiago", "TimeZone.America.Santiago");
		timeZones.put("America/Argentina/Buenos_Aires", "TimeZone.America.Argentina.Buenos_Aires");
		timeZones.put("America/Sao_Paulo", "TimeZone.America.Sao_Paulo");
		timeZones.put("Canada/Newfoundland", "TimeZone.Canada.Newfoundland");
		timeZones.put("America/Godthab", "TimeZone.America.Godthab");
		timeZones.put("Brazil/DeNoronha", "TimeZone.Brazil.DeNoronha");
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
		timeZones.put("Australia/Brisbane", "TimeZone.Australia.Brisbane");
		timeZones.put("Australia/Sydney", "TimeZone.Australia.Sydney");
		timeZones.put("Australia/Lord_Howe", "TimeZone.Australia.Lord_Howe");
		timeZones.put("Asia/Magadan", "TimeZone.Asia.Magadan");
		timeZones.put("Pacific/Fiji", "TimeZone.Pacific.Fiji");
		timeZones.put("Pacific/Auckland", "TimeZone.Pacific.Auckland");

		TIME_ZONES = Collections.unmodifiableMap(timeZones);
	}

	static {
		Map<String, String> timeZones = new LinkedHashMap<String, String>();
		timeZones.put("MIT", "TimeZone.Pacific.Samoa");
		timeZones.put("HST", "TimeZone.Pacific.Honolulu");
		timeZones.put("AST", "TimeZone.US.Alaska");
		timeZones.put("PST", "TimeZone.US.Pacific");
		timeZones.put("MST", "TimeZone.US.Mountain");
		timeZones.put("PNT", "TimeZone.US.Arizona");
		timeZones.put("CST", "TimeZone.US.Central");
		timeZones.put("EST", "TimeZone.US.Eastern");
		timeZones.put("IET", "TimeZone.America.Caracas");
		timeZones.put("CNT", "TimeZone.Canada.Newfoundland");
		timeZones.put("AGT", "TimeZone.America.Godthab");
		timeZones.put("BET", "TimeZone.Brazil.DeNoronha");
		timeZones.put("GMT", "TimeZone.Greenwich");
		timeZones.put("UTC", "TimeZone.Etc.UTC");
		timeZones.put("WET", "TimeZone.Africa.Lagos");
		timeZones.put("ECT", "TimeZone.Europe.Paris");
		timeZones.put("ART", "TimeZone.Asia.Jerusalem");
		timeZones.put("EET", "TimeZone.Asia.Riyadh");
		timeZones.put("EAT", "TimeZone.Europe.Moscow");
		timeZones.put("NET", "TimeZone.Asia.Kabul");
		timeZones.put("PLT", "TimeZone.Asia.Calcutta");
		timeZones.put("IST", "TimeZone.Asia.Kathmandu");
		timeZones.put("BST", "TimeZone.Asia.Bangkok");
		timeZones.put("VST", "TimeZone.Asia.Shanghai");
		timeZones.put("CTT", "TimeZone.Asia.Tokyo");
		timeZones.put("PRC", "TimeZone.Asia.Singapore");
		timeZones.put("JST", "TimeZone.Asia.Japan");
		timeZones.put("ACT", "TimeZone.Australia.Darwin");
		timeZones.put("AET", "TimeZone.Australia.Sydney");
		timeZones.put("SST", "TimeZone.Pacific.Norfolk");
		timeZones.put("NST", "TimeZone.Pacific.Auckland");

		TIME_ZONES_SHORT = Collections.unmodifiableMap(timeZones);
	}

	public static Map<String, String> timeZones() {
		FeatureToggle featureToggle = (FeatureToggle) SpringUtils.getBean("FeatureToggle");
		if (featureToggle == null || !featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_CANONICAL_TIMEZONES)) {
			return TIME_ZONES;
		} else {
			return TIME_ZONES_CANONICAL;
		}
	}

	static {
		Map<String, String> countryToTz = new LinkedHashMap<String, String>();
		countryToTz.put("AE", "Asia/Dubai");
		countryToTz.put("IL", "Asia/Jerusalem");
		countryToTz.put("AF", "Asia/Kabul");
		countryToTz.put("NP", "Asia/Kathmandu");
		countryToTz.put("IN", "Asia/Kolkata");
		countryToTz.put("IQ", "Asia/Riyadh");
		countryToTz.put("SA", "Asia/Riyadh");
		countryToTz.put("CN", "Asia/Shanghai");
		countryToTz.put("KR", "Asia/Seoul");
		countryToTz.put("IR", "Asia/Tehran");
		countryToTz.put("JP", "Asia/Tokyo");
		countryToTz.put("EG", "Africa/Cairo");
		countryToTz.put("AO", "Africa/Lagos");
		countryToTz.put("CD", "Africa/Lagos");
		countryToTz.put("CM", "Africa/Lagos");
		countryToTz.put("DZ", "Africa/Lagos");
		countryToTz.put("NE", "Africa/Lagos");
		countryToTz.put("NG", "Africa/Lagos");
		countryToTz.put("KE", "Africa/Nairobi");
		countryToTz.put("AR", "America/Argentina/Buenos_Aires");
		countryToTz.put("BB", "Etc/GMT-4");
		countryToTz.put("PR", "Etc/GMT-4");
		countryToTz.put("GH", "Etc/GMT");
		countryToTz.put("MA", "Etc/GMT");
		countryToTz.put("AL", "Europe/Paris");
		countryToTz.put("AT", "Europe/Paris");
		countryToTz.put("BE", "Europe/Paris");
		countryToTz.put("CH", "Europe/Paris");
		countryToTz.put("DE", "Europe/Paris");
		countryToTz.put("DK", "Europe/Paris");
		countryToTz.put("FR", "Europe/Paris");
		countryToTz.put("IT", "Europe/Paris");
		countryToTz.put("NL", "Europe/Paris");
		countryToTz.put("NO", "Europe/Paris");
		countryToTz.put("SE", "Europe/Paris");
		countryToTz.put("BG", "Europe/Helsinki");
		countryToTz.put("CY", "Europe/Helsinki");
		countryToTz.put("FI", "Europe/Helsinki");
		countryToTz.put("GR", "Europe/Helsinki");
		countryToTz.put("GB", "Europe/London");
		countryToTz.put("IE", "Europe/London");
		countryToTz.put("AS", "Pacific/Pago_Pago");
		COUNTRY_TO_TIME_ZONE = Collections.unmodifiableMap(countryToTz);
	}

	static {
		Map<String, String> countrySubToTz = new LinkedHashMap<String, String>();
		countrySubToTz.put("US-CA", "America/Los_Angeles");
		countrySubToTz.put("US-WA", "America/Los_Angeles");
		countrySubToTz.put("US-NV", "America/Los_Angeles");
		countrySubToTz.put("US-MT", "America/Denver");
		countrySubToTz.put("US-WY", "America/Denver");
		countrySubToTz.put("US-UT", "America/Denver");
		countrySubToTz.put("US-CO", "America/Denver");
		countrySubToTz.put("US-NM", "America/Denver");
		countrySubToTz.put("US-AZ", "America/Phoenix");
		countrySubToTz.put("US-MN", "America/Chicago");
		countrySubToTz.put("US-IA", "America/Chicago");
		countrySubToTz.put("US-MO", "America/Chicago");
		countrySubToTz.put("US-OK", "America/Chicago");
		countrySubToTz.put("US-AR", "America/Chicago");
		countrySubToTz.put("US-LA", "America/Chicago");
		countrySubToTz.put("US-IL", "America/Chicago");
		countrySubToTz.put("US-WI", "America/Chicago");
		countrySubToTz.put("US-MS", "America/Chicago");
		countrySubToTz.put("US-AL", "America/Chicago");
		countrySubToTz.put("US-GA", "America/New_York");
		countrySubToTz.put("US-OH", "America/New_York");
		countrySubToTz.put("US-PA", "America/New_York");
		countrySubToTz.put("US-NY", "America/New_York");
		countrySubToTz.put("US-VT", "America/New_York");
		countrySubToTz.put("US-ME", "America/New_York");
		countrySubToTz.put("US-NH", "America/New_York");
		countrySubToTz.put("US-MA", "America/New_York");
		countrySubToTz.put("US-RI", "America/New_York");
		countrySubToTz.put("US-CT", "America/New_York");
		countrySubToTz.put("US-NJ", "America/New_York");
		countrySubToTz.put("US-DE", "America/New_York");
		countrySubToTz.put("US-MD", "America/New_York");
		countrySubToTz.put("US-DC", "America/New_York");
		countrySubToTz.put("US-WV", "America/New_York");
		countrySubToTz.put("US-VA", "America/New_York");
		countrySubToTz.put("US-NC", "America/New_York");
		countrySubToTz.put("US-SC", "America/New_York");
		countrySubToTz.put("US-AK", "America/Anchorage");
		countrySubToTz.put("US-HI", "Pacific/Honolulu");
		countrySubToTz.put("BR-AC", "America/Rio_Branco");
		countrySubToTz.put("BR-AL", "America/Maceio");
		countrySubToTz.put("BR-SE", "America/Maceio");
		countrySubToTz.put("BR-AM", "America/Manaus");
		countrySubToTz.put("BR-AP", "America/Belem");
		countrySubToTz.put("BR-PA", "America/Belem");
		countrySubToTz.put("BR-BA", "America/Bahia");
		countrySubToTz.put("BR-CE", "America/Fortaleza");
		countrySubToTz.put("BR-MA", "America/Fortaleza");
		countrySubToTz.put("BR-PB", "America/Fortaleza");
		countrySubToTz.put("BR-PI", "America/Fortaleza");
		countrySubToTz.put("BR-RN", "America/Fortaleza");
		countrySubToTz.put("BR-MT", "America/Cuiaba");
		countrySubToTz.put("BR-MS", "America/Campo_Grande");
		countrySubToTz.put("BR-RO", "America/Porto_Velho");
		countrySubToTz.put("BR-PE", "America/Noronha");
		countrySubToTz.put("BR-RR", "America/Boa_Vista");
		countrySubToTz.put("BR-DF", "America/Sao_Paulo");
		countrySubToTz.put("BR-ES", "America/Sao_Paulo");
		countrySubToTz.put("BR-GO", "America/Sao_Paulo");
		countrySubToTz.put("BR-MG", "America/Sao_Paulo");
		countrySubToTz.put("BR-PR", "America/Sao_Paulo");
		countrySubToTz.put("BR-RJ", "America/Sao_Paulo");
		countrySubToTz.put("BR-RS", "America/Sao_Paulo");
		countrySubToTz.put("BR-SC", "America/Sao_Paulo");
		countrySubToTz.put("BR-SP", "America/Sao_Paulo");
		countrySubToTz.put("BR-TO", "America/Araguaina");
		countrySubToTz.put("CA-AB", "America/Edmonton");
		COUNTRY_SUB_TO_TIME_ZONE = Collections.unmodifiableMap(countrySubToTz);
	}
}
