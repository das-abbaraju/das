package com.picsauditing.auditbuilder.entities;

import java.util.Calendar;

public enum PastAuditYear {
    Any(0),
	LastYearOnly(1),
	TwoYearsAgo(2),
	ThreeYearsAgo(3);

	private int dbValue;

	private PastAuditYear(int dbValue) {
		this.dbValue = dbValue;
	}
	public int getYear() {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return currentYear - dbValue;
	}
}