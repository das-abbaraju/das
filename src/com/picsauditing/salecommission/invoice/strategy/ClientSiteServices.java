package com.picsauditing.salecommission.invoice.strategy;

import com.picsauditing.jpa.entities.FeeClass;

public class ClientSiteServices {
	
	private int invoiceId;
	private int clientSiteId;
	private FeeClass feeClass;
	
	public int getInvoiceId() {
		return invoiceId;
	}
	
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	public int getClientSiteId() {
		return clientSiteId;
	}
	
	public void setClientSiteId(int clientSiteId) {
		this.clientSiteId = clientSiteId;
	}
	
	public FeeClass getFeeClass() {
		return feeClass;
	}
	
	public void setFeeClass(FeeClass feeClass) {
		this.feeClass = feeClass;
	}
	
}
