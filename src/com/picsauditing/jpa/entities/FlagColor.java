package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum FlagColor {
	Green, Amber, Red;

	public static String DEFAULT_FLAG_STATUS = "- Flag Status -";

	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		values.add(FlagColor.DEFAULT_FLAG_STATUS);
		for (FlagColor value : FlagColor.values())
			values.add(value.name());
		return values;
	}

}
