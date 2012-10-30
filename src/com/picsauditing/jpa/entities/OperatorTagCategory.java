package com.picsauditing.jpa.entities;

public enum OperatorTagCategory implements Translatable {
	None, CompetencyReview, OperatorQualification, OtherEmployeeGUARD;

	public boolean isCompetencyReview() {
		return this == CompetencyReview;
	}

	public boolean isOperatorQualification() {
		return this == OperatorQualification;
	}

	/**
	 * This is for Competency Reviews (not related to HSE/Shell) or Training
	 * Verifications
	 */
	public boolean isOtherEmployeeGUARD() {
		return this == OtherEmployeeGUARD;
	}

	public boolean isEmployeeGUARD() {
		return this == CompetencyReview || this == OperatorQualification || this == OtherEmployeeGUARD;
	}

	@Override
	public String getI18nKey() {
		return String.format("%s.%s", this.getClass().getSimpleName(), this.toString());
	}

	@Override
	public String getI18nKey(String property) {
		return String.format("%s.%s", getI18nKey(), property);
	}
}
