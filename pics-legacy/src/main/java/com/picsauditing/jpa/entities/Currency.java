package com.picsauditing.jpa.entities;

import com.picsauditing.quickbooks.model.CreditCardAccount;
import com.picsauditing.quickbooks.model.UnDepositedFundsAccount;

public enum Currency {
    USD("USD", "$",         "US Dollar",            CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    CAD("CAD", "$",         "Canadian Dollar",      CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    GBP("GBP", "\u00a3",    "British Pound",        CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    EUR("EUR", "\u20ac",    "Euro",                 CreditCardAccount.AMEX_MERCHANT_ACCOUNT_EURO,   CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCT_EURO,  UnDepositedFundsAccount.UNDEPOSITED_FUNDS_EURO),
    SEK("SEK", "kr",        "Swedish Krona",        CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    ZAR("ZAR", "R",         "South African Rand",   CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    NOK("NOK", "kr",        "Norwegian Krone",      CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    DKK("DKK", "kr",        "Danish Krone",         CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    AUD("AUD", "$",         "Australian Dollar",    CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    NZD("NZD", "$",         "New Zealand Dollar",   CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    TRY("TRY", "TL",        "Turkish Lira",         CreditCardAccount.AMEX_MERCHANT_ACCOUNT,        CreditCardAccount.VISA_MC_DISC_MERCHANT_ACCOUNT,    UnDepositedFundsAccount.UNDEPOSITED_FUNDS),
    CHF("CHF", "Fr",        "Swiss Franc",          CreditCardAccount.NONE,                         CreditCardAccount.VISA_CHF,                         UnDepositedFundsAccount.UNDEPOSITED_FUNDS_CHF),
    PLN("PLN", "z\u0142",   "Polish Złoty",         CreditCardAccount.NONE,                         CreditCardAccount.VISA_PLN,                         UnDepositedFundsAccount.UNDEPOSITED_FUNDS_PLN);

    private String isoCode;
    private String symbol;
    private String englishDescription;
    private CreditCardAccount qbAmexAccount;
    private CreditCardAccount qbVisaMCDiscAccount;
    private UnDepositedFundsAccount qbUnDepositedFundsAccount;

    private Currency(String isoCode, String symbol, String englishDescription, CreditCardAccount qbAmexAccount, CreditCardAccount qbVisaMCDiscAccount, UnDepositedFundsAccount qbUnDepositedFundsAccount) {
        this.isoCode = isoCode;
        this.symbol = symbol;
        this.englishDescription = englishDescription;
        this.qbAmexAccount = qbAmexAccount;
        this.qbVisaMCDiscAccount = qbVisaMCDiscAccount;
        this.qbUnDepositedFundsAccount = qbUnDepositedFundsAccount;
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

    public CreditCardAccount getQbAmexAccount() {
        return qbAmexAccount;
    }

    public CreditCardAccount getQbVisaMCDiscAccount() {
        return qbVisaMCDiscAccount;
    }

    public UnDepositedFundsAccount getQbUnDepositedFundsAccount() {
        return qbUnDepositedFundsAccount;
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
}