package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum Currency {
	USD("USD", "$"),        //US Dollar             (symbol placed before amounts)
    CAD("CAD", "$"),        //Canadian Dollar       (symbol placed before amounts)
    GBP("GBP", "\u00a3"),   //Great Britain Pound   (symbol placed before amounts)
    EUR("EUR", "\u20ac"),   //Euros                 (symbol placed either before or after)
    SEK("SEK", "kr"),       //Swedish Krona         (symbol placed after amounts)
    ZAR("ZAR", "R"),        //South African Rand    (symbol placed after amounts)
    NOK("NOK", "kr"),       //Norwegian Krone       (symbol placed after amounts)
    DKK("DKK", "kr");       //Danish Krone          (symbol placed after amounts)

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
