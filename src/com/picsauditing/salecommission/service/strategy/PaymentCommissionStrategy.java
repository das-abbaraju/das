package com.picsauditing.salecommission.service.strategy;

import java.util.List;

import com.picsauditing.jpa.entities.PaymentCommission;

/**
 * All implementations of the interface MUST return a Non-Null value
 * for the List<PaymentCommission>, otherwise they risk NullPointerException.
 * 
 * @param <T> - Entity Type that is used for calculating the payment commissions
 */

public interface PaymentCommissionStrategy<T> {

	public List<PaymentCommission> calculatePaymentCommission(T data);
	
}
