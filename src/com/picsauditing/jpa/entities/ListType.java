package com.picsauditing.jpa.entities;

public enum ListType {
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
}
