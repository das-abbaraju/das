package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_override_history")
public class FlagOverrideHistory extends BaseTable {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor forceFlag;
	private Date forceBegin;
	private Date forceEnd;
	private User forceBy;
	private String forceReason;
	private boolean deleted;
	private User deletedBy;
	private String deleteReason;
	private Date deleteDate;

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne
	@JoinColumn(name = "criteriaID", nullable = true)
	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}

	public Date getForceBegin() {
		return forceBegin;
	}

	public void setForceBegin(Date forceBegin) {
		this.forceBegin = forceBegin;
	}

	@ManyToOne
	@JoinColumn(name = "forceBy", nullable = true)
	public User getForceBy() {
		return forceBy;
	}

	public void setForceBy(User forceBy) {
		this.forceBy = forceBy;
	}

	public String getForceReason() {
		return forceReason;
	}

	public void setForceReason(String forceReason) {
		this.forceReason = forceReason;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@ManyToOne
	@JoinColumn(name = "deletedBy", nullable = true)
	public User getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deletedReason) {
		this.deleteReason = deletedReason;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getForceFlag() {
		return forceFlag;
	}

	public void setForceFlag(FlagColor forceFlag) {
		this.forceFlag = forceFlag;
	}

	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	@Transient
	public boolean isOverall() {
		return (criteria == null);
	}
	
	public void setOverride(FlagDataOverride override) {
		contractor = override.getContractor();
		criteria = override.getCriteria();
		forceBegin = override.getCreationDate();
		forceBy = override.getCreatedBy();
		forceEnd = override.getForceEnd();
		forceFlag = override.getForceflag();
		operator = override.getOperator();
	}

	public void setOverride(ContractorOperator co) {
		contractor = co.getContractorAccount();
		criteria = null;
		forceBegin = co.getForceBegin();
		forceBy = co.getForcedBy();
		forceEnd = co.getForceEnd();
		forceFlag = co.getForceFlag();
		operator = co.getOperatorAccount();
	}
}
