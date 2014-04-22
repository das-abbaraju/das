package com.picsauditing.flagcalculator.entities;

public enum FlagCriteriaCategory {
    Audits,
    Insurance,
    InsuranceAMBClass,
    InsuranceAMBRating,
    InsuranceCriteria,
    Paperwork,
    Safety,
    Statistics,
    ClientReviews;

    public boolean isAMBest() {
        return this == InsuranceAMBClass || this == InsuranceAMBRating;
    }
}
