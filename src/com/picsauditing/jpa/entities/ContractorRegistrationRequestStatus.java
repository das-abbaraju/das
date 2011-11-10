package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum ContractorRegistrationRequestStatus implements Translatable {
	Active, 
	Hold, 
	ClosedContactedSuccessful, 
	ClosedSuccessful, 
	ClosedUnsuccessful;
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
