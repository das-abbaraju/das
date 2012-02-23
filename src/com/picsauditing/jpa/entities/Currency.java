package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum Currency {
	USD("USD", "$"), CAD("CAD", "$"), GBP("GBP", "\u00a3"), EUR("EUR", "\u20ac");

	private String display;
	private String symbol;

	private Currency(String display, String icon) {
		this.display = display;
		this.symbol = icon;
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

	public boolean isCAD() {
		return this.equals(CAD);
	}

	public boolean isUSD() {
		return this.equals(USD);
	}
	

	public boolean isGBP() {
		return this.equals(GBP);
	}
	

	public boolean isEUR() {
		return this.equals(EUR);
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	@Transient
	public boolean isTaxable() {
		return isCAD();
	}

	@Transient
	public InvoiceFee getTaxFee() {
		if (isCAD()) {
			InvoiceFee gst = new InvoiceFee(InvoiceFee.GST);
			gst.setFeeClass(FeeClass.GST);
			return gst;
		} else
			return null;
	}
}
