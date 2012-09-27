package com.picsauditing.jpa.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice_commission")
public class InvoiceCommission extends BaseTable {
	
	private Invoice invoice;
	private User user; // Sales Representatives or Account Managers
	private float points;
	private float revenuePercent;

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
	@JoinColumn(name = "userID", nullable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "activationPoints")
	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}

	@Column(name = "revenue")
	@ReportField(category = FieldCategory.Commission, filterType = FilterType.Float, requiredPermissions = OpPerms.Billing)
	public float getRevenuePercent() {
		return revenuePercent;
	}

	public void setRevenuePercent(float revenuePercent) {
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
