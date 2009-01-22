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
	
	public String getBigIcon() {
		return "<img src=\"images/icon_"+this.toString().toLowerCase()+
			"FlagBig.gif\" width=\"32\" height=\"32\" border=\"0\" title=\""+this.toString()+"\" />";
	}
	
	public String getSmallIcon() {
			return "<img src=\"images/icon_"+this.toString().toLowerCase()+
				"Flag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\""+this.toString()+"\" />";
	}
	
	
	public static FlagColor getWorseColor( FlagColor color1, FlagColor color2) {
		if (color2 == null)
			return color1;

		if (color1 == null) {
			System.out.println("WARNING: oldColor == null");
			return color2;
		}

		if (color2.ordinal() > color1.ordinal())
			color1 = color2;
		return color1;
	}
}
