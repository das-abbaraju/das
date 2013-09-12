package com.picsauditing.jpa.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("I")
public class InvoiceItem extends TransactionItem {

	private Invoice invoice;
	private Date paymentExpires;
	private boolean returned = false;
	private String qbRefundID;

	public InvoiceItem() {
	}

	public InvoiceItem(InvoiceFee fee) {
		super();
		invoiceFee = fee;
		amount = fee.getAmount();
	}

	public InvoiceItem(InvoiceFee fee, BigDecimal amount, Date paymentExpires) {
		super();
		invoiceFee = fee;
		this.amount = amount;
		this.paymentExpires = fee.getFeeClass().isPaymentExpiresNeeded() ? paymentExpires : null;
	}

	@ManyToOne
	@JoinColumn(name = "invoiceID")
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}



	/**
	 * If the fee is for a contractor membership, the date the membership is
	 * valid until
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	public Date getPaymentExpires() {
		return paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	public boolean setReturned() {
		return returned;
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
	}

	public String getQbRefundID() {
		return qbRefundID;
	}

	public void setQbRefundID(String qbRefundID) {
		this.qbRefundID = qbRefundID;
	}
}
