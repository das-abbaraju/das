package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_item")
public class InvoiceItem extends BaseTable implements java.io.Serializable {

	private Invoice invoice;
	private InvoiceFee invoiceFee;
	// TODO change this to decimal 
	private int amount;
	private String description;
	
	public InvoiceItem() {
	}

	public InvoiceItem(InvoiceFee fee) {
		super();
		invoiceFee = fee;
		amount = fee.getAmount();
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Column(length=100)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
