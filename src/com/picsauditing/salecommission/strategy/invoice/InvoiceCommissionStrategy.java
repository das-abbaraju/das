package com.picsauditing.salecommission.strategy.invoice;


/**
 * All implementations of the interface MUST return a Non-Null value
 * for the List<InvoiceCommission>, otherwise they risk NullPointerException.
 * 
 * @param <T> - Entity Type that is used for calculating the invoice commissions
 */
public interface InvoiceCommissionStrategy<T> {
	
	void processInvoiceCommission(T data);
	
}