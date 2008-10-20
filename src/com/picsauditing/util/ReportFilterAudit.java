package com.picsauditing.util;

public class ReportFilterAudit extends ReportFilterContractor {
	protected boolean auditType = true;
	protected boolean auditStatus = true;
	protected boolean auditor = true;
	protected boolean createdDate = true;
	protected boolean completedDate = true;
	protected boolean closedDate = true;
	protected boolean expiredDate = true;
	protected boolean oshaEmr = false;

	public boolean isAuditType() {
		return auditType;
	}

	public void setAuditType(boolean auditType) {
		this.auditType = auditType;
	}

	public boolean isAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(boolean auditStatus) {
		this.auditStatus = auditStatus;
	}

	public boolean isAuditor() {
		return auditor;
	}

	public void setAuditor(boolean auditor) {
		this.auditor = auditor;
	}

	public boolean isCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(boolean createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(boolean completedDate) {
		this.completedDate = completedDate;
	}

	public boolean isClosedDate() {
		return closedDate;
	}

	public void setClosedDate(boolean closedDate) {
		this.closedDate = closedDate;
	}

	public boolean isExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(boolean expiredDate) {
		this.expiredDate = expiredDate;
	}

	public boolean isOshaEmr() {
		return oshaEmr;
	}

	public void setOshaEmr(boolean oshaEmr) {
		this.oshaEmr = oshaEmr;
	}
}
