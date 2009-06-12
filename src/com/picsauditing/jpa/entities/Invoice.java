package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(value = "I")
public class Invoice extends Transaction {
	public final static int daysUntilDue = 30;

	protected TransactionType txnType;
	private Date dueDate;
	private String poNumber;
	private String notes;
	private Date paidDate; // MAX(Payment.creationDate)
	@Deprecated
	private PaymentMethod paymentMethod;
	@Deprecated
	private String checkNumber;
	@Deprecated
	private String transactionID;
	@Deprecated
	private String ccNumber;
	
	private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
	private List<InvoicePayment> payments = new ArrayList<InvoicePayment>();

	@Transient
	public boolean isOverdue() {
		if (totalAmount.compareTo(BigDecimal.ZERO) <= 0)
			return false;

		if (getStatus().isPaid())
			return false;

		if (dueDate == null)
			return false;

		return dueDate.before(new Date());
	}

	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Check to see if a invoice is Cancelled
	 * 
	 * @return
	 */
	@Transient
	public boolean isCancelledInvoice() {
		if (getStatus().isPaid() && totalAmount.compareTo(BigDecimal.ZERO) == 0)
			return true;
		return false;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@OneToMany(mappedBy = "invoice", cascade = { CascadeType.ALL })
	public List<InvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}

	@OneToMany(mappedBy = "invoice", cascade = { CascadeType.ALL })
	public List<InvoicePayment> getPayments() {
		return payments;
	}

	public void setPayments(List<InvoicePayment> payments) {
		this.payments = payments;
	}

	@Transient
	public void markPaid(User u) {
		setStatus(TransactionStatus.Paid);
		this.setPaidDate(new Date());
		this.setAuditColumns(u);
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO;
		for (InvoicePayment ip : payments) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
	}
	
	@Deprecated
	public boolean isPaid() {
		return getStatus().isPaid();
	}
	
	@Enumerated(EnumType.STRING)
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	@Column(name = "ccNumber")
	public String getCcNumber() {
		return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

}
