package com.picsauditing.jpa.entities;

public enum EmployeeClassification implements Translatable {
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
	
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
	
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}