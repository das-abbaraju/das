package com.picsauditing.employeeguard.entities;

import com.picsauditing.jpa.entities.Translatable;

public enum PositionType implements Translatable {

	FullTime("FullTime", "Full Time"),
	PartTime("PartTime", "Part Time"),
	Contract("Contract", "Contract");

	private final String displayName;
	private final String dbValue;

	private PositionType(final String dbValue, final String description) {
		this.displayName = description;
		this.dbValue = dbValue;
	}

	public String getDisplayValue() {
		return displayName;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static PositionType fromDbValue(final String dbValue) {
		for (PositionType positionType : PositionType.values()) {
			if (positionType.dbValue.equals(dbValue)) {
				return positionType;
			}
		}

		throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

	@Override
	public String toString() {
		return displayName;
	}

}
