package com.picsauditing.jpa.entities;

public enum OshaType implements Translatable {
	OSHA, MSHA, COHS, UK_HSE;
	
	@Override
	public String getI18nKey() {
		return this.toString();
	}
	
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
