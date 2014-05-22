package com.picsauditing.employeeguard.services.status;

// TODO this is pretty much a copy of SkillStatus
public enum DocumentStatus {
	Expired("expired"),
	Expiring("expiring"),
	Pending("pending"),
	Complete("complete");

	private String displayValue;

	private DocumentStatus(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}
}