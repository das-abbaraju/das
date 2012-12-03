package com.picsauditing.jpa.entities;

public enum PasswordSecurityLevel {

    Normal(0, 5, false, false, 0, 0, 0),
    High(1, 7, false, true, 3, 0, 4),
    Maximum(2, 8, true, true, 3, 12, 0);

    public int dbValue;
    public int minLength;
    public boolean enforceMixedCase;
    public boolean enforceSpecialCharacter;
    public int expirationMonths;
    public int monthsOfHistoryToDisallow;
    public int entriesOfHistoryToDisallow;

    private PasswordSecurityLevel(int dbValue, int minLength, boolean enforceMixedCase, boolean enforceSpecialCharacter, int expirationMonths, int monthsOfHistoryToDisallow, int entriesOfHistoryToDisallow) {
        this.dbValue = dbValue;
        this.minLength = minLength;
        this.enforceMixedCase = enforceMixedCase;
        this.enforceSpecialCharacter = enforceSpecialCharacter;
        this.expirationMonths = expirationMonths;
        this.monthsOfHistoryToDisallow = monthsOfHistoryToDisallow;
        this.entriesOfHistoryToDisallow = entriesOfHistoryToDisallow;
    }

	public boolean enforcePasswordExpiration() {
		return expirationMonths > 0;
	}

	public boolean enforceEntriesOfHistory() {
		return entriesOfHistoryToDisallow > 0;
	}

	public boolean enforceMonthsOfHistory() {
		return monthsOfHistoryToDisallow > 0;
	}

	public boolean enforceHistory() {
		return (enforceEntriesOfHistory() || enforceMonthsOfHistory());
	}

    public static PasswordSecurityLevel fromDbValue(int passwordSecurityLevel) {
        for (PasswordSecurityLevel securityLevel : PasswordSecurityLevel.values()) {
            if (securityLevel.dbValue == passwordSecurityLevel) {
                return securityLevel;
            }
        }

        return PasswordSecurityLevel.Normal;
    }
}
