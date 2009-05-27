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

import com.picsauditing.access.Permissions;
import com.picsauditing.util.Strings;

@Entity
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable {
	private ContractorAudit audit;
	private OperatorAccount operator;
	private CaoStatus status = CaoStatus.Pending;
	private User statusChangedBy = null;
	private Date statusChangedDate;
	private boolean visible = true;
	private boolean valid;
	private Certificate certificate;
	private FlagColor flag = null;
	private String notes;
	private String reason;

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
	public CaoStatus getStatus() {
		return status;
	}

	public void setStatus(CaoStatus status) {
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

	@Column(length = 255)
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Transient
	public boolean isVisibleTo(Permissions permissions) {
		if (!visible)
			return false;

		if (permissions.getInsuranceOperatorID() > 0) {
			return operator.getId() == permissions.getInsuranceOperatorID();
		}

		// This may not be necessary any more. We added this when we had
		// BASF but now that it's inherited, we don't have so many records
		// if (status.isNotApplicable())
		// return false;

		return true;
	}
	
	@Transient
	public boolean isCanContractorSubmit() {
		return certificate != null && (valid || !Strings.isEmpty(reason));
	}

}
