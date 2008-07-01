package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum Industry {
	Petrochemical, Mining, Power, General, Construction, Manufacturing, Pharmaceutical, Telecommunications;

	public static String DEFAULT_INDUSTRY = "- Industry -";
	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values =  new ArrayList<String>();
		values.add(Industry.DEFAULT_INDUSTRY);
		for(Industry value : Industry.values())
			values.add(value.name());
		return values;
	}
}
