package com.picsauditing.util;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.CaoStatus;

@SuppressWarnings("serial")
public class ReportFilterAudit extends ReportFilterContractor {

	// Filter parameters
	protected boolean showAuditType = true;
	protected boolean showPolicyType = false;
	protected boolean showAuditStatus = true;
	protected boolean showCaoStatus = false;
	protected boolean showRecommendedFlag = false;
	protected boolean showAuditor = true;
	protected boolean showClosingAuditor = false;
	protected boolean showCreatedDate = true;
	protected boolean showCompletedDate = true;
	protected boolean showClosedDate = true;
	protected boolean showHasClosedDate = false;
	protected boolean showExpiredDate = true;
	protected boolean showPercentComplete = true;
	protected boolean showUnConfirmedAudits = false;
	protected boolean showConLicense = false;
	protected boolean showExpiredLicense = false;
	protected boolean showAuditFor = false;
	protected boolean showEmrRange = false;
	protected boolean showIncidenceRate = false;
	protected boolean showAMBest = false;
	

	// Filter values
	protected int[] auditID;
	protected int[] auditTypeID;
	protected int[] pqfTypeID;
	protected AuditStatus[] auditStatus;
	protected CaoStatus[] caoStatus;
	protected String recommendedFlag;
	protected int[] auditorId;
	protected int[] closingAuditorId;
	protected Date createdDate1;
	protected Date createdDate2;
	protected Date completedDate1;
	protected Date completedDate2;
	protected Date closedDate1;
	protected Date closedDate2;
	protected String hasClosedDate;
	protected Date expiredDate1;
	protected Date expiredDate2;
	protected String percentComplete1;
	protected String percentComplete2;
	protected boolean unScheduledAudits = false;
	protected boolean conExpiredLic = false;
	protected String validLicense = "Valid";
	protected String[] auditFor;
	protected float minEMR = 0;
	protected float maxEMR = 100;
	protected double incidenceRate = -1;
	protected int amBestRating;
	protected int amBestClass;

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
	
	public boolean isShowCaoStatus() {
		return showCaoStatus;
	}

	public void setShowCaoStatus(boolean showCaoStatus) {
		this.showCaoStatus = showCaoStatus;
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

	public boolean isShowPolicyType() {
		return showPolicyType;
	}

	public void setShowPolicyType(boolean showPolicyType) {
		this.showPolicyType = showPolicyType;
	}
	
	public boolean isShowRecommendedFlag() {
		return showRecommendedFlag;
	}

	public void setShowRecommendedFlag(boolean showRecommendedFlag) {
		this.showRecommendedFlag = showRecommendedFlag;
	}
	
	public boolean isShowHasClosedDate() {
		return showHasClosedDate;
	}

	public void setShowHasClosedDate(boolean showHasClosedDate) {
		this.showHasClosedDate = showHasClosedDate;
	}
	
	public boolean isShowClosingAuditor() {
		return showClosingAuditor;
	}

	public void setShowClosingAuditor(boolean showClosingAuditor) {
		this.showClosingAuditor = showClosingAuditor;
	}

	public boolean isShowAMBest() {
		return showAMBest;
	}

	public void setShowAMBest(boolean showAMBest) {
		this.showAMBest = showAMBest;
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
	
	public CaoStatus[] getCaoStatus() {
		return caoStatus;
	}

	public void setCaoStatus(CaoStatus[] caoStatus) {
		this.caoStatus = caoStatus;
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
	
	public String getHasClosedDate() {
		return hasClosedDate;
	}

	public void setHasClosedDate(String hasClosedDate) {
		this.hasClosedDate = hasClosedDate;
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

	public String getRecommendedFlag() {
		return recommendedFlag;
	}

	public void setRecommendedFlag(String recommendedFlag) {
		this.recommendedFlag = recommendedFlag;
	}

	// Getting all the Lists
	public List<AuditType> getAuditTypeList() {
		AuditTypeDAO auditDAO = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		return new AuditTypeCache(auditDAO).getAuditTypes(permissions);
	}

	public List<AuditType> getPQFTypeList() {
		AuditTypeDAO auditDAO = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		return new AuditTypeCache(auditDAO).getPqfTypes(permissions);
	}

	public List<AuditType> getPolicyTypeList() {
		AuditTypeDAO auditDAO = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		return new AuditTypeCache(auditDAO).getPolicyTypes(permissions);
	}

	public AuditStatus[] getAuditStatusList() {
		return AuditStatus.values();
	}

	public CaoStatus[] getCaoStatusList() {
		return CaoStatus.values();
	}
	
	public Map<Integer, String> getAMBestClassList() {
		return AmBest.financialMap;
	}

	public Map<Integer, String> getAMBestRatingsList() {
		return AmBest.ratingMap;
	}

	public boolean isShowAuditFor() {
		return showAuditFor;
	}

	public void setShowAuditFor(boolean showAuditFor) {
		this.showAuditFor = showAuditFor;
	}

	public String[] getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String[] auditFor) {
		this.auditFor = auditFor;
	}

	public float getMinEMR() {
		return minEMR;
	}

	public void setMinEMR(float minEMR) {
		this.minEMR = minEMR;
	}

	public float getMaxEMR() {
		return maxEMR;
	}

	public void setMaxEMR(float maxEMR) {
		this.maxEMR = maxEMR;
	}

	public boolean isShowEmrRange() {
		return showEmrRange;
	}

	public void setShowEmrRange(boolean showEmrRange) {
		this.showEmrRange = showEmrRange;
	}

	public boolean isShowIncidenceRate() {
		return showIncidenceRate;
	}

	public void setShowIncidenceRate(boolean showIncidenceRate) {
		this.showIncidenceRate = showIncidenceRate;
	}

	public double getIncidenceRate() {
		return incidenceRate;
	}

	public void setIncidenceRate(double incidenceRate) {
		this.incidenceRate = incidenceRate;
	}

	public int[] getPqfTypeID() {
		return pqfTypeID;
	}

	public void setPqfTypeID(int[] pqfTypeID) {
		this.pqfTypeID = pqfTypeID;
	}

	public int[] getClosingAuditorId() {
		return closingAuditorId;
	}

	public void setClosingAuditorId(int[] closingAuditorId) {
		this.closingAuditorId = closingAuditorId;
	}

	public int getAmBestRating() {
		return amBestRating;
	}

	public void setAmBestRating(int amBestRating) {
		this.amBestRating = amBestRating;
	}

	public int getAmBestClass() {
		return amBestClass;
	}

	public void setAmBestClass(int amBestClass) {
		this.amBestClass = amBestClass;
	}
}
