package com.picsauditing.jpa.entities;

public enum ListType implements Translatable {
	ALL("Any List"),
	Contractor("Contractors"),
	Audit("Contractors by Audit"),
	User("Users"),
	ContractorOperator("Contractors by Operator"),
	Operator("Operators"),
	Invoice("Invoices")
	;
	
	private String description;
	private ListType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
	
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
