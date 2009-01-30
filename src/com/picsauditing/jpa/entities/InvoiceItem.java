package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_item")
public class InvoiceItem extends BaseTable implements java.io.Serializable {
	
	private ContractorAccount contractor;
	private int amount;
	private boolean paid;
	private Date dueDate;
	private Date paidDate;
	private String paymentMethod; // Check or Credit Card
	private int btTransactionId;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public int getBtTransactionId() {
		return btTransactionId;
	}

	public void setBtTransactionId(int btTransactionId) {
		this.btTransactionId = btTransactionId;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "conID", nullable = false, updatable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}

}
