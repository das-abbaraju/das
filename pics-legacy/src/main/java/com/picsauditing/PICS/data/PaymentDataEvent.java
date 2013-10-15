package com.picsauditing.PICS.data;

import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;

public class PaymentDataEvent extends DataEvent<PaymentApplied> {
	
	// TODO: See if refund could be deprecated	
	public enum PaymentEventType {
		REFUND, PAYMENT, REMOVE, SAVE
	}
	
	private PaymentEventType eventType;
	
	public PaymentDataEvent(PaymentApplied data, PaymentEventType eventType) {
		super(data);
		this.eventType = eventType;
	}
	
	public PaymentEventType getPaymentEventType() {
		return eventType;
	}
	
}