package com.picsauditing.salecommission.payment.strategy;


/**
 * All implementations of the interface MUST return a Non-Null value
 * for the List<PaymentCommission>, otherwise they risk NullPointerException.
 * 
 * @param <T> - Entity Type that is used for calculating the payment commissions
 */

public interface PaymentCommissionStrategy<T> {
	
	void processPaymentCommission(T data);
	
}