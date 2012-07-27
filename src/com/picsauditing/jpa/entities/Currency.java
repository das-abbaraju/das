package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum Currency {
	USD("USD", "$"), CAD("CAD", "$"), GBP("GBP", "\u00a3"), EUR("EUR", "\u20ac"), DKK("DKK", "\u6b72"), SEK("SEK", "\u6b72"), NOK("NOL", "\u6b72"), ZAR("ZAR", "\u0052");

	private String display;
	private String symbol;

	private Currency(String display, String icon) {
		this.display = display;
		this.symbol = icon;
	}

	public String getDisplay() {
		return display;
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

	public boolean isDKK(){
		return this.equals(DKK);
	}

	public boolean isSEK(){
		return this.equals(SEK);
	}

	public boolean isNOK(){
		return this.equals(NOK);
	}

	public boolean isZAR(){
		return this.equals(ZAR);
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	@Transient
	public boolean isTaxable() {
		//return isCAD() || isGBP() || isEUR();
		return isCAD() || isGBP();
	}

	@Transient
	public InvoiceFee getTaxFee() {
		if (isCAD()) {
			InvoiceFee gst = new InvoiceFee(InvoiceFee.GST);
			gst.setFeeClass(FeeClass.GST);
			return gst;
		//} else if(isGBP() || isEUR()){
		} else if(isGBP()){
			InvoiceFee vat = new InvoiceFee(InvoiceFee.VAT);
			vat.setFeeClass(FeeClass.VAT);
			return vat;
		} else
			return null;
	}
}
