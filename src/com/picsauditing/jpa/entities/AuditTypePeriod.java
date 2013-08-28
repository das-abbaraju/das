package com.picsauditing.jpa.entities;

public enum AuditTypePeriod {
    None, Monthly, Quarterly, Yearly, CustomDate;

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

    public boolean isAnnual() {
        return Yearly == this || CustomDate == this;
    }

    public boolean isMonthlyQuarterlyAnnual() {
        return Monthly == this || Quarterly == this || Yearly == this || CustomDate == this;
    }

}
