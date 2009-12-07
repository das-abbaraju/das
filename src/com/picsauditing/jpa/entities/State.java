package com.picsauditing.jpa.entities;

import java.util.TreeMap;

public class State {

	public static TreeMap<String, String> getStates(String country) {
		if("US".equals(country))
			return getUSStates();
		
		if("CA".equals(country))
			return getCAStates();
		
		return new TreeMap<String, String>();
	}


	public static TreeMap<String, String> getUSStates() {
		TreeMap<String, String> states = new TreeMap<String, String>();
		states.put("AL", "Alabama");
		states.put("AK", "Alaska");
		states.put("AZ", "Arizona");
		states.put("AR", "Arkansas");
		states.put("CA", "California");
		states.put("CO", "Colorado");
		states.put("CT", "Connecticut");
		states.put("DE", "Delaware");
		states.put("FL", "Florida");
		states.put("GA", "Georgia");
		states.put("GU", "Guam");
		states.put("HI", "Hawaii");
		states.put("ID", "Idaho");
		states.put("IL", "Illinois");
		states.put("IN", "Indiana");
		states.put("IA", "Iowa");
		states.put("KS", "Kansas");
		states.put("KY", "Kentucky");
		states.put("LA", "Louisiana");
		states.put("ME", "Maine");
		states.put("MD", "Maryland");
		states.put("MA", "Massachusetts");
		states.put("MI", "Michigan");
		states.put("MN", "Minnesota");
		states.put("MS", "Mississippi");
		states.put("MO", "Missouri");
		states.put("MT", "Montana");
		states.put("NE", "Nebraska");
		states.put("NV", "Nevada");
		states.put("NH", "New Hampshire");
		states.put("NJ", "New Jersey");
		states.put("NM", "New Mexico");
		states.put("NY", "New York");
		states.put("NC", "North Carolina");
		states.put("ND", "North Dakota");
		states.put("OH", "Ohio");
		states.put("OK", "Oklahoma");
		states.put("OR", "Oregon");
		states.put("PA", "Pennsylvania");
		states.put("PR", "Puerto Rico");
		states.put("RI", "Rhode Island");
		states.put("SC", "South Carolina");
		states.put("SD", "South Dakota");
		states.put("TN", "Tennessee");
		states.put("TX", "Texas");
		states.put("UT", "Utah");
		states.put("VT", "Vermont");
		states.put("VA", "Virginia");
		states.put("WA", "Washington");
		states.put("DC", "Washington D.C.");
		states.put("WV", "West Virginia");
		states.put("WI", "Wisconsin");
		states.put("WY", "Wyoming");
		return states;
	}

	public static TreeMap<String, String> getCAStates() {
		TreeMap<String, String> states = new TreeMap<String, String>();
		states.put("AB", "Alberta");
		states.put("MB", "Manitoba");
		states.put("NL", "Newfoundland");
		states.put("NS", "Nova Scotia");
		states.put("NU", "Nunavut");
		states.put("ON", "Ontario");
		states.put("QC", "Quebec");
		states.put("YT", "Yukon");
		states.put("BC", "British Columbia");
		states.put("NB", "New Brunswick");
		states.put("PE", "Prince Edward Is.");
		states.put("SK", "Saskatchewan");
		return states;
	}
}
