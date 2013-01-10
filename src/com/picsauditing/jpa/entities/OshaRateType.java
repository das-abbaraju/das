package com.picsauditing.jpa.entities;

public enum OshaRateType implements Translatable {
	LwcrAbsolute(true),
	LwcrNaics,
	TrirAbsolute(true),
	TrirNaics,
	TrirWIA,
	Fatalities,
	Dart(true),
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
	FileUpload,
	IFR,
	EMR,
	AFR,
	DOFR,
	LTIFR;

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
	
	public boolean isTrir() {
		if (this == TrirAbsolute || this == TrirNaics || this == TrirWIA)
			return true;
		
		return false;
	}
	
	public boolean isDart() {
		if (this == Dart || this == DartNaics)
			return true;
		
		return false;
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
