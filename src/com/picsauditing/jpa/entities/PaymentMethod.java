package com.picsauditing.jpa.entities;

public enum PaymentMethod {
	CreditCard("Credit Card"),
	Check("Check");
	
	private String description;
	
	PaymentMethod(String description) {
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
	public boolean isCreditCard() {
		return this.equals(CreditCard);
	}
}
