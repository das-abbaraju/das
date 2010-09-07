package com.picsauditing.util;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportFilterCAO extends ReportFilterAudit {

	protected boolean showAuditStatus = true;
	protected boolean showPercentComplete = true;

	protected AuditStatus[] auditStatus;
	protected String percentComplete1;
	protected String percentComplete2;

	public boolean isShowAuditStatus() {
		return showAuditStatus;
	}

	public void setShowAuditStatus(boolean showAuditStatus) {
		this.showAuditStatus = showAuditStatus;
	}

	public boolean isShowPercentComplete() {
		return showPercentComplete;
	}

	public void setShowPercentComplete(boolean showPercentComplete) {
		this.showPercentComplete = showPercentComplete;
	}

	public AuditStatus[] getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus[] auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getPercentComplete1() {
		return percentComplete1;
	}

	public void setPercentComplete1(String percentComplete1) {
		this.percentComplete1 = percentComplete1;
	}

	public String getPercentComplete2() {
		return percentComplete2;
	}

	public void setPercentComplete2(String percentComplete2) {
		this.percentComplete2 = percentComplete2;
	}

}
