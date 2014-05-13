package com.picsauditing.flagcalculator;

public interface FlagData {
    boolean isInsurance();
    String getCriteriaCategory();
    String getCriteriaLabel();
    String getFlagColor();
    int getCriteriaID();
}
