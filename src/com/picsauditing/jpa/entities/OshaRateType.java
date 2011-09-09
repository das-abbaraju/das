package com.picsauditing.jpa.entities;

public enum OshaRateType implements Translatable {
	LwcrAbsolute,
	LwcrNaics,
	TrirAbsolute,
	TrirNaics,
	Fatalities,
	Dart,
	SeverityRate,
	Cad7,
	Neer;

	public String getDescriptionKey() {
		return getI18nKey("description");
	}
	
	@Override
	public String getI18nKey() {
		return this.toString();
	}
	
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}

}
