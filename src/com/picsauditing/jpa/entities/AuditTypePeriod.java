package com.picsauditing.jpa.entities;

public enum AuditTypePeriod {
    None, Monthly, Quarterly, Yearly, CustomDate, Membership, Expiration;

    public boolean isNone() {
        return None == this;
    }

    public boolean isMonthly() {
        return Monthly == this;
    }

    public boolean isQuarterly() {
        return Quarterly == this;
    }

    public boolean isYearly() {
        return Yearly == this;
    }

    public boolean isCustomDate() {
        return CustomDate == this;
    }

    public boolean isMembership() {
        return Membership == this;
    }

    public boolean isExpiration() {
        return Expiration == this;
    }

    public boolean isMonthlyQuarterlyYearly() {
        return Monthly == this || Quarterly == this || Yearly == this || CustomDate == this;
    }

}
