package com.picsauditing.jpa.entities;

public enum ContractorOperatorNumberType implements Translatable {
	SAP,
	CRM,
	LMS,
	Badging,
	Other;
	
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
	
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
