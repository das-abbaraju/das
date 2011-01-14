package com.picsauditing.actions.users;


public enum UserAccountRole {
	PICSSalesRep("Sales Representative"), 
	PICSAccountRep("Account Manager");

	private String description;

	UserAccountRole(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	static public String getDesc(UserAccountRole role) {
		for (UserAccountRole value : UserAccountRole.values()) {
			if (value.equals(role))
				return value.description;
		}
		return "";
	}
	
	public boolean isAccountManager() {
		return this == PICSAccountRep;
	}
	
	public boolean isSalesRep() {
		return this == PICSSalesRep;
	}
}
