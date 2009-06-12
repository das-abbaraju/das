package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(value = "P")
public class Payment extends Transaction {

	private PaymentMethod paymentMethod;
	private String checkNumber;
	private String transactionID;
	private String ccNumber;

	private List<InvoicePayment> invoices = new ArrayList<InvoicePayment>();

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

	@OneToMany(mappedBy = "payment", cascade = { CascadeType.ALL })
	public List<InvoicePayment> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<InvoicePayment> invoices) {
		this.invoices = invoices;
	}

	@Transient
	public void updateAmountApplied() {
		amountApplied = BigDecimal.ZERO;
		for (InvoicePayment ip : invoices) {
			amountApplied = amountApplied.add(ip.getAmount());
		}
	}

}
