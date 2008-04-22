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
	protected AuditType auditType;

	protected OperatorAccount operatorAccount;
	protected int minRiskLevel = 1;
	protected FlagColor requiredForFlag;
	protected int orderedCount = -1;
	protected Date orderDate;

	private int htmlID = 0;

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
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount account) {
		this.operatorAccount = account;
	}

	public FlagColor getRequiredForFlag() {
		return requiredForFlag;
	}

	public void setRequiredForFlag(FlagColor requiredForFlag) {
		this.requiredForFlag = requiredForFlag;
	}

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
		if (getOperatorAccount() != null)
			htmlID += getOperatorAccount().getId();
		return htmlID;
	}
}
