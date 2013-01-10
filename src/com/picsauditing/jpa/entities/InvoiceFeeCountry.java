package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

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
	private CountrySubdivision subdivision;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal ratePercent = BigDecimal.ZERO;
	private Date effectiveDate;

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

	@ManyToOne
	@JoinColumn(name = "subdivision")
	public CountrySubdivision getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(CountrySubdivision subdivision) {
		this.subdivision = subdivision;
	}

	@Column(name = "amount", nullable = false)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRatePercent() {
		return ratePercent;
	}

	public void setRatePercent(BigDecimal ratePercent) {
		this.ratePercent = ratePercent;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
