package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum ClientSiteReferralStatus implements Translatable {
	Active, 
	ClosedSuccessful, 
	ClosedContactedSuccessful, 
	ClosedUnsuccessful;
	
	public boolean isActive() {
		return this == Active;
	}
	
	public boolean isClosedSuccessful() {
		return this == ClosedSuccessful;
	}
	
	public boolean isClosedContactedSuccessful() {
		return this == ClosedContactedSuccessful;
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
