package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum FlagColor {
	Green("#339900"), Amber("#FFCC33"), Red("#CC0000");

	private String hex;
	
	private FlagColor(String hex) {
		this.hex = hex;
	}
	
	public static String DEFAULT_FLAG_STATUS = "- Flag Status -";

	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		values.add(FlagColor.DEFAULT_FLAG_STATUS);
		for (FlagColor value : FlagColor.values())
			values.add(value.name());
		return values;
	}

	public String getHex() {
		return hex;
	}

}
