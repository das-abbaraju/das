package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

@Deprecated
public enum ContractorRegistrationRequestStatus implements Translatable {
	Active, 
	Hold, 
	ClosedContactedSuccessful, 
	ClosedSuccessful, 
	ClosedUnsuccessful;
	
	public boolean isActive() {
		return this == Active;
	}

	public boolean isHold() {
		return this == Hold;
	}

	public boolean isClosedContactedSuccessful() {
		return this == ClosedContactedSuccessful;
	}

	public boolean isClosedSuccessful() {
		return this == ClosedSuccessful;
	}

	public boolean isClosedUnsuccessful() {
		return this == ClosedUnsuccessful;
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
