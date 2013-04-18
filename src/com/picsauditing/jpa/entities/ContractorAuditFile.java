package com.picsauditing.jpa.entities;

import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_file")
public class ContractorAuditFile extends BaseTable {
	private ContractorAudit audit;
	private String fileType;
	private String description;
	private boolean reviewed;

	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	@Column(nullable = false)
	@ReportField(type = FieldType.String)
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Column(length = 100)
	@ReportField(type = FieldType.String)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ReportField(type = FieldType.Boolean)
	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}
}
