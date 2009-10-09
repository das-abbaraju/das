package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum Industry {
	Petrochemical, Mining, Power, General, Construction, Manufacturing, Pharmaceutical, PulpPaper("Pulp and Paper"), Telecommunications;

	private String description;
	
	private Industry() {
		this.description = this.toString();
	}

	private Industry(String description) {
		this.description = description;
	}
	
	public static String DEFAULT_INDUSTRY = "- Industry -";
	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values =  new ArrayList<String>();
		values.add(Industry.DEFAULT_INDUSTRY);
		for(Industry value : Industry.values())
			values.add(value.name());
		return values;
	}

	public String getDescription() {
		return description;
	}
}
