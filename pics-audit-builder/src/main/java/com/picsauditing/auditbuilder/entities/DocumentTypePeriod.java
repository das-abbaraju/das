package com.picsauditing.auditbuilder.entities;

public enum DocumentTypePeriod {
    None, Monthly, Quarterly, Yearly, CustomDate;

    public boolean isMonthly() {
        return Monthly == this;
    }

    public boolean isQuarterly() {
        return Quarterly == this;
    }

    public boolean isYearly() {
        return Yearly == this;
    }

    public boolean isYearlyCustomDate() {
        return Yearly == this || CustomDate == this;
    }

    public boolean isMonthlyQuarterlyAnnual() {
        return Monthly == this || Quarterly == this || Yearly == this || CustomDate == this;
    }
}