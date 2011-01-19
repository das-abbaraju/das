package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_item")
public class InvoiceItem extends BaseTable {

	private Invoice invoice;
	private InvoiceFee invoiceFee;
	private BigDecimal amount = BigDecimal.ZERO;
	private String description;
	private Date paymentExpires;
	private boolean refunded = false;
	private String qbRefundID;
	
	public InvoiceItem() {
	}

	public InvoiceItem(InvoiceFee fee) {
		super();
		invoiceFee = fee;
		amount = fee.getAmount();
	}

	public InvoiceItem(InvoiceFee fee, Date paymentExpires) {
		this(fee);
		this.paymentExpires = paymentExpires;
	}

	@ManyToOne
	@JoinColumn(name = "invoiceID")
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@ManyToOne
	@JoinColumn(name = "feeID")
	public InvoiceFee getInvoiceFee() {
		return invoiceFee;
	}

	public void setInvoiceFee(InvoiceFee invoiceFee) {
		this.invoiceFee = invoiceFee;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(length=100)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * If the fee is for a contractor membership, the date the membership is valid until
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	public Date getPaymentExpires() {
		return paymentExpires;
	}

	public void setPaymentExpires(Date paymentExpires) {
		this.paymentExpires = paymentExpires;
	}

	public boolean isRefunded() {
		return refunded;
	}

	public void setRefunded(boolean refunded) {
		this.refunded = refunded;
	}

	@Column(length=25)
	public String getQbRefundID() {
		return qbRefundID;
	}

	public void setQbRefundID(String qbRefundID) {
		this.qbRefundID = qbRefundID;
	}
}
