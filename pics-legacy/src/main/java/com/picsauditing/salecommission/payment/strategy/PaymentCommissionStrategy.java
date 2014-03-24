package com.picsauditing.salecommission.payment.strategy;


import com.picsauditing.PICS.data.PaymentDataEvent;

public interface PaymentCommissionStrategy<T> {
	
	void processPaymentCommission(T data, PaymentDataEvent.PaymentEventType eventType);
	
}