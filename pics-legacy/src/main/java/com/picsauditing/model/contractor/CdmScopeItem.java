package com.picsauditing.model.contractor;

public enum CdmScopeItem {

	PrincipalContractor("Principal Contractor", "PrincipalContractor"),
    Training("CDM Coordinator", "CDMCoordinator"),
    Designer("Designer", "Designer"),
    Contractor("Contractor", "Contractor");

	private final String displayValue;
	private final String dbValue;

	private CdmScopeItem(final String displayValue, final String dbValue) {
		this.displayValue = displayValue;
		this.dbValue = dbValue;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public String getDbValue() {
		return dbValue;
	}

	public static CdmScopeItem fromDbValue(final String dbValue) {
		for (CdmScopeItem value : CdmScopeItem.values()) {
			if (value.dbValue.equals(dbValue)) {
				return value;
			}
		}

		throw new IllegalArgumentException("Invalid dbValue: " + dbValue);
	}

	@Override
	public String toString() {
		return displayValue;
	}


}
