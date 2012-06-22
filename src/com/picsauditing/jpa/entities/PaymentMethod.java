package com.picsauditing.jpa.entities;

public enum PaymentMethod {
	CreditCard("Credit Card"), Check("Check"), Refund("Refund"), EFT("EFT");  // EFT is for wiring the money 

	private String description;

	PaymentMethod(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public boolean isCreditCard() {
		return this.equals(CreditCard);
	}

	public boolean isCheck() {
		return this.equals(Check);
	}
	
	public boolean isEFT() {
		return this.equals(EFT);
	}
}
