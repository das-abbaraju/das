package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

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
import javax.persistence.Transient;

@Entity
@Table(name = "invoice")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tableType", discriminatorType = DiscriminatorType.STRING)
public abstract class Transaction extends BaseTable {
	protected Account account;
	protected BigDecimal totalAmount = BigDecimal.ZERO;
	protected BigDecimal amountApplied = BigDecimal.ZERO;
	protected boolean qbSync;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getAmountApplied() {
		return amountApplied;
	}

	public void setAmountApplied(BigDecimal amountApplied) {
		this.amountApplied = amountApplied;
	}

	@Transient
	public BigDecimal getBalance() {
		if (TransactionStatus.Void.equals(status))
			return BigDecimal.ZERO;
		return totalAmount.subtract(amountApplied);
	}

	@Transient
	public boolean isApplied() {
		return getBalance().equals(BigDecimal.ZERO);
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
	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency curreny) {
		this.currency = curreny;
	}

	public void updateAmountApplied() {
		if (status.equals(TransactionStatus.Void))
			return;
		if (totalAmount.compareTo(BigDecimal.ZERO) != 0 && getBalance().compareTo(BigDecimal.ZERO) == 0)
			status = TransactionStatus.Paid;
		else
			status = TransactionStatus.Unpaid;
	}
}
