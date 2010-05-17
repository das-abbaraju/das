package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("I")
public class Invoice extends Transaction {
	public final static int daysUntilDue = 30;

	private Date dueDate;
	private String poNumber;
	private String notes;
	private Date paidDate; // MAX(Payment.creationDate)

	private List<InvoiceItem> items = new ArrayList<InvoiceItem>();
	private List<PaymentAppliedToInvoice> payments = new ArrayList<PaymentAppliedToInvoice>();

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

	@OneToMany(mappedBy = "invoice", cascade = { CascadeType.REMOVE })
	public List<PaymentAppliedToInvoice> getPayments() {
		return payments;
	}

	public void setPayments(List<PaymentAppliedToInvoice> payments) {
		this.payments = payments;
	}

	@Transient
	public void markPaid(User u) {
		setStatus(TransactionStatus.Paid);
		this.setPaidDate(new Date());
		this.setAuditColumns(u);
	}

	@Transient
	public void updateAmount() {
		totalAmount = BigDecimal.ZERO;
		for (InvoiceItem item : items)
			totalAmount = totalAmount.add(item.getAmount());
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO.setScale(2);
		for (PaymentApplied ip : payments) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
		super.updateAmountApplied();
	}

	@Transient
	public boolean isCcRebill() {
		if (getAccount() instanceof ContractorAccount) {
			ContractorAccount contractor = (ContractorAccount) getAccount();
			return !getStatus().isPaid() && contractor.getPaymentMethod().isCreditCard() && contractor.isCcValid();
		} else
			return false;
	}

	@Transient
	public String getDueDateF() {
		return Strings.formatDateShort(getDueDate());
	}
}
