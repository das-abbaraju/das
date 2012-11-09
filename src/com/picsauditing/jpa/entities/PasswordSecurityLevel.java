package com.picsauditing.jpa.entities;

import com.picsauditing.PICS.PasswordValidator;

public enum PasswordSecurityLevel {

    Normal(0, PasswordValidator.MINIMUM_LENGTH, false, false, 0, 0, 0),
    High(1, 6, true, false, 12, 3, 0),
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

    public boolean enforceHistory() {
        if (entriesOfHistoryToDisallow > 0) {
        	return true;
        }

        if (monthsOfHistoryToDisallow > 0) {
        	return true;
        }

        return false;
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
