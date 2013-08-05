package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

@Deprecated
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

    @Deprecated
	private Currency(String display, String icon) {
		this.display = display;
		this.symbol = icon;
	}

    @Deprecated
	public String getDisplay() {
		return display;
	}

    @Deprecated
	public boolean isCAD() {
		return this.equals(CAD);
	}

    @Deprecated
	public boolean isUSD() {
		return this.equals(USD);
	}


    @Deprecated
	public boolean isGBP() {
		return this.equals(GBP);
	}


    @Deprecated
	public boolean isEUR() {
		return this.equals(EUR);
	}

    @Deprecated
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

    @Deprecated
	public String getSymbol() {
		return symbol;
	}

    @Deprecated
    @Transient
	public boolean isTaxable() {
		return isCAD() || isGBP();
	}

    @Deprecated
    public com.picsauditing.currency.Currency toNewCurrency() {
        return com.picsauditing.currency.Currency.valueOf(this.name());
    }
}