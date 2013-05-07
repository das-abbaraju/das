package com.picsauditing.jpa.entities;

public enum OperatorCompetencyCourseType implements Translatable {
	REQUIRES_DOCUMENTATION;

	public boolean isRequiresDocumentation() {
		return this == REQUIRES_DOCUMENTATION;
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
