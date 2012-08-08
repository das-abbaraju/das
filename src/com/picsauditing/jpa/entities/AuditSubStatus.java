package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum  AuditSubStatus implements Translatable {
	LIMITS_NOT_MET, NO_ADDITIONAL_INSURED, NO_WAVER_OF_SUBROGATION;
	
	public boolean isLimitsNotMet() {
		return this.equals(LIMITS_NOT_MET);
	}

	public boolean isNoAdditionalInsured() {
		return this.equals(NO_ADDITIONAL_INSURED);
	}

	public boolean isNoWaverOfSubrogation() {
		return this.equals(NO_WAVER_OF_SUBROGATION);
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
