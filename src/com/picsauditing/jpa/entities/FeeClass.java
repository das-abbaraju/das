package com.picsauditing.jpa.entities;

public enum FeeClass {
	Deprecated, LateFee, ReschedulingFee, ScanningFee, WebcamFee, ExpediteFee, ImportFee, GST, Free, Activation, Reactivation, ListOnly, DocuGUARD, AuditGUARD, InsureGUARD, EmployeeGUARD, Misc;

	public boolean isPaymentExpiresNeeded() {
		return this == ListOnly || this == DocuGUARD;
	}
}
