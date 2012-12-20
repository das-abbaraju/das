package com.picsauditing.salecommission.invoice.strategy;

import java.util.Date;

import com.picsauditing.jpa.entities.Invoice;

public class ContractorInvoiceState {

	private Invoice invoice;
	private Date paymentExpiresDate;
	private boolean activation;
	private boolean reactivation;
	private boolean renewal;
	private boolean upgrade;

	public Invoice getInvoice() {
		return invoice;
	}

	protected void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Date getPaymentExpiresDate() {
		return paymentExpiresDate;
	}

	protected void setPaymentExpiresDate(Date paymentExpiresDate) {
		this.paymentExpiresDate = paymentExpiresDate;
	}

	public boolean isActivation() {
		return activation;
	}

	protected void setActivation(boolean activation) {
		this.activation = activation;
	}

	public boolean isReactivation() {
		return reactivation;
	}

	protected void setReactivation(boolean reactivation) {
		this.reactivation = reactivation;
	}

	public boolean isRenewal() {
		return renewal;
	}

	protected void setRenewal(boolean renewal) {
		this.renewal = renewal;
	}

	public boolean isUpgrade() {
		return upgrade;
	}

	protected void setUpgrade(boolean upgrade) {
		this.upgrade = upgrade;
	}

}
