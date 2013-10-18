package com.picsauditing.employeeguard.entities;

public enum RuleType {
	
	REQUIRED("Required", "Required"),
	OPTIONAL("Optional", "Optional");

	private final String displayValue;
	private final String dbValue;

	private RuleType(final String displayValue, final String dbValue) {
		this.displayValue = displayValue;
		this.dbValue = dbValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static RuleType fromDbValue(final String dbValue) {
		for (RuleType ruleType : RuleType.values()) {
			if (ruleType.dbValue.equals(dbValue)) {
				return ruleType;
			}
		}

		throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
	}

	@Override
	public String toString() {
		return displayValue;
	}

	public boolean isRequired() {
		return this == REQUIRED;
	}
}
