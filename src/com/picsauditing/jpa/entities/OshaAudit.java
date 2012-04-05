package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.jboss.util.Strings;

import com.picsauditing.PICS.OshaVisitable;
import com.picsauditing.PICS.OshaVisitor;
import com.picsauditing.util.Testable;

/**
 * Decorator for ContractorAudit, specifically for when ContractorAudit is an OSHa type, that adds OSHA-specific logic.
 */
public class OshaAudit implements OshaVisitable {
	public static final int CAT_ID_OSHA = 2033; // U.S.
	public static final int CAT_ID_OSHA_ADDITIONAL = 2209; // U.S.
	public static final int CAT_ID_MSHA = 2256; // U.S.
	public static final int CAT_ID_COHS = 2086; // Canada
	public static final int CAT_ID_UK_HSE = 2092; // U.K.
	public static final int CAT_ID_FRANCE_NRIS = 1691; // France
	public static final int[] SAFETY_STATISTICS_CATEGORY_IDS = new int[] { CAT_ID_OSHA, CAT_ID_OSHA_ADDITIONAL,
			CAT_ID_MSHA, CAT_ID_COHS, CAT_ID_UK_HSE, CAT_ID_FRANCE_NRIS };

	protected ContractorAudit contractorAudit;

	Map<OshaType, SafetyStatistics> safetyStatisticsMap;

	public OshaAudit(ContractorAudit contractorAudit) {
		assert (contractorAudit.getAuditType().isAnnualAddendum());

		this.contractorAudit = contractorAudit;
		safetyStatisticsMap = new HashMap<OshaType, SafetyStatistics>();
		initializeStatistics();
	}

	public String getAuditFor() {
		return contractorAudit.getAuditFor();
	}

	public int getId() {
		return contractorAudit.getId();
	}

	public List<ContractorAuditOperator> getCaos() {
		return contractorAudit.getOperatorsVisible();
	}

	@Testable
	List<AuditCatData> getCategories() {
		return contractorAudit.getCategories();
	}

	@Testable
	ContractorAudit getContractorAudit() {
		return contractorAudit;
	}

	@Testable
	List<AuditData> getData() {
		return contractorAudit.getData();
	}

	@Testable
	void setCategories(ArrayList<AuditCatData> categories) {
		contractorAudit.setCategories(categories);
	}

	@Transient
	public Collection<SafetyStatistics> getStatistics() {
		return safetyStatisticsMap.values();
	}

	@Testable
	void initializeStatistics() {
		SafetyStatistics safetyStatistics = null;
		int year = new Integer(contractorAudit.getAuditFor());
		for (AuditCatData category : this.getCategories()) {
			OshaType oshaType = convertCategoryToOshaType(category.getCategory().getId());
			if (oshaType != null && category.isApplies()) {
				if (oshaType == OshaType.OSHA) {
					safetyStatistics = new OshaStatistics(year, contractorAudit.getData());
				} else if (oshaType == OshaType.COHS) {
					safetyStatistics = new CohsStatistics(year, contractorAudit.getData());
				} else if (oshaType == OshaType.UK_HSE) {
					safetyStatistics = new UkStatistics(year, contractorAudit.getData());
				}

				if (safetyStatistics != null) {
					safetyStatisticsMap.put(oshaType, safetyStatistics);
					safetyStatistics.setVerified(isVerified(oshaType));
				}
			}
		}
	}

	public SafetyStatistics getSafetyStatistics(OshaType oshaType) {
		return safetyStatisticsMap.get(oshaType);
	}

	public String getSpecificRate(OshaType oshaType, OshaRateType rateType) {
		if (getSafetyStatistics(oshaType) == null) {
			return Strings.EMPTY;
		}

		return getSafetyStatistics(oshaType).getStats(rateType);
	}

	// TODO I can't find where this actually being used. Need code coverage testing. -- AA
	// Answer: Line 65 in verification_detail.jsp line 298 in verification_audit.jsp -- Sober Alcoholic
	public Integer getFileUploadId(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getFileUpload() == null
				|| getSafetyStatistics(oshaType).getFileUpload().getQuestion() == null) {
			return Integer.valueOf(-1);
		}

		return getSafetyStatistics(oshaType).getFileUpload().getQuestion().getId();
	}

	public List<AuditData> getQuestionsToVerify(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null) {
			return Collections.emptyList();
		}

		return getSafetyStatistics(oshaType).getQuestionsToVerify();
	}

	public Collection<AuditData> getAllQuestionsInOshaType(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null) {
			return Collections.emptyList();
		}

		return getSafetyStatistics(oshaType).getAnswerMap().values();
	}

	public boolean isEmpty(OshaType oshaType) {
		return (getSafetyStatistics(oshaType) == null
				|| getSafetyStatistics(oshaType).getStats(OshaRateType.Hours) == null || getSafetyStatistics(oshaType)
				.getStats(OshaRateType.Hours).equals("0"));
	}

	public String getComment(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getFileUpload() == null) {
			return Strings.EMPTY;
		}

		return getSafetyStatistics(oshaType).getFileUpload().getComment();
	}

	public AuditData stampOshaComment(OshaType oshaType, String comment) {
		if (getSafetyStatistics(oshaType) == null) {
			return null;
		}

		AuditData fileUpload = getSafetyStatistics(oshaType).getFileUpload();
		if (fileUpload != null) {
			fileUpload.setComment(comment);
		}

		return fileUpload;
	}

	@Override
	public void accept(OshaVisitor visitor) {
		for (SafetyStatistics stats : getStatistics()) {
			visitor.gatherData(stats);
		}
	}

	private OshaType convertCategoryToOshaType(int catId) {
		if (catId == CAT_ID_OSHA) {
			return OshaType.OSHA;
		}
		if (catId == CAT_ID_COHS) {
			return OshaType.COHS;
		}
		if (catId == CAT_ID_UK_HSE) {
			return OshaType.UK_HSE;
		}
		return null;

	}

	// If one cao status is complete, it is safe to assume it's verified.
	public boolean isVerified() {
		for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
			if (cao.isVisible() && cao.getStatus().isComplete())
				return true;
		}

		return false;
	}

	public boolean isVerified(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
				|| getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
			return false;
		}

		return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).isVerified();
	}

	public Date getVerifiedDate(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
				|| getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
			return null;
		}

		return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).getDateVerified();
	}
	
	public User getAuditor(OshaType oshaType) {
		if (getSafetyStatistics(oshaType) == null || getSafetyStatistics(oshaType).getAnswerMap() == null
				|| getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours) == null) {
			return null;
		}

		return getSafetyStatistics(oshaType).getAnswerMap().get(OshaRateType.Hours).getAuditor();
	}

	public static boolean isSafetyStatisticsCategory(int categoryId) {
		for (int safetyStatisticsCategory : SAFETY_STATISTICS_CATEGORY_IDS) {
			if (categoryId == safetyStatisticsCategory)
				return true;
		}

		return false;
	}
}
