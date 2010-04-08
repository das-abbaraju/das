package com.picsauditing.jpa.entities;

public enum EmployeeClassification {
	FullTime("Full Time"),
	PartTime("Part Time"),
	Contract("Contract");

	private String description;

	private EmployeeClassification(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
