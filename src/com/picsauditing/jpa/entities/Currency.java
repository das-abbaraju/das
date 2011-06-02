package com.picsauditing.jpa.entities;

public enum Currency {
	USD("USD $","$"), CAD("CAD $","$"), GBP("GBP £","£");

	private String display;
	private String icon;

	private Currency(String display, String icon) {
		this.display = display;
		this.setIcon(display);
	}

	public String getDisplay() {
		return display;
	}

	public static Currency getFromISO(String isoCode) {
		if (isoCode.equals("CA"))
			return CAD;
		if (isoCode.equals("GB"))
			return GBP;

		return USD;
	}

	public boolean isCanada() {
		return this.equals(CAD);
	}

	public boolean isUs() {
		return this.equals(USD);
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}
}
