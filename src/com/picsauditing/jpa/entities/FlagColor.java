package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum FlagColor {

	Green("#339900", "Approve"),
	Amber("#FFCC33", "Review"),
	Red("#CC0000", "Reject");

	private String hex;
	private String insuranceStatus;

	private FlagColor(String hex, String insuranceStatus) {
		this.hex = hex;
		this.insuranceStatus = insuranceStatus;
	}

	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		for (FlagColor value : FlagColor.values())
			values.add(value.name());
		return values;
	}

	public String getHex() {
		return hex;
	}

	public String getInsuranceStatus() {
		return insuranceStatus;
	}

	public String getBigIcon() {
		return "<img src=\"images/icon_" + this.toString().toLowerCase()
				+ "FlagBig.gif\" width=\"32\" height=\"32\" border=\"0\" title=\"" + this.toString() + "\" />";
	}

	public String getSmallIcon() {
		return "<img src=\"images/icon_" + this.toString().toLowerCase()
				+ "Flag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"" + this.toString() + "\" />";
	}

	static public String getSmallIcon(String flagColor) {
		return valueOf(flagColor).getSmallIcon();
	}

	public static FlagColor getWorseColor(FlagColor color1, FlagColor color2) {
		if (color2 == null)
			return color1;

		if (color1 == null) {
			// System.out.println("WARNING: oldColor == null");
			return color2;
		}

		if (color2.ordinal() > color1.ordinal())
			color1 = color2;
		return color1;
	}

	public boolean isRed() {
		return this.equals(Red);
	}

	public boolean isAmber() {
		return this.equals(Amber);
	}

	public boolean isGreen() {
		return this.equals(Green);
	}
}
