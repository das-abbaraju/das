package com.picsauditing.util;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportFilterCAO extends ReportFilterAudit {

	protected boolean showAuditStatus = true;
	protected boolean showPercentComplete = true;
	protected boolean showCaoStatusChangedDate = true;
	protected boolean showCaoOperator = true;
	protected boolean showNotRenewingContractors = true;
	protected boolean showContractorsWithPendingMembership = true;

	protected AuditStatus[] auditStatus;
	protected String percentComplete1;
	protected String percentComplete2;
	protected int[] caoOperator;
	protected boolean notRenewingContractors = false;
	protected boolean contractorsWithPendingMembership = false;

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

	public boolean isShowCaoStatusChangedDate() {
		return showCaoStatusChangedDate;
	}

	public void setShowCaoStatusChangedDate(boolean showCaoStatusChangedDate) {
		this.showCaoStatusChangedDate = showCaoStatusChangedDate;
	}

	public boolean isShowCaoOperator() {
		return showCaoOperator;
	}

	public void setShowCaoOperator(boolean showCaoOperator) {
		this.showCaoOperator = showCaoOperator;
	}
	
	public int[] getCaoOperator() {
		return caoOperator;
	}
	
	public void setCaoOperator(int[] caoOperator) {
		this.caoOperator = caoOperator;
	}

	public boolean isShowNotRenewingContractors() {
		return showNotRenewingContractors;
	}

	public void setShowNotRenewingContractors(boolean showNotRenewingContractors) {
		this.showNotRenewingContractors = showNotRenewingContractors;
	}

	public boolean isShowContractorsWithPendingMembership() {
		return showContractorsWithPendingMembership;
	}

	public void setShowContractorsWithPendingMembership(boolean showContractorsWithPendingMembership) {
		this.showContractorsWithPendingMembership = showContractorsWithPendingMembership;
	}

	public boolean isNotRenewingContractors() {
		return notRenewingContractors;
	}

	public void setNotRenewingContractors(boolean notRenewingContractors) {
		this.notRenewingContractors = notRenewingContractors;
	}

	public boolean isContractorsWithPendingMembership() {
		return contractorsWithPendingMembership;
	}

	public void setContractorsWithPendingMembership(boolean contractorsWithPendingMembership) {
		this.contractorsWithPendingMembership = contractorsWithPendingMembership;
	}
}
