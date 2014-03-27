package com.picsauditing.jpa.entities;

public enum InvoiceType {
    Activation,Reactivation,Upgrade,Upgrade_Service,Upgrade_Tier,Renewal,LateFee,OtherFees;

    public boolean isMembershipType() {
        return this == Activation || this == Reactivation || this == Upgrade || this == Upgrade_Service || this == Upgrade_Tier || this == Renewal;
    }

    public boolean isUpgrade() {
        return this == Upgrade || this == Upgrade_Service || this == Upgrade_Tier;
    }
}
