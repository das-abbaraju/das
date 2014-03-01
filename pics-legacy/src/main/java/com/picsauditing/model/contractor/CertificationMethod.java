package com.picsauditing.model.contractor;

public enum CertificationMethod {
    INTERNAL("Internal", "INTERNAL"),
    DEEMS_TO_SATISFY("Deems To Satisfy", "DEEMS_TO_SATISFY");

    private final String displayValue;
    private final String dbValue;

    private CertificationMethod(final String displayValue, final String dbValue) {
        this.displayValue = displayValue;
        this.dbValue = dbValue;
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
}
