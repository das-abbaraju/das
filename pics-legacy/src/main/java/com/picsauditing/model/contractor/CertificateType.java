package com.picsauditing.model.contractor;

public enum CertificateType {
    SSIP("SSIP", "SSIP");

    private final String displayValue;
    private final String dbValue;

    private CertificateType(final String displayValue, final String dbValue) {
        this.displayValue = displayValue;
        this.dbValue = dbValue;
    }

    public static CertificateType fromDbValue(final String dbValue) {
        for (CertificateType certificateType : CertificateType.values()) {
            if (certificateType.dbValue.equals(dbValue)) {
                return certificateType;
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
