package com.picsauditing.util;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.OshaType;

@SuppressWarnings("serial")
public class ReportFilterAudit extends ReportFilterContractor {

	// Filter parameters
	protected boolean showAuditType = true;
	protected boolean showPolicyType = false;
	protected boolean showRecommendedFlag = false;
	protected boolean showAuditor = true;
	protected boolean showClosingAuditor = false;
	protected boolean showCreatedDate = true;
	protected boolean showUnConfirmedAudits = false;
	protected boolean showConLicense = false;
	protected boolean showExpiredLicense = false;
	protected boolean showAuditFor = false;
	protected boolean showEmrRange = false;
	protected boolean showIncidenceRate = false;
	protected boolean showIncidenceRateAvg = false;
	protected boolean showAMBest = false;
	protected boolean showVerifiedAnnualUpdates = false;
	protected boolean showShaType = false;
	protected boolean showShaTypeFlagCriteria = false;
	protected boolean showShaLocation = false;
	protected boolean showCohsStats = false;
	protected boolean showQuestionAnswer = false;

	// Filter values
	protected int[] auditID;
	protected int[] auditTypeID;
	protected int[] pqfTypeID;
	protected String recommendedFlag;
	protected int[] auditorId;
	protected int[] closingAuditorId;
	protected Date createdDate1;
	protected Date createdDate2;
	protected boolean unScheduledAudits = false;
	protected boolean conExpiredLic = false;
	protected String validLicense = "Valid";
	protected String[] auditFor;
	protected float minEMR = 0;
	protected float maxEMR = 100;
	protected double incidenceRate = -1;
	protected double incidenceRateMax = 100;
	protected double incidenceRateAvg = -1;
	protected double incidenceRateAvgMax = 100;
	protected int amBestRating;
	protected int amBestClass;
	protected int verifiedAnnualUpdate = 1;
	protected OshaType shaType;
	protected OshaType shaTypeFlagCriteria;
	protected String shaLocation;
	protected float cad7 = 0;
	protected float neer = 0;
	protected int[] questionIds;
	protected String answer = "No";
	protected Date statusChangedDate1;
	protected Date statusChangedDate2;

	public boolean isShowAuditType() {
		return showAuditType;
	}

	public void setShowAuditType(boolean showAuditType) {
		this.showAuditType = showAuditType;
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

	public boolean isShowVerifiedAnnualUpdates() {
		return showVerifiedAnnualUpdates;
	}

	public void setShowVerifiedAnnualUpdates(boolean showVerifiedAnnualUpdates) {
		this.showVerifiedAnnualUpdates = showVerifiedAnnualUpdates;
	}

	public boolean isShowShaType() {
		return showShaType;
	}

	public void setShowShaType(boolean showShaType) {
		this.showShaType = showShaType;
	}

	public boolean isShowShaTypeFlagCriteria() {
		return showShaTypeFlagCriteria;
	}

	public void setShowShaTypeFlagCriteria(boolean showShaTypeFlagCriteria) {
		this.showShaTypeFlagCriteria = showShaTypeFlagCriteria;
	}

	public boolean isShowShaLocation() {
		return showShaLocation;
	}

	public void setShowShaLocation(boolean showShaLocation) {
		this.showShaLocation = showShaLocation;
	}

	public boolean isShowCohsStats() {
		return showCohsStats;
	}

	public void setShowCohsStats(boolean showCohsStats) {
		this.showCohsStats = showCohsStats;
	}

	public boolean isShowQuestionAnswer() {
		return showQuestionAnswer;
	}

	public void setShowQuestionAnswer(boolean showQuestionAnswer) {
		this.showQuestionAnswer = showQuestionAnswer;
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

	public int[] getQuestionIds() {
		return questionIds;
	}

	public void setQuestionIds(int[] questionIds) {
		this.questionIds = questionIds;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
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

	public Map<Integer, String> getAMBestClassList() {
		return AmBest.financialMap;
	}

	public Map<Integer, String> getAMBestRatingsList() {
		return AmBest.ratingMap;
	}

	public OshaType[] getOshaTypesList() {
		return OshaType.values();
	}

	public List<AuditQuestion> getQuestionsByAuditList() {
		AuditQuestionDAO auditQuestionDAO = (AuditQuestionDAO) SpringUtils.getBean("AuditQuestionDAO");
		return auditQuestionDAO
				.findWhere("t.category.parentAuditType.id = 81 AND t.effectiveDate < NOW() AND t.expirationDate > NOW()");
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

	public double getIncidenceRateMax() {
		return incidenceRateMax;
	}

	public void setIncidenceRateMax(double incidenceRateMax) {
		this.incidenceRateMax = incidenceRateMax;
	}

	public boolean isShowIncidenceRateAvg() {
		return showIncidenceRateAvg;
	}

	public void setShowIncidenceRateAvg(boolean showIncidenceRateAvg) {
		this.showIncidenceRateAvg = showIncidenceRateAvg;
	}

	public double getIncidenceRateAvg() {
		return incidenceRateAvg;
	}

	public void setIncidenceRateAvg(double incidenceRateAvg) {
		this.incidenceRateAvg = incidenceRateAvg;
	}

	public double getIncidenceRateAvgMax() {
		return incidenceRateAvgMax;
	}

	public void setIncidenceRateAvgMax(double incidenceRateAvgMax) {
		this.incidenceRateAvgMax = incidenceRateAvgMax;
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

	public int getVerifiedAnnualUpdate() {
		return verifiedAnnualUpdate;
	}

	public void setVerifiedAnnualUpdate(int verifiedAnnualUpdate) {
		this.verifiedAnnualUpdate = verifiedAnnualUpdate;
	}

	public OshaType getShaType() {
		return shaType;
	}

	public void setShaType(OshaType shaType) {
		this.shaType = shaType;
	}

	public OshaType getShaTypeFlagCriteria() {
		return shaTypeFlagCriteria;
	}

	public void setShaTypeFlagCriteria(OshaType shaTypeFlagCriteria) {
		this.shaTypeFlagCriteria = shaTypeFlagCriteria;
	}

	public String getShaLocation() {
		return shaLocation;
	}

	public void setShaLocation(String shaLocation) {
		this.shaLocation = shaLocation;
	}

	public float getCad7() {
		return cad7;
	}

	public void setCad7(float cad7) {
		this.cad7 = cad7;
	}

	public float getNeer() {
		return neer;
	}

	public void setNeer(float neer) {
		this.neer = neer;
	}

	public Date getStatusChangedDate1() {
		return statusChangedDate1;
	}

	public void setStatusChangedDate1(Date statusChangedDate1) {
		this.statusChangedDate1 = statusChangedDate1;
	}

	public Date getStatusChangedDate2() {
		return statusChangedDate2;
	}

	public void setStatusChangedDate2(Date statusChangedDate2) {
		this.statusChangedDate2 = statusChangedDate2;
	}
}
