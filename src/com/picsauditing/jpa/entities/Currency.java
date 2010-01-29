package com.picsauditing.jpa.entities;

public enum Currency {
	USD("USD $"), CAD("CAD $");

	private String display;

	private Currency(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

	public static Currency getFromISO(String isoCode) {
		if (isoCode.equals("CA"))
			return CAD;

		return USD;
	}
}
