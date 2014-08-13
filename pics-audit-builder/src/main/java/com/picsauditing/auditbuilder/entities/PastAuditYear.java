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

    public int getDbValue() {
        return dbValue;
    }

    public void setDbValue(int dbValue) {
        this.dbValue = dbValue;
    }

    public static PastAuditYear fromDbValue(int dbValue) {
        for (PastAuditYear pastAuditYear : PastAuditYear.values()) {
            if (pastAuditYear.dbValue == dbValue) {
                return pastAuditYear;
            }
        }

        return PastAuditYear.Any;
    }
}