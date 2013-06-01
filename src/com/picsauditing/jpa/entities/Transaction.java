package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tableType", discriminatorType = DiscriminatorType.STRING)
public abstract class Transaction extends BaseTable {
	protected Account account;
	protected BigDecimal totalAmount = BigDecimal.ZERO;
	protected BigDecimal amountApplied = BigDecimal.ZERO;
    protected boolean sapSync;
	protected boolean qbSync;
	protected Date sapLastSync;
	protected String sapId;
	protected String qbListID;
	protected TransactionStatus status = TransactionStatus.Unpaid;
	protected Currency currency = Currency.USD;

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@ReportField(category = FieldCategory.Billing, type = FieldType.Float, requiredPermissions = OpPerms.Billing, importance = FieldImportance.Required)
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@ReportField(category = FieldCategory.Billing, type = FieldType.Float, requiredPermissions = OpPerms.Billing, importance = FieldImportance.Average)
	public BigDecimal getAmountApplied() {
		return amountApplied;
	}

	public void setAmountApplied(BigDecimal amountApplied) {
		this.amountApplied = amountApplied;
	}

	@Transient
	public BigDecimal getBalance() {
		if (TransactionStatus.Void.equals(status)) {
			return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP);
		}
		return totalAmount.subtract(amountApplied);
	}

	@Transient
	public boolean isApplied() {
		return getBalance().compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * True if QuickBooks Web Connector needs to pull this record into QuickBooks
	 *
	 * @return
	 */
	public boolean isQbSync() {
		return qbSync;
	}

	public void setQbSync(boolean qbSync) {
		this.qbSync = qbSync;
	}

	@Temporal(TemporalType.DATE)
	public Date getSapLastSync() {
		return sapLastSync;
	}

	public void setSapLastSync(Date sapLastSync) {
		this.sapLastSync = sapLastSync;
	}

    public boolean isSapSync() {
        return sapSync;
    }

    public void setSapSync(boolean sapSync) {
        this.sapSync = sapSync;
    }

    @Column(name = "sapID")
	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	/**
	 * Unique Customer ID in QuickBooks, sample: 31A0000-1151296183
	 *
	 * @return
	 */
	public String getQbListID() {
		return qbListID;
	}

	public void setQbListID(String qbListID) {
		this.qbListID = qbListID;
	}

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	@ReportField(type = FieldType.TransactionStatus, importance = FieldImportance.Average)
	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	@ReportField(category = FieldCategory.Invoicing, type = FieldType.Currency, importance = FieldImportance.Required)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency curreny) {
		this.currency = curreny;
	}

	public void updateAmountApplied() {
		if (status.isVoid()) {
			return;
		}
		if (status.isPaid() && totalAmount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		if (totalAmount.compareTo(BigDecimal.ZERO) != 0 && getBalance().compareTo(BigDecimal.ZERO) <= 0) {
			status = TransactionStatus.Paid;
		} else {
			status = TransactionStatus.Unpaid;
		}
	}
}
