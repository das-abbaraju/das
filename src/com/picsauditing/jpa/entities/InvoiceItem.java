package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("I")
public class InvoiceItem extends TransactionItem {

	private Invoice invoice;
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

	public boolean isRefunded() {
		return refunded;
	}

	public void setRefunded(boolean refunded) {
		this.refunded = refunded;
	}

	@Column(length = 25)
	public String getQbRefundID() {
		return qbRefundID;
	}

	public void setQbRefundID(String qbRefundID) {
		this.qbRefundID = qbRefundID;
	}
}
