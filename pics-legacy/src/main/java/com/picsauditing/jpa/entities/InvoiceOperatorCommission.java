package com.picsauditing.jpa.entities;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldImportance;

import javax.persistence.*;
import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_operator_commission")
public class InvoiceOperatorCommission extends BaseTable {
	
	private Invoice invoice;
	private OperatorAccount operatorAccount;
	private BigDecimal revenuePercent;

	private List<PaymentOperatorCommission> paymentOperatorCommissions;

	@ManyToOne
	@JoinColumn(name = "invoiceID", nullable = false, updatable = false)
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Column(name = "revenue")
	@ReportField(type = FieldType.Float, importance = FieldImportance.Average, requiredPermissions = OpPerms.SalesCommission)
	public BigDecimal getRevenuePercent() {
		return revenuePercent;
	}

	public void setRevenuePercent(BigDecimal revenuePercent) {
		this.revenuePercent = revenuePercent;
	}

	@OneToMany(mappedBy = "invoiceOperatorCommission", cascade = { CascadeType.REMOVE })
	public List<PaymentOperatorCommission> getPaymentOperatorCommissions() {
		return paymentOperatorCommissions;
	}

	public void setPaymentOperatorCommissions(List<PaymentOperatorCommission> paymentOperatorCommissions) {
		this.paymentOperatorCommissions = paymentOperatorCommissions;
	}

}
