package com.picsauditing.quickbooks.model;

public enum CreditCard {

    VISA("Visa"),
    MASTERCARD("Mastercard"),
    DISCOVER("Discover"),
    AMEX("American Express");

    private String cardName;

    CreditCard(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    public static CreditCard fromName(String cardName) throws Exception {
        try {
            return CreditCard.valueOf(cardName);
        } catch (IllegalArgumentException e) {
            throw new Exception("No CreditCard by this name: " + cardName, e);
        }
    }

    public boolean isVisaMCDiscover() {
        return this.equals(VISA) || this.equals(MASTERCARD) || this.equals(DISCOVER);
    }
}

