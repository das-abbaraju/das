package com.picsauditing.jpa.entities;

public enum FlagCriteriaCategory implements Translatable {
    Audits,
    Insurance,
    InsuranceAMBClass,
    InsuranceAMBRating,
    InsuranceCriteria,
    Paperwork,
    Safety,
    Statistics;

    public boolean isAMBest() {
        return this == InsuranceAMBClass || this == InsuranceAMBRating;
    }

    @Override
    public String getI18nKey() {
        return "FlagCriteria.Category." + toString();
    }

    @Override
    public String getI18nKey(String property) {
        return getI18nKey() + "." + property;
    }

}
