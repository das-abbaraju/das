package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable implements Comparable<ContractorAuditOperator>{

	private ContractorAudit audit;
	private OperatorAccount operator;
	private AuditStatus status = AuditStatus.Pending;
	private Date statusChangedDate;
	private int percentComplete;
	private int percentVerified;
	private boolean visible = true;
	private FlagColor flag = null;
	private List<ContractorAuditOperatorPermission> caoPermissions = new ArrayList<ContractorAuditOperatorPermission>();
	private List<ContractorAuditOperatorWorkflow> caoWorkflow = new ArrayList<ContractorAuditOperatorWorkflow>();

	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	/**
	 * @return The "inherited" operator/corporate/division that is associated
	 *         with this CAO
	 */
	@ManyToOne
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public AuditStatus getStatus() {
		return status;
	}

	/**
	 * Don't use this! Use changeStatus instead
	 * 
	 * @deprecated
	 * @param auditStatus
	 * @see ContractorAuditOperator.changeStatus()
	 */
	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@Transient
	public void changeStatus(AuditStatus auditStatus, Permissions permissions) {
		setAuditColumns(permissions);
		setStatusChangedDate(new Date());
		this.status = auditStatus;

		if (audit.getAuditType().isPqf() || audit.getAuditType().isAnnualAddendum())
			return;

		if (auditStatus.isPending())
			return;

		if (audit.getEffectiveDate() == null)
			audit.setEffectiveDate(new Date());
	}

	@Transient
	public Date getEffectiveDate() {
		if (status.isPending() && audit.getAuditor() != null)
			return audit.getAssignedDate();

		return statusChangedDate;
	}

	@Transient
	public String getStatusDescription() {
		String statusDescription = "";

		if (status.isComplete()) {
			if (audit.getAuditType().getWorkFlow().isHasSubmittedStep()) {
				if (audit.getAuditType().isPqf() || audit.getAuditType().isAnnualAddendum()) {
					statusDescription = "Annual requirements have been verified. "
							+ audit.getAuditType().getClassType() + " is closed.";
				} else if (audit.getAuditType().getClassType().isPolicy()) {
					statusDescription = "Policy completed, awaiting verification";
				} else
					statusDescription = audit.getAuditType().getClassType() + " has been verified.";
			} else if (audit.getAuditType().getWorkFlow().isHasRequirements()) {
				statusDescription = "All the requirements for this " + audit.getAuditType().getClassType().toString()
						+ " have been met. " + audit.getAuditType().getClassType() + " closed.";
			} else
				statusDescription = audit.getAuditType().getClassType() + " closed.";
		} else if (status.isNotApplicable())
			statusDescription = audit.getAuditType().getClassType() + " is not required.";
		else if (status.isExpired())
			statusDescription = audit.getAuditType().getClassType() + " is no longer active.";
		else if (status.isPending()) {
			if (audit.getAuditType().getWorkFlow().isHasSubmittedStep())
				statusDescription = audit.getAuditType().getClassType() + " has not been submitted.";
			else
				statusDescription = audit.getAuditType().getClassType() + " has not been started.";
		} else if (status.isSubmitted()) {
			if (audit.getContractorAccount().isAcceptsBids()) {
				statusDescription = audit.getAuditType().getClassType().toString() + " has been submitted.";
			} else if (audit.getAuditType().getWorkFlow().isHasSubmittedStep())
				statusDescription = audit.getAuditType().getClassType().toString()
						+ " has been sent.  Awaiting verification.";
			else
				statusDescription = audit.getAuditType().getClassType().toString()
						+ " has been submitted but there are requirements pending.";
		} else if (status.isResubmitted())
			statusDescription = "Policy updated; pending approval of changes.";
		else if (status.isIncomplete())
			statusDescription = "Rejected " + audit.getAuditType().getClassType() + " during verification";

		return statusDescription;
	}

	@Transient
	public String getSynopsis() {
		// TODO clean this up a bit more
		String synopsis = "";

		if (audit.isAboutToExpire()) {
			synopsis = "Expires on " + DateBean.format(audit.getExpiresDate(), "MM/dd/YYYY");
		}

		if (audit.getAuditType().isScheduled()) {
			if (audit.getScheduledDate() == null)
				synopsis = "Waiting to be scheduled";
			else if (audit.getScheduledDate().after(new Date()))
				synopsis = "Scheduled for " + DateBean.format(audit.getScheduledDate(), "MM/dd/YYYY");
		}

		if (!audit.getAuditType().classType.isPolicy() && status.isPending()) {
			if (audit.getAuditType().isCanContractorEdit())
				synopsis = "Waiting on contractor";
			else
				synopsis = "Pending";
		}

		if (status.isNotApplicable()) {
			synopsis = "Not Applicable";
		}

		if (status.isSubmitted()) {
			if (!audit.getContractorAccount().isAcceptsBids()) {
				if (audit.getAuditType().getWorkFlow().isHasSubmittedStep())
					synopsis = "Awaiting verification.";
				else
					synopsis = "Submitted pending requirements.";
			}
		}

		if (status.isComplete() || status.isApproved() || status.isResubmitted() || status.isIncomplete())
			synopsis = status.toString();

		return synopsis;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getFlag() {
		return flag;
	}

	public void setFlag(FlagColor flag) {
		this.flag = flag;
	}

	public Date getStatusChangedDate() {
		return statusChangedDate;
	}

	public void setStatusChangedDate(Date statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}

	public int getPercentVerified() {
		return percentVerified;
	}

	public void setPercentVerified(int percentVerified) {
		this.percentVerified = percentVerified;
	}

	@Transient
	public int getPercent() {
		if (status.isPending())
			return this.percentComplete;
		if (status.isSubmittedResubmitted())
			return this.percentVerified;

		return 100;
	}

	@OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
	public List<ContractorAuditOperatorPermission> getCaoPermissions() {
		return caoPermissions;
	}

	public void setCaoPermissions(List<ContractorAuditOperatorPermission> caoPermissions) {
		this.caoPermissions = caoPermissions;
	}
	
	@OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
	public List<ContractorAuditOperatorWorkflow> getCaoWorkflow() {
		return caoWorkflow;
	}
	
	public void setCaoWorkflow(List<ContractorAuditOperatorWorkflow> caoWorkflow) {
		this.caoWorkflow = caoWorkflow;
	}

	@Transient
	public boolean isVisibleTo(Permissions permissions) {
		if (!this.visible)
			return false;

		if (permissions.isContractor() || permissions.isPicsEmployee())
			return true;

		if (operator.getId() == permissions.getAccountId())
			return true;

		if (permissions.isCorporate()) {
			for (ContractorAuditOperatorPermission caop : getCaoPermissions()) {
				for (Integer ids : permissions.getOperatorChildren()) {
					if (caop.getOperator().getId() == ids)
						return true;
				}
			}
		}

		return hasCaop(permissions.getAccountId());
	}

	@Transient
	public boolean hasCaop(int opID) {
		for (ContractorAuditOperatorPermission caop : getCaoPermissions()) {
			if (caop.getOperator().getId() == opID)
				return true;
		}
		return false;
	}

	@Transient
	public boolean hasOnlyCaop(int opID) {
		if (getCaoPermissions().size() == 1) {
			return hasCaop(opID);
		}
		return false;
	}

	@Override
	public int compareTo(ContractorAuditOperator o) {
		return this.getOperator().getName().compareTo(o.getOperator().getName());
	}
}
