package com.picsauditing.employeeguard.entities;

public enum SkillType {

	Certification("Certification", "Certification"),
	Training("Training", "Training");

	private final String displayValue;
	private final String dbValue;

	private SkillType(final String displayValue, final String dbValue) {
		this.displayValue = displayValue;
		this.dbValue = dbValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static SkillType fromDbValue(final String dbValue) {
		for (SkillType skillType : SkillType.values()) {
			if (skillType.dbValue.equals(dbValue)) {
				return skillType;
			}
		}

		throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
	}

	public boolean isTraining() {
		return this == Training;
	}

    public boolean isCertification() {
        return this == Certification;
    }

	@Override
	public String toString() {
		return displayValue;
	}


}
