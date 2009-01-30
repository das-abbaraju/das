package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditOperator extends BaseTable implements java.io.Serializable {

	protected AuditType auditType;
	protected OperatorAccount operatorAccount;
	protected boolean canSee;
	protected boolean canEdit;
	protected int minRiskLevel = 0;
	protected AuditStatus requiredAuditStatus = AuditStatus.Active; // Can sometimes be Submitted
	protected FlagColor requiredForFlag;
	protected int orderedCount = -1;
	protected Date orderDate;

	private int htmlID = 0;
	private FlagColor contractorFlag;
	
	@ManyToOne
	@JoinColumn(name = "auditTypeID", nullable=false)
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public boolean isCanSee() {
		return canSee;
	}

	public void setCanSee(boolean canSee) {
		this.canSee = canSee;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	/**
	 * 0 None, 1 Low, 2 Med, 3 High
	 * @return
	 */
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
	@JoinColumn(name = "opID", nullable=false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operator) {
		this.operatorAccount = operator;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getRequiredForFlag() {
		return requiredForFlag;
	}

	public void setRequiredForFlag(FlagColor requiredForFlag) {
		this.requiredForFlag = requiredForFlag;
	}
	
	@Enumerated(EnumType.STRING)
	public AuditStatus getRequiredAuditStatus() {
		return requiredAuditStatus;
	}
	
	public void setRequiredAuditStatus(AuditStatus requiredAuditStatus) {
		this.requiredAuditStatus = requiredAuditStatus;
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
			htmlID = (getAuditType().getId() * 100000);
		if (getOperatorAccount() != null)
			htmlID += getOperatorAccount().getId();
		return htmlID;
	}

	/**
	 * Temporary field to store ??
	 * @return
	 */
	@Transient
	public FlagColor getContractorFlag() {
		return contractorFlag;
	}

	public void setContractorFlag(FlagColor contractorFlag) {
		this.contractorFlag = contractorFlag;
	}

	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditOperator other = (AuditOperator) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
