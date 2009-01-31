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
@Table(name = "invoice_item")
public class InvoiceItem extends BaseTable implements java.io.Serializable {
	
	private int id;
	private Invoice invoice;
	private InvoiceFee invoiceFee;
	private int amount;
	private String description;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
