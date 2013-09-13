package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportFilterCAO extends ReportFilterAudit {

	protected boolean showAuditorType = false;
	protected boolean showAuditStatus = true;
	protected boolean showPercentComplete = true;
	protected boolean showPercentVerified = true;
	protected boolean showCaoStatusChangedDate = true;
	protected boolean showCaoOperator = true;
	protected boolean showNotRenewingContractors = false;
	protected boolean showContractorsWithPendingMembership = false;

	protected AuditStatus[] auditStatus;
	protected String percentComplete1;
	protected String percentComplete2;
	protected String percentVerified1;
	protected String percentVerified2;
	protected int[] caoOperator;
	protected boolean auditorType = true;
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

	public boolean isShowPercentVerified() {
		return showPercentVerified;
	}

	public void setShowPercentVerified(boolean showPercentVerified) {
		this.showPercentVerified = showPercentVerified;
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

	public String getPercentVerified1() {
		return percentVerified1;
	}

	public void setPercentVerified1(String percentVerified1) {
		this.percentVerified1 = percentVerified1;
	}

	public String getPercentVerified2() {
		return percentVerified2;
	}

	public void setPercentVerified2(String percentVerified2) {
		this.percentVerified2 = percentVerified2;
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

	public boolean isShowAuditorType() {
		return showAuditorType;
	}

	public void setShowAuditorType(boolean showAuditorType) {
		this.showAuditorType = showAuditorType;
	}
	
	public boolean isAuditorType() {
		return auditorType;
	}

	public void setAuditorType(boolean auditorType) {
		this.auditorType = auditorType;
	}
	
	public Map<String, String> getAuditorTypeList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("false", "Closing Auditor");
		map.put("true", "Safety Professional");
		return map;
	}
}
