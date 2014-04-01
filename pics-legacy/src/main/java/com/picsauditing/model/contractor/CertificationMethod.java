package com.picsauditing.model.contractor;

public enum CertificationMethod {
    INTERNAL("Internal", "INTERNAL", "PICS Manual Audit SSiP Approval"),
    DEEMS_TO_SATISFY("Deems To Satisfy", "DEEMS_TO_SATISFY", "SSiP Forum Deemed to Satisfy");

    private final String displayValue;
    private final String dbValue;
    private final String description;

    private CertificationMethod(final String displayValue, final String dbValue, final String description) {
        this.displayValue = displayValue;
        this.dbValue = dbValue;
        this.description = description;
    }

    public static CertificationMethod fromDbValue(final String dbValue) {
        for (CertificationMethod certificationMethod : CertificationMethod.values()) {
            if (certificationMethod.dbValue.equals(dbValue)) {
                return certificationMethod;
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
