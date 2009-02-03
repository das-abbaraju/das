package com.picsauditing.jpa.entities;

public enum PaymentMethod {
	CreditCard("CreditCard"),
	Check("Check");
	
	private String description;
	
	PaymentMethod(String description) {
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
}
