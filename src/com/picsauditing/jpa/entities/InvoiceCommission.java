package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_commission")
public class InvoiceCommission extends BaseTable {
	
	private Invoice invoice;
	private AccountUser accountUser;
	private BigDecimal points;
	private BigDecimal revenuePercent;

	private List<PaymentCommission> paymentCommissions;

	@ManyToOne
	@JoinColumn(name = "invoiceID", nullable = false, updatable = false)
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@ManyToOne
	@JoinColumn(name = "accountUserID", nullable = false, updatable = false)
	public AccountUser getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}

	@Column(name = "activationPoints")
	public BigDecimal getPoints() {
		return points;
	}

	public void setPoints(BigDecimal points) {
		this.points = points;
	}

	@Column(name = "revenue")
	@ReportField(category = FieldCategory.Commission, type = FieldType.Float, importance = FieldImportance.Average, requiredPermissions = OpPerms.SalesCommission)
	public BigDecimal getRevenuePercent() {
		return revenuePercent;
	}

	public void setRevenuePercent(BigDecimal revenuePercent) {
		this.revenuePercent = revenuePercent;
	}

	@OneToMany(mappedBy = "invoiceCommission", cascade = { CascadeType.REMOVE })
	public List<PaymentCommission> getPaymentCommissions() {
		return paymentCommissions;
	}

	public void setPaymentCommissions(List<PaymentCommission> paymentCommission) {
		this.paymentCommissions = paymentCommission;
	}

}
