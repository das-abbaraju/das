package com.picsauditing.actions.users;

public enum UserAccountRole {
	PICSSalesRep("Sales Representative"), 
	PICSAccountRep("Account Representative");

	private String description;

	UserAccountRole(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
