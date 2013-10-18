package com.picsauditing.employeeguard.entities;

public enum IntervalType {
	DAY("Day", "Day"),
	WEEK("Week", "Week"),
	MONTH("Month", "Month"),
	YEAR("Year", "Year"),
	NO_EXPIRATION("No Expiration", "No Expiration"),
	NOT_APPLICABLE("Not Applicable", "Not Applicable");

	private String displayValue;
	private String dbValue;
	private static IntervalType[] displayableOptions = new IntervalType[]{DAY, WEEK, MONTH, YEAR};

	private IntervalType(String displayValue, String dbValue) {
		this.displayValue = displayValue;
		this.dbValue = dbValue;
	}

	public static IntervalType[] getDisplayableOptions() {
		return displayableOptions;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static IntervalType fromDbValue(final String dbValue) {
		for (IntervalType intervalType : IntervalType.values()) {
			if (intervalType.dbValue.equals(dbValue)) {
				return intervalType;
			}
		}

		throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
	}

	public boolean doesNotExpire() {
		return this == NO_EXPIRATION;
	}

    public boolean isApplicableExpiration() {
        return (this != NO_EXPIRATION && this != NOT_APPLICABLE);
    }

	@Override
	public String toString() {
		return displayValue;
	}

}
