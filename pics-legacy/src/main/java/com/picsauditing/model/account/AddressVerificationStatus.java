package com.picsauditing.model.account;

public enum AddressVerificationStatus {
    PASSED_VALIDATION("Passed Validation", "PASSED_VALIDATION", "Attempted to validate and succeeded"),
    AWAITING_VALIDATION("Awaiting Validation", "AWAITING_VALIDATION", "Has not been sent to the validator yet"),
    FAILED_VALIDATION("Failed Validation", "FAILED_VALIDATION", "Attempted to validate and failed");

    private final String displayValue;
    private final String dbValue;
    private final String description;

    private AddressVerificationStatus(final String displayValue, final String dbValue, final String description) {
        this.displayValue = displayValue;
        this.dbValue = dbValue;
        this.description = description;
    }

    public static AddressVerificationStatus fromDbValue(final String dbValue) {
        for (AddressVerificationStatus addressVerificationStatus : AddressVerificationStatus.values()) {
            if (addressVerificationStatus.dbValue.equals(dbValue)) {
                return addressVerificationStatus;
            }
        }

        throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
    }

    @Override
    public String toString() {
        return displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getDescription() {
        return description;
    }
}

