package com.picsauditing.jpa.entities;

public enum FeeClass {
	Deprecated, Free, ListOnly, DocuGUARD, InsureGUARD, AuditGUARD, EmployeeGUARD, Activation, Reactivation, LateFee, ReschedulingFee, ScanningFee, WebcamFee, ExpediteFee, ImportFee, GST, Misc;

	public boolean isPaymentExpiresNeeded() {
		return this == ListOnly || this == DocuGUARD || this == Activation || this == Reactivation;
	}
}
