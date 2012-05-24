package com.picsauditing.oshadisplay;

import com.picsauditing.jpa.entities.OshaRateType;


public class StatisticsDisplayRow extends OshaDisplayRow{
	private OshaRateType oshaRateType;

	@Override
	public String getTitle() {
		return oshaRateType.getI18nKey();
	}
	
	public OshaRateType getOshaRateType() {
		return oshaRateType;
	}

	public void setOshaRateType(OshaRateType oshaRateType) {
		this.oshaRateType = oshaRateType;
	}

	@Override
	public boolean isHurdleRate() {
		return false;
	}
}
