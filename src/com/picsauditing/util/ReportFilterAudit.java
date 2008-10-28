package com.picsauditing.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;

public class ReportFilterAudit extends ReportFilterContractor {

	// Filter parameters
	protected boolean showAuditType = true;
	protected boolean showAuditStatus = true;
	protected boolean showAuditor = true;
	protected boolean showCreatedDate = true;
	protected boolean showCompletedDate = true;
	protected boolean showClosedDate = true;
	protected boolean showExpiredDate = true;
	protected boolean showOshaEmr = false;
	protected boolean showPercentComplete = true;
	protected boolean showUnConfirmedAudits = false;
	protected boolean showConLicense = false;
	protected boolean showExpiredLicense = false;


	// Filter values
	protected int[] auditID;
	protected int[] auditTypeID;
	protected AuditStatus[] auditStatus;
	protected int[] auditorId;
	protected Date createdDate1;
	protected Date createdDate2;
	protected Date completedDate1;
	protected Date completedDate2;
	protected Date closedDate1;
	protected Date closedDate2;
	protected Date expiredDate1;
	protected Date expiredDate2;
	protected String percentComplete1;
	protected String percentComplete2;
	protected boolean unScheduledAudits = false;
	protected boolean conExpiredLic = false;
	protected String validLicense = "Valid";
	protected boolean osha1 = false;
	protected boolean osha2 = false;
	protected boolean osha3 = false;
	protected boolean emr07 = false;
	protected boolean emr06 = false;
	protected boolean emr05 = false;

	public boolean isShowAuditType() {
		return showAuditType;
	}

	public void setShowAuditType(boolean showAuditType) {
		this.showAuditType = showAuditType;
	}

	public boolean isShowAuditStatus() {
		return showAuditStatus;
	}

	public void setShowAuditStatus(boolean showAuditStatus) {
		this.showAuditStatus = showAuditStatus;
	}

	public boolean isShowAuditor() {
		return showAuditor;
	}

	public void setShowAuditor(boolean showAuditor) {
		this.showAuditor = showAuditor;
	}

	public boolean isShowCreatedDate() {
		return showCreatedDate;
	}

	public void setShowCreatedDate(boolean showCreatedDate) {
		this.showCreatedDate = showCreatedDate;
	}

	public boolean isShowCompletedDate() {
		return showCompletedDate;
	}

	public void setShowCompletedDate(boolean showCompletedDate) {
		this.showCompletedDate = showCompletedDate;
	}

	public boolean isShowClosedDate() {
		return showClosedDate;
	}

	public void setShowClosedDate(boolean showClosedDate) {
		this.showClosedDate = showClosedDate;
	}

	public boolean isShowExpiredDate() {
		return showExpiredDate;
	}

	public void setShowExpiredDate(boolean showExpiredDate) {
		this.showExpiredDate = showExpiredDate;
	}

	public boolean isShowOshaEmr() {
		return showOshaEmr;
	}

	public void setShowOshaEmr(boolean showOshaEmr) {
		this.showOshaEmr = showOshaEmr;
	}
	
	public boolean isShowPercentComplete() {
		return showPercentComplete;
	}

	public void setShowPercentComplete(boolean showPercentComplete) {
		this.showPercentComplete = showPercentComplete;
	}

	public boolean isShowUnConfirmedAudits() {
		return showUnConfirmedAudits;
	}

	public void setShowUnConfirmedAudits(boolean showUnConfirmedAudits) {
		this.showUnConfirmedAudits = showUnConfirmedAudits;
	}

	public boolean isShowConLicense() {
		return showConLicense;
	}

	public void setShowConLicense(boolean showConLicense) {
		this.showConLicense = showConLicense;
	}

	public boolean isShowExpiredLicense() {
		return showExpiredLicense;
	}

	public void setShowExpiredLicense(boolean showExpiredLicense) {
		this.showExpiredLicense = showExpiredLicense;
	}

	public int[] getAuditID() {
		return auditID;
	}

	public void setAuditID(int[] auditID) {
		this.auditID = auditID;
	}

	public int[] getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int[] auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public AuditStatus[] getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus[] auditStatus) {
		this.auditStatus = auditStatus;
	}

	public int[] getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(int[] auditorId) {
		this.auditorId = auditorId;
	}

	public Date getCreatedDate1() {
		return createdDate1;
	}

	public void setCreatedDate1(Date createdDate1) {
		this.createdDate1 = createdDate1;
	}

	public Date getCreatedDate2() {
		return createdDate2;
	}

	public void setCreatedDate2(Date createdDate2) {
		this.createdDate2 = createdDate2;
	}

	public Date getCompletedDate1() {
		return completedDate1;
	}

	public void setCompletedDate1(Date completedDate1) {
		this.completedDate1 = completedDate1;
	}

	public Date getCompletedDate2() {
		return completedDate2;
	}

	public void setCompletedDate2(Date completedDate2) {
		this.completedDate2 = completedDate2;
	}

	public Date getClosedDate1() {
		return closedDate1;
	}

	public void setClosedDate1(Date closedDate1) {
		this.closedDate1 = closedDate1;
	}

	public Date getClosedDate2() {
		return closedDate2;
	}

	public void setClosedDate2(Date closedDate2) {
		this.closedDate2 = closedDate2;
	}

	public Date getExpiredDate1() {
		return expiredDate1;
	}

	public void setExpiredDate1(Date expiredDate1) {
		this.expiredDate1 = expiredDate1;
	}

	public Date getExpiredDate2() {
		return expiredDate2;
	}

	public void setExpiredDate2(Date expiredDate2) {
		this.expiredDate2 = expiredDate2;
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

	public boolean isUnScheduledAudits() {
		return unScheduledAudits;
	}

	public void setUnScheduledAudits(boolean unScheduledAudits) {
		this.unScheduledAudits = unScheduledAudits;
	}

	public boolean isConExpiredLic() {
		return conExpiredLic;
	}

	public void setConExpiredLic(boolean conExpiredLic) {
		this.conExpiredLic = conExpiredLic;
	}

	public String getValidLicense() {
		return validLicense;
	}

	public void setValidLicense(String validLicense) {
		this.validLicense = validLicense;
	}

	public boolean isOsha1() {
		return osha1;
	}

	public void setOsha1(boolean osha1) {
		this.osha1 = osha1;
	}

	public boolean isOsha2() {
		return osha2;
	}

	public void setOsha2(boolean osha2) {
		this.osha2 = osha2;
	}

	public boolean isOsha3() {
		return osha3;
	}

	public void setOsha3(boolean osha3) {
		this.osha3 = osha3;
	}

	public boolean isEmr07() {
		return emr07;
	}

	public void setEmr07(boolean emr07) {
		this.emr07 = emr07;
	}

	public boolean isEmr06() {
		return emr06;
	}

	public void setEmr06(boolean emr06) {
		this.emr06 = emr06;
	}

	public boolean isEmr05() {
		return emr05;
	}

	public void setEmr05(boolean emr05) {
		this.emr05 = emr05;
	}

	// Getting all the Lists
	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		List<AuditType> list = new ArrayList<AuditType>();
		for (AuditType aType : dao.findAll()) {
			if (permissions.canSeeAudit(aType))
				list.add(aType);
		}
		return list;
	}

	public AuditStatus[] getAuditStatusList() {
		return AuditStatus.values();
	}

}
