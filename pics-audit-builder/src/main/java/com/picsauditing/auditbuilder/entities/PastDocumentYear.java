package com.picsauditing.auditbuilder.entities;

import java.util.Calendar;

public enum PastDocumentYear {
    Any(0),
	LastYearOnly(1),
	TwoYearsAgo(2),
	ThreeYearsAgo(3);

	private int dbValue;

	private PastDocumentYear(int dbValue) {
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

    public static PastDocumentYear fromDbValue(int dbValue) {
        for (PastDocumentYear pastDocumentYear : PastDocumentYear.values()) {
            if (pastDocumentYear.dbValue == dbValue) {
                return pastDocumentYear;
            }
        }

        return PastDocumentYear.Any;
    }
}