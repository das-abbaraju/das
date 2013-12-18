package com.picsauditing.jpa.entities;

public enum InvoiceType {
    Activation,Upgrade,Renewal,LateFee,OtherFees;

    public boolean isMembershipType() {
        return this == Activation || this == Upgrade || this == Renewal;
    }
}
