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
	private Date statusChangedDate;


	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	/**
	 * @return The "inherited" operator/corporate/division that is associated with this CAO
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

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name = "statusChangedBy")
	public User getStatusChangedBy() {
		return statusChangedBy;
	}

	public void setStatusChangedBy(User statusChangedBy) {
		this.statusChangedBy = statusChangedBy;
	}

	public Date getStatusChangedDate() {
		return statusChangedDate;
	}

	public void setStatusChangedDate(Date statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
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
