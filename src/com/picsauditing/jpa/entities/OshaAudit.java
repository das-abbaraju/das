package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.picsauditing.PICS.OshaVisitable;
import com.picsauditing.PICS.OshaVisitor;
import com.picsauditing.util.Testable;

/**
 * Decorator for ContractorAudit, specifically for when ContractorAudit is an
 * OSHa type, that adds OSHA-specific logic.
 * 
 * @author PICS-User
 * 
 */
public class OshaAudit implements OshaVisitable {
	public static final int CAT_ID_OSHA = 2033; // U.S.
	public static final int CAT_ID_COHS = 2086; // Canada
	public static final int CAT_ID_UK_HSE = 2092; // U.K.
	public static final int[] SAFETY_STATISTICS_CATEGORY_IDS = new int[] {CAT_ID_OSHA, CAT_ID_COHS, CAT_ID_UK_HSE};

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
			 if (category.isApplies()) {
				if (oshaType == OshaType.OSHA) {
					safetyStatistics = new OshaStatistics(year, contractorAudit.getData());
				} else if (oshaType == OshaType.COHS) {
					safetyStatistics = new CohsStatistics(year, contractorAudit.getData());
				} else if (oshaType == OshaType.UK_HSE) {
					safetyStatistics = new UkStatistics(year, contractorAudit.getData());
				}
				if (safetyStatistics != null) {
					safetyStatisticsMap.put(oshaType,safetyStatistics);
				}
			}
		}
	}

	@Transient
	public SafetyStatistics getSafetyStatistics(OshaType oshaType) {
		return safetyStatisticsMap.get(oshaType);
	}

	@Transient
	public boolean isEmpty(OshaType oshaType) {
		return (getSafetyStatistics(oshaType).getStats(OshaRateType.Hours) == null || getSafetyStatistics(
				oshaType).getStats(OshaRateType.Hours).equals("0"));
	}



	@Override
	public void accept(OshaVisitor visitor) {
		for (SafetyStatistics stats: getStatistics()) {
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
		for (ContractorAuditOperator cao: contractorAudit.getOperators()) {
			if (cao.isVisible() && cao.getStatus().isComplete())
				return true;
		}
		return false;
	}

}
