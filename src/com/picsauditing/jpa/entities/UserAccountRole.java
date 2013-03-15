package com.picsauditing.jpa.entities;

import com.picsauditing.util.Strings;

public enum UserAccountRole implements Translatable {
	PICSSalesRep("Sales Representative"), 
	PICSAccountRep("Account Manager"),
	PICSInsideSalesRep("Inside Sales Representative"),
	PICSCustomerServiceRep("Customer Service Representative");

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
		
		return Strings.EMPTY_STRING;
	}
	
	public boolean isAccountManager() {
		return this == PICSAccountRep;
	}
	
	public boolean isSalesRep() {
		return this == PICSSalesRep;
	}

    public boolean isInsideSalesRep() {
        return this == PICSInsideSalesRep;
    }

    public boolean isCustomerServiceRep() {
        return this == PICSCustomerServiceRep;
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
