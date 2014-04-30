package com.picsauditing.flagcalculator.entities;

public enum AccountLevel {
    BidOnly, ListOnly, Full;

    public boolean isBidOnly() {
        return this.equals(BidOnly);
    }

    public boolean isFull() {
        return this.equals(Full);
    }
}
