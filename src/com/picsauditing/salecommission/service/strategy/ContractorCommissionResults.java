package com.picsauditing.salecommission.service.strategy;

import java.util.List;

import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.jpa.entities.PaymentCommission;

public class ContractorCommissionResults {
	
	private List<InvoiceCommission> invoiceCommissions;
	private List<PaymentCommission> paymentCommissions;
	
	public List<InvoiceCommission> getInvoiceCommissions() {
		return invoiceCommissions;
	}
	
	public void setInvoiceCommissions(List<InvoiceCommission> invoiceCommissions) {
		this.invoiceCommissions = invoiceCommissions;
	}
	
	public List<PaymentCommission> getPaymentCommissions() {
		return paymentCommissions;
	}
	
	public void setPaymentCommissions(List<PaymentCommission> paymentCommissions) {
		this.paymentCommissions = paymentCommissions;
	}
	
}
