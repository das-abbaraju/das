package com.picsauditing.quickbooks.model;

public enum CreditCardAccount {

    VISA_MC_DISC_MERCHANT_ACCOUNT("VISA/MC/DISC Merchant Account"),
    VISA_MC_DISC_MERCHANT_ACCT_EURO("VISA/MC/DISC Merchant Acct EURO"),
    VISA_CHF("Visa CHF"),
    VISA_PLN("Visa PLN"),
    AMEX_MERCHANT_ACCOUNT("Amex Merchant Account"),
    AMEX_MERCHANT_ACCOUNT_EURO("AMEX Merchant Account EURO"),
    NONE("None");

    private String accountName;

    CreditCardAccount(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }
}

