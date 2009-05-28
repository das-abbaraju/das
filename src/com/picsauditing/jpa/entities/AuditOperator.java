package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "audit_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditOperator extends BaseTable {

	protected AuditType auditType;
	protected OperatorAccount operatorAccount;
	protected boolean canSee;
	protected boolean canEdit;
	protected int minRiskLevel = 0;
	// Can sometimes be Submitted
	protected AuditStatus requiredAuditStatus = AuditStatus.Active;
	protected FlagColor requiredForFlag;
	protected String help;

	private int htmlID = 0;
	private FlagColor contractorFlag;

	@ManyToOne
	@JoinColumn(name = "auditTypeID", nullable = false)
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
	 * 
	 * @return
	 */
	public int getMinRiskLevel() {
		return minRiskLevel;
	}

	public void setMinRiskLevel(int minRiskLevel) {
		this.minRiskLevel = minRiskLevel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opID", nullable = false)
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

	@Column(length = 1000)
	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
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
	 * 
	 * @return
	 */
	@Transient
	public FlagColor getContractorFlag() {
		return contractorFlag;
	}

	public void setContractorFlag(FlagColor contractorFlag) {
		this.contractorFlag = contractorFlag;
	}

	/**
	 * Transient method that returns true if below are all true
	 * <ul>
	 * <li>the operator canSee this AuditType</li>
	 * <li>minRiskLevel > 0</li>
	 * <li>minRiskLevel <= contractor.getRiskLevel().ordinal()</li>
	 * </ul>
	 * 
	 * @param contractor
	 * @return
	 */
	@Transient
	public boolean isRequiredFor(ContractorAccount contractor) {
		return (canSee && minRiskLevel > 0 && minRiskLevel <= contractor.getRiskLevel().ordinal());
	}
}
