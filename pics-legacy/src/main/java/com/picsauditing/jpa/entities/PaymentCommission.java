package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;

@SuppressWarnings("serial")
@Entity
@Table(name="payment_commission")
public class PaymentCommission extends BaseTable {
	
	private InvoiceCommission invoiceCommission;
	private Payment payment;
	private BigDecimal paymentAmount = BigDecimal.ZERO;
	private BigDecimal activationPoints;
	
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

	@ReportField(type = FieldType.Float, requiredPermissions = OpPerms.SalesCommission)
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

    // Does this field really require OpPerms.SalesCommission?
    // The entire table probably requires it but these two fields don't seem special
    // Trevor 6/9/2013
	@ReportField(type = FieldType.Float, requiredPermissions = OpPerms.SalesCommission)
	public BigDecimal getActivationPoints() {
		return activationPoints;
	}
	
	public void setActivationPoints(BigDecimal activationPoints) {
		this.activationPoints = activationPoints;
	}

}