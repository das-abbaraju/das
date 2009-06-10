package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Entity
@MappedSuperclass
public class Transaction extends BaseTable {
	protected Account account;
	protected TransactionType txnType;
	protected BigDecimal totalAmount = BigDecimal.ZERO;
	protected BigDecimal amountApplied = BigDecimal.ZERO;
	protected Date txnDate;
	protected boolean qbSync;
	protected String qbListID;

	@ManyToOne
	@JoinColumn(name = "accountID")
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Enumerated(EnumType.STRING)
	public TransactionType getTxnType() {
		return txnType;
	}

	public void setTxnType(TransactionType type) {
		this.txnType = type;
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

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Date transactionDate) {
		this.txnDate = transactionDate;
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

}
