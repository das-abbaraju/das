package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="payment_commission")
public class PaymentCommission extends BaseTable {
	
	private InvoiceCommission invoiceCommission;
	private Payment payment;
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	private float activationPoints;
	
	@ManyToOne
	@JoinColumn(name = "commissionID", nullable = false, updatable = false)
	public InvoiceCommission getInvoiceCommission() {
		return invoiceCommission;
	}

	public void setInvoiceCommission(InvoiceCommission invoiceCommission) {
		this.invoiceCommission = invoiceCommission;
	}

	@ManyToOne
	@JoinColumn(name = "paymentID", nullable = false)
	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	
	public float getActivationPoints() {
		return activationPoints;
	}
	
	public void setActivationPoints(float activationPoints) {
		this.activationPoints = activationPoints;
	}

}
