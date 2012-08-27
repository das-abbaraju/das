package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum  AuditSubStatus implements Translatable {
	NoValidCertificate, LimitsNotMet, NoAdditionalInsured, NoWaiverOfSubrogation;
	
	public boolean isNoValidCertificate() {
		return this == NoValidCertificate;
	}
	
	public boolean isLimitsNotMet() {
		return this == LimitsNotMet;
	}

	public boolean isNoAdditionalInsured() {
		return this == NoAdditionalInsured;
	}

	public boolean isNoWaverOfSubrogation() {
		return this == NoWaiverOfSubrogation;
	}

	@Transient
	@Override
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

}
