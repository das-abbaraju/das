package com.picsauditing.PICS.data;

import com.picsauditing.jpa.entities.Payment;

public class PaymentDataEvent extends DataEvent<Payment> {
	
	public enum PaymentEventType {
		SAVE, REFUND, PAYMENT, VOID, REMOVE
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
