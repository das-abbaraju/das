package com.picsauditing.employeeguard.services.status;

import com.picsauditing.jpa.entities.JSONable;
import org.json.simple.JSONObject;

// TODO: Rename so it is more generic since status is now applied to more than just skills
public enum SkillStatus implements JSONable {

    // TODO: Rename expiring to something more meaningful that the Skill is about to expire

    // Keep in order of severity, from highest severity to lowest
    Expired("expired"),    //-- Lowest Ordinal
    Expiring("expiring"),
    Pending("pending"),
    Completed("complete"); //-- Highest Ordinal

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
