package com.picsauditing.PICS.data;

import com.picsauditing.jpa.entities.Payment;

public class PaymentDataEvent extends DataEvent<Payment> {
	
	// TODO: See if refund could be deprecated	
	public enum PaymentEventType {
		REFUND, PAYMENT, REMOVE, SAVE
	}
	
	private PaymentEventType eventType;
	
	public PaymentDataEvent(Payment data, PaymentEventType eventType) {
		super(data);
		this.eventType = eventType;
	}
	
	public PaymentEventType getPaymentEventType() {
		return eventType;
	}
	
}