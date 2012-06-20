package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum NoteCategory implements Translatable {
	General,
	Audits,
	Billing,
	Flags,
	Insurance,
	OperatorChanges,
	Other,
	OperatorQualification,
	Employee,
	RiskRanking,
	Registration;
	
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
