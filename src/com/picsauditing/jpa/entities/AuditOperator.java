package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "audit_operator")
public class AuditOperator {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	protected int auditOperatorID = 0;
	protected int auditTypeID;
	
	protected int opID;
//	protected Account account;
	protected int minRiskLevel = 1;
	protected int orderedCount = -1;
	protected Date orderDate = null;

	public int getAuditOperatorID() {
		return auditOperatorID;
	}
	public void setAuditOperatorID(int auditOperatorID) {
		this.auditOperatorID = auditOperatorID;
	}
	public int getAuditTypeID() {
		return auditTypeID;
	}
	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}
	public int getOpID() {
		return opID;
	}
	public void setOpID(int opID) {
		this.opID = opID;
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
	
	@Temporal( value = TemporalType.DATE )
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	
	/*
	@ManyToOne
    @JoinColumn(name="opID", nullable=false, updatable=false, insertable=false)
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	*/
}
