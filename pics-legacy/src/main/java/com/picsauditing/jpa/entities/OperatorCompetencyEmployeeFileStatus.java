package com.picsauditing.jpa.entities;

public enum OperatorCompetencyEmployeeFileStatus implements Translatable {
	NEEDED, PROVIDED, NA;

	public boolean isNeeded() {
		return this == NEEDED;
	}

	public boolean isProvided() {
		return this == PROVIDED;
	}

	public boolean isNotApplicable() {
		return this == NA;
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
