package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable {

	private ContractorAudit audit;
	private OperatorAccount operator;
	private AuditStatus status = AuditStatus.Pending;
	private User statusChangedBy = null;
	private Date submittedDate;
	private Date completedDate;
	private Date approvedDate;
	private Date incompleteDate;
	private int percentComplete;
	private int percentVerified;
	private boolean visible = true;
	private FlagColor flag = null;
	private String notes;
	private String reason;
	// To be removed
	private YesNo valid = null;
	private Certificate certificate;

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
	 * @param auditStatus
	 * @see ContractorAuditOperator.changeStatus()
	 */
	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@Transient
	public void changeStatus(AuditStatus auditStatus, User user) {
		// If we're changing the status to Submitted or Active, then we need
		// to set the dates
		Date today = new Date();
		if (auditStatus.isIncomplete()) {
			incompleteDate = today;
		}
		if (auditStatus.isSubmittedResubmitted()) {
			submittedDate = today;
		}
		if (auditStatus.isComplete()) {
			completedDate = today;
		}
		if (auditStatus.isApproved()) {
			approvedDate = today;
		}

		setAuditColumns(user);
		setStatusChangedBy(user);
		setStatus(auditStatus);
	}

	@ManyToOne
	@JoinColumn(name = "statusChangedBy")
	public User getStatusChangedBy() {
		return statusChangedBy;
	}

	public void setStatusChangedBy(User statusChangedBy) {
		this.statusChangedBy = statusChangedBy;
	}

	@Transient
	public Date getEffectiveDate() {
		if (audit.getAuditType().getClassType() == AuditTypeClass.Policy)
			return creationDate;

		if (status.isApproved())
			return approvedDate;

		if (status.isComplete())
			return completedDate;

		if (status.isSubmittedResubmitted())
			return submittedDate;

		if (status.isIncomplete())
			return incompleteDate;

		if (status.isPending() && audit.getAuditor() != null)
			return audit.getAssignedDate();

		return creationDate;
	}
	

	@Transient
	// TODO rewrite this status description if needed
	public String getStatusDescription() {
		String statusDescription = "";
		/*
		if (status.isActive())
			if (auditType.isMustVerify())
				if (auditType.isPqf() || auditType.isAnnualAddendum())
					statusDescription = "Annual requirements have been verified. " + this.getAuditType().getClassType()
							+ " is closed.";
				else
					statusDescription = this.getAuditType().getClassType() + " has been verified.";
			else if (auditType.isHasRequirements())
				statusDescription = "All the requirements for this " + this.getAuditType().getClassType().toString()
						+ " have been met. " + this.getAuditType().getClassType() + " closed.";
			else
				statusDescription = this.getAuditType().getClassType() + " closed.";

		if (auditStatus.isExempt())
			statusDescription = this.getAuditType().getClassType() + " is not required.";

		if (auditStatus.isExpired())
			statusDescription = this.getAuditType().getClassType() + " is no longer active.";

		if (auditStatus.isPending())
			if (auditType.isMustVerify())
				statusDescription = this.getAuditType().getClassType() + " has not been submitted.";
			else
				statusDescription = this.getAuditType().getClassType() + " has not been started.";

		if (auditStatus.isSubmitted())
			if (contractorAccount.isAcceptsBids()) {
				statusDescription = this.getAuditType().getClassType().toString() + " has been submitted.";
			} else if (auditType.isMustVerify())
				statusDescription = this.getAuditType().getClassType().toString()
						+ " has been sent.  Awaiting verification.";
			else
				statusDescription = this.getAuditType().getClassType().toString()
						+ " has been submitted but there are requirements pending.";

		if (auditStatus.isResubmitted())
			statusDescription = "Policy updated; pending approval of changes.";

		if (auditStatus.isIncomplete())
			statusDescription = "Rejected " + this.getAuditType().getClassType() + " during verification";
			*/
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
				if (audit.getAuditType().isMustVerify())
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

	@Column(length = 1000)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Transient
	public boolean isNotesLength() {
		if (Strings.isEmpty(notes))
			return false;
		return true;
	}

	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = { @Parameter(name = "enumClass", value = "com.picsauditing.jpa.entities.YesNo") })
	@Enumerated(EnumType.STRING)
	public YesNo getValid() {
		return valid;
	}

	public void setValid(YesNo valid) {
		this.valid = valid;
	}

	@ManyToOne
	@JoinColumn(name = "certificateID")
	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	@Enumerated(EnumType.STRING)
	public FlagColor getFlag() {
		return flag;
	}

	public void setFlag(FlagColor flag) {
		this.flag = flag;
	}

	@Column(length = 500)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Date getIncompleteDate() {
		return incompleteDate;
	}

	public void setIncompleteDate(Date incompleteDate) {
		this.incompleteDate = incompleteDate;
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

	@Transient
	public boolean isVisibleTo(Permissions permissions) {
		if (permissions.isOperatorCorporate()) {
			return permissions.getVisibleCAOs().contains(operator.getId());
		}

		for (ContractorOperator co : audit.getContractorAccount().getNonCorporateOperators()) {
			if (co.getOperatorAccount().getInheritInsurance().getId() == operator.getId())
				return true;
		}
		return false;
	}

	@Transient
	public boolean isCanContractorSubmit() {
		return certificate != null && valid != null && (valid.isTrue() || !Strings.isEmpty(reason));
	}

}
