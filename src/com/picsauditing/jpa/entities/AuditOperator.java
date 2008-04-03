package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "audit_operator")
public class AuditOperator {

	protected int auditOperatorID = 0;
	protected AuditType auditType = null;

	// protected int opID;
	protected Account account;
	protected int minRiskLevel = 1;
	protected int orderedCount = -1;
	protected Date orderDate = null;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getAuditOperatorID() {
		return auditOperatorID;
	}

	public void setAuditOperatorID(int auditOperatorID) {
		this.auditOperatorID = auditOperatorID;
	}

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public int getMinRiskLevel() {
		return minRiskLevel;
	}

	public void setMinRiskLevel(int minRiskLevel) {
		this.minRiskLevel = minRiskLevel;
	}

	public int getOrderedCount() {
		return orderedCount;
	}

	public void setOrderedCount(int orderedCount) {
		this.orderedCount = orderedCount;
	}

	@Temporal(value = TemporalType.DATE)
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	@ManyToOne( fetch=FetchType.LAZY)
	@JoinColumn(name = "opID")
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	private int htmlID = 0;

	/**
	 * Unique ID used in HTML. We can't use the auditOperatorID because that may
	 * be blank for new records
	 * 
	 * @return
	 */
	@Transient
	public int getHtmlID() {
		htmlID = 0;
		if (getAuditType() != null)
			htmlID = (getAuditType().getAuditTypeID() * 100000);
		if (getAccount() != null)
			htmlID += getAccount().getId();
		return htmlID;
	}
}
