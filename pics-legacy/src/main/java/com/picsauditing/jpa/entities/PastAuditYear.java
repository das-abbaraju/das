package com.picsauditing.jpa.entities;

import java.util.Calendar;

public enum PastAuditYear {

    Any("Any Year", 0, 0),
	LastYearOnly("Last Year Only", 1, 1),
	TwoYearsAgo("Two Years Ago", 2, 2),
	ThreeYearsAgo("Three Years Ago", 3, 3);

	private String displayName;
	private int dbValue;
	private int yearsAgo;

	private PastAuditYear(String displayName, int dbValue, int yearsAgo) {
		this.displayName = displayName;
		this.dbValue = dbValue;
		this.yearsAgo = yearsAgo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getDbValue() {
		return dbValue;
	}

	public void setDbValue(int dbValue) {
		this.dbValue = dbValue;
	}

	public int getYearsAgo() {
		return yearsAgo;
	}

	public void setYearsAgo(int yearsAgo) {
		this.yearsAgo = yearsAgo;
	}

	public static PastAuditYear fromDbValue(int dbValue) {
        for (PastAuditYear pastAuditYear : PastAuditYear.values()) {
            if (pastAuditYear.dbValue == dbValue) {
                return pastAuditYear;
            }
        }

        return PastAuditYear.Any;
    }

	public int getYear() {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return currentYear - dbValue;
	}
}
