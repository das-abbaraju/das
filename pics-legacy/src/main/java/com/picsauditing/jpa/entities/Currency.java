package com.picsauditing.jpa.entities;

public enum Currency {
    USD("USD", "$", "US Dollar", Constants.BEFORE),
    CAD("CAD", "$", "Canadian Dollar", Constants.BEFORE),
    GBP("GBP", "\u00a3", "British Pound", Constants.BEFORE),
    EUR("EUR", "\u20ac", "Euro", Constants.EITHER),
    SEK("SEK", "kr", "Swedish Krona", Constants.AFTER),
    ZAR("ZAR", "R", "South African Rand", Constants.AFTER),
    NOK("NOK", "kr", "Norwegian Krone", Constants.AFTER),
    DKK("DKK", "kr", "Danish Krone", Constants.AFTER),
    AUD("AUD", "$", "Australian Dollar", Constants.AFTER),
    NZD("NZD", "$", "New Zealand Dollar", Constants.AFTER),
    TRY("TRY", "TL", "Turkish Lira", Constants.AFTER),
    CHF("CHF", "Fr", "Swiss Franc", Constants.AFTER),
    PLN("PLN", "z\u0142", "Polish Złoty", Constants.AFTER);

    private String isoCode;
    private String symbol;
    private String englishDescription;
    private String symbolPlacement;

    private Currency(String isoCode, String symbol, String englishDescription, String symbolPlacement) {
        this.isoCode = isoCode;
        this.symbol = symbol;
        this.englishDescription = englishDescription;
        this.symbolPlacement = symbolPlacement;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    public String getSymbolPlacement() {
        return symbolPlacement;
    }

    // methods
    public String getDisplay() {
        return isoCode;
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

    public boolean isCHF() {
        return this.equals(CHF);
    }

    public boolean isTaxable() {
        return isCAD() || isGBP();
    }

    public com.picsauditing.currency.Currency toNewCurrency() {
        return com.picsauditing.currency.Currency.valueOf(this.name());
    }

    private static class Constants {
        public static final String BEFORE = "before";
        public static final String EITHER = "either";
        public static final String AFTER = "after";
    }
}