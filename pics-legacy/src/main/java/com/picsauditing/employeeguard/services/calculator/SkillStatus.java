package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.jpa.entities.JSONable;
import org.json.simple.JSONObject;

public enum SkillStatus implements JSONable {

    // TODO: Rename expiring to something more meaningful that the Skill is about to expire

    // Keep in order of severity, from highest severity to lowest
    Expired("expired"),
    Expiring("expiring"),
    Pending("pending"),
    Completed("complete");

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

    public boolean isCompleted() {
        return this == Completed;
    }

    public String getDisplayValue() {
        return displayValue;
    }

	@Override
	public JSONObject toJSON(boolean full) {
		return new JSONObject();
	}

	@Override
	public void fromJSON(JSONObject o) {

	}
}
