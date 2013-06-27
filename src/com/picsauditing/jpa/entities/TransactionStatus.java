package com.picsauditing.jpa.entities;

public enum TransactionStatus {
	Paid, Unpaid, Void, BadDebt;

	public boolean isPaid() {
		return this == Paid;
	}

	public boolean isUnpaid() {
		return this == Unpaid;
	}

	public boolean isVoid() {
		return this == Void;
	}
}
