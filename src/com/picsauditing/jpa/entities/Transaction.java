package com.picsauditing.jpa.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DiscriminatorFormula;

@SuppressWarnings("serial")
@Entity
@Table(name = "invoice")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tableType", discriminatorType = DiscriminatorType.CHAR)
public class Transaction extends BaseTable {
	protected Account account;
	protected BigDecimal totalAmount = BigDecimal.ZERO;
	protected BigDecimal amountApplied = BigDecimal.ZERO;
	protected boolean qbSync;
	protected String qbListID;
	private TransactionStatus status;

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
	
	@Transient
	public boolean isApplied() {
		return totalAmount.compareTo(amountApplied) == 0;
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
