package com.picsauditing.jpa.entities;

public enum Currency {
	USD("USD $"), CAN("CAN $");

	private String display;

	private Currency(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

}
