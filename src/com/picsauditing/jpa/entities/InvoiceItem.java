package com.picsauditing.jpa.entities;

import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name = "invoice_item")
public class InvoiceItem extends BaseTable implements java.io.Serializable {

	private Invoice invoice;
	private InvoiceFee invoiceFee;
	private int amount;
	private String description;

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
