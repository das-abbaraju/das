package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum LanguageStatus implements Translatable {
	Future, Alpha, Beta, Stable;

	public boolean isStable() {
		return this.equals(Stable);
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
