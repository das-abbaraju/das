package com.picsauditing.salecommission.service.strategy;

import java.util.List;

import com.picsauditing.jpa.entities.InvoiceCommission;

/**
 * All implementations of the interface MUST return a Non-Null value
 * for the List<InvoiceCommission>, otherwise they risk NullPointerException.
 * 
 * @param <T> - Entity Type that is used for calculating the invoice commissions
 */
public interface InvoiceCommissionStrategy<T> {
	
	public List<InvoiceCommission> calculateInvoiceCommission(T data);
	
}
