package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@Entity
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable {
	private ContractorAudit audit;
	private OperatorAccount operator;
	private CaoStatus status = CaoStatus.Pending;
	private String aiName;
	private boolean aiNameValid;
	private Certificate certificate;
	private FlagColor flag;
	// private CaoStatus recommendedStatus = CaoStatus.Pending;
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

	@Transient
	@Deprecated
	public CaoStatus getRecommendedStatus() {
		if (flag == null)
			return CaoStatus.NotApplicable;
		if (flag.equals(FlagColor.Green))
			return CaoStatus.Approved;
		if (flag.equals(FlagColor.Amber))
			return CaoStatus.Pending;
		return CaoStatus.Rejected;
	}

	@Deprecated
	public void setRecommendedStatus(CaoStatus recommendedStatus) {
		// this.recommendedStatus = recommendedStatus;
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

	public String getAiName() {
		return aiName;
	}

	public void setAiName(String aiName) {
		this.aiName = aiName;
	}

	public boolean isAiNameValid() {
		return aiNameValid;
	}

	public void setAiNameValid(boolean aiNameValid) {
		this.aiNameValid = aiNameValid;
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

}
