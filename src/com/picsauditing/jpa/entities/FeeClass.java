package com.picsauditing.jpa.entities;

public enum FeeClass {
	// TODO combine some of these fees
	Deprecated, Free, BidOnly, ListOnly, DocuGUARD, InsureGUARD, AuditGUARD, EmployeeGUARD, Activation, Reactivation, LateFee, ReschedulingFee, ScanningFee, WebcamFee, ExpediteFee, ImportFee, SuncorDiscount, GST, Misc;

	public boolean isPaymentExpiresNeeded() {
		return this == BidOnly || this == ListOnly || this == DocuGUARD || this == Activation || this == Reactivation;
	}

	public boolean isMembership() {
		return this == BidOnly || this == ListOnly || this == DocuGUARD || this == AuditGUARD || this == EmployeeGUARD
				|| this == InsureGUARD;
	}
}
