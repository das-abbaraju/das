package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@SuppressWarnings("serial")
@Entity
@Table(name = "generalcontractors")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorOperator extends BaseTable implements java.io.Serializable {
	private OperatorAccount operatorAccount;
	private ContractorAccount contractorAccount;
	private String workStatus = "P";
	private FlagColor forceFlag;
	private Date forceEnd;
	private ContractorOperatorFlag flag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "genID", nullable = false, updatable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operator) {
		this.operatorAccount = operator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subID", nullable = false, updatable = false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(ContractorAccount contractor) {
		this.contractorAccount = contractor;
	}

	/**
	 * Assume Yes if the operator approvesRelationships=No, otherwise this
	 * should default to P and then be approved or rejected
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

	@OneToOne(fetch = FetchType.LAZY)
	@Fetch(FetchMode.JOIN)
	@JoinColumns( { @JoinColumn(name = "genID", referencedColumnName = "opID", insertable = false, updatable = false),
			@JoinColumn(name = "subID", referencedColumnName = "conID", insertable = false, updatable = false) })
	public ContractorOperatorFlag getFlag() {
		return flag;
	}

	public void setFlag(ContractorOperatorFlag flag) {
		this.flag = flag;
	}

}
