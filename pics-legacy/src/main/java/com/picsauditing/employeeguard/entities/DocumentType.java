package com.picsauditing.employeeguard.entities;

public enum DocumentType {

    Certificate("Certificate", "Certificate"),
    Photo("Photo", "Photo");

    private String displayValue;
    private String dbValue;

    private DocumentType(String displayValue, String dbValue) {
        this.displayValue = displayValue;
        this.dbValue = dbValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static DocumentType fromDbValue(final String dbValue) {
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.dbValue.equals(dbValue)) {
                return documentType;
            }
        }

        throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
    }

    @Override
    public String toString() {
        return displayValue;
    }
}
