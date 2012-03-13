package com.picsauditing.jpa.entities;

public enum OshaRateType implements Translatable {
	LwcrAbsolute,
	LwcrNaics,
	TrirAbsolute,
	TrirNaics,
	TrirWIA,
	Fatalities,
	Dart,
	DartNaics,
	SeverityRate,
	Cad7,
	Neer,
	DaysAwayCases,
	DaysAway,
	JobTransfersCases,
	JobTransferDays,
	OtherRecordables,
	Hours,
	IFR,
	EMR,
	AFR,
	DOFR;

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
