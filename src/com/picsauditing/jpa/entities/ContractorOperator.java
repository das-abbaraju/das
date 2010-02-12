package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "generalcontractors")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorOperator extends BaseTable implements java.io.Serializable {
	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private String workStatus = "P";
	private FlagColor flagColor;
	private FlagColor forceFlag;
	private Date flagLastUpdated;
	private Date forceEnd;
	private WaitingOn waitingOn = WaitingOn.None;
	private Date processCompletion;
	private String relationshipType;

	@ManyToOne
	@JoinColumn(name = "genID", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operator) {
		this.operatorAccount = operator;
	}

	@ManyToOne
	@JoinColumn(name = "subID", nullable = false, updatable = false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractor) {
		this.contractorAccount = contractor;
	}

	/**
	 * Assume Yes if the operator approvesRelationships=No, otherwise this should default to P and then be approved or
	 * rejected
	 * 
	 * @return P=Pending, Y=Yes, N=No
	 */
	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	@Transient
	public boolean isWorkStatusApproved() {
		return "Y".equals(workStatus);
	}

	@Transient
	public boolean isWorkStatusRejected() {
		return "N".equals(workStatus);
	}

	@Transient
	public boolean isWorkStatusPending() {
		return "P".equals(workStatus);
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}

	@Temporal(TemporalType.DATE)
	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}

	@Temporal(TemporalType.DATE)
	public Date getProcessCompletion() {
		return processCompletion;
	}

	public void setProcessCompletion(Date processCompletion) {
		this.processCompletion = processCompletion;
	}

	@Transient
	public boolean isForcedFlag() {
		if (forceFlag == null || forceEnd == null) {
			// Just double check they are both set back to null
			forceFlag = null;
			forceEnd = null;
			return false;
		}

		// We have a forced flag, but make sure it's still in effect
		if (forceEnd.before(new Date())) {
			forceFlag = null;
			forceEnd = null;
			return false;
		}
		return true;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "flag", nullable = false)
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	public Date getFlagLastUpdated() {
		return flagLastUpdated;
	}

	public void setFlagLastUpdated(Date flagLastUpdated) {
		this.flagLastUpdated = flagLastUpdated;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "waitingOn", nullable = false)
	public WaitingOn getWaitingOn() {
		return waitingOn;
	}

	public void setWaitingOn(WaitingOn waitingOn) {
		this.waitingOn = waitingOn;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	@Transient
	@Deprecated
	public ContractorOperatorFlag getFlag() {
		return null;
	}

	@Deprecated
	public void setFlag(ContractorOperatorFlag flag) {
	}

}
