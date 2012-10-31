package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum AccountType implements Translatable {
	Contractor,Operator,Admin,Corporate,Assessment;

	public boolean isContractor() {
		return this.equals(Contractor);
	}

	public boolean isOperator() {
		return this.equals(Operator);
	}

	public boolean isAdmin() {
		return this.equals(Admin);
	}

	public boolean isCorporate() {
		return this.equals(Corporate);
	}

	public boolean isAssessment() {
		return this.equals(Assessment);
	}

	public boolean isOperatorCorporate() {
		return this.equals(Operator) || this.equals(Corporate);
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
	
	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}