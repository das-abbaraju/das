package com.picsauditing.quickbooks.model;

public enum UnDepositedFundsAccount {

    UNDEPOSITED_FUNDS_EURO("Undeposited Funds EURO"),
    UNDEPOSITED_FUNDS_CHF("Undeposited Funds CHF"),
    UNDEPOSITED_FUNDS("Undeposited Funds"),
    UNDEPOSITED_FUNDS_PLN("Undeposited Funds PLN");

    private String accountName;

    UnDepositedFundsAccount(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountName() {
        return accountName;
    }
}
