package com.picsauditing.jpa.entities;

public enum OshaRateType implements Translatable {
	LwcrAbsolute(true),
	LwcrNaics,
	TrirAbsolute(true),
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

	private boolean hasIndustryAverage;

	private OshaRateType() {
		this.hasIndustryAverage = false;
	}
	
	private OshaRateType(boolean hasIndustryAverage) {
		this.hasIndustryAverage = hasIndustryAverage;
	}

	public boolean isHasIndustryAverage() {
		return hasIndustryAverage;
	}

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
