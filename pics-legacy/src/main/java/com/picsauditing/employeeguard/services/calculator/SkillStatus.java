package com.picsauditing.employeeguard.services.calculator;

public enum SkillStatus {

    // TODO: Rename expiring to something more meaningful that the Skill is about to expire

    // Keep in order of serverity, from highest severity to lowest
    Expired("expired"),
    Expiring("expiring"),
    Pending("pending"),
    Complete("complete");

    private String displayValue;

    private SkillStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public boolean isExpired() {
        return this == Expired;
    }

    public boolean isExpiring() {
        return this == Expiring;
    }

    public boolean isPending() {
        return this == Pending;
    }

    public boolean isComplete() {
        return this == Complete;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
