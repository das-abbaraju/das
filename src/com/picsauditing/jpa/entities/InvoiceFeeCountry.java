package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_fee_country")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class InvoiceFeeCountry extends BaseTable {
	private InvoiceFee invoiceFee;
	private Country country;
	private BigDecimal amount = BigDecimal.ZERO;

	@ManyToOne
	@JoinColumn(name = "feeID")
	public InvoiceFee getInvoiceFee() {
		return invoiceFee;
	}

	public void setInvoiceFee(InvoiceFee invoiceFee) {
		this.invoiceFee = invoiceFee;
	}

	@ManyToOne
	@JoinColumn(name = "country")
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name = "amount", nullable = false)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
