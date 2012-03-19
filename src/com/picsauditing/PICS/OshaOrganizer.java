package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.SafetyStatistics;
import com.picsauditing.util.Testable;
import com.picsauditing.util.YearList;


public class OshaOrganizer implements OshaVisitor {

	// OSHA Audits will be sorted by their auditYears
	Map<OshaType, Map<Integer, SafetyStatistics>> safetyStatisticsData = new HashMap<OshaType, Map<Integer, SafetyStatistics>>();
	
	private static final MultiYearScope[] YEARS_ONLY = {
			MultiYearScope.ThreeYearsAgo, MultiYearScope.TwoYearsAgo,
			MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage };

	/**
	 * For the given OshaType, determine (up to) the three most resent years for
	 * which we data.
	 * 
	 * @param type
	 */
	@Testable
	public YearList mostRecentThreeYears(OshaType type) {
		YearList yearList = new YearList();
		
		if (safetyStatisticsData.get(type) != null && safetyStatisticsData.get(type).size() > 0) {
			for (int year : safetyStatisticsData.get(type)
					.keySet()) {
				yearList.add(year);
			}
		}
		return yearList;
	}

	public boolean isVerified(OshaType type, MultiYearScope year) {
		/*
		 * if (data.get(type).isEmpty()) return false;
		 * 
		 * PicsLogger.log("OshaOrganizer.isVerified(" + type + "," + year +
		 * ")"); OshaAudit oshaAudit = getOshaAudit(type, year);
		 * 
		 * return oshaAudit != null && oshaAudit.isVerified();
		 */
		return false;
	}

	/**
	 * Returns the contractor's rate for a specified year, osha type and rate
	 * type. If a value is not found for the given parameters this method will
	 * return -1 (since rates are always positive).
	 * 
	 * @param type
	 * @param scope
	 * @param rateType
	 * @return
	 */
	public double getRate(OshaType type, MultiYearScope scope, OshaRateType rateType) {
		YearList years = mostRecentThreeYears(type);

		if (scope == MultiYearScope.ThreeYearAverage) {
			int avgCount = 0;
			BigDecimal rate = new BigDecimal(0);
			
			for (MultiYearScope yearScope: YEARS_ONLY) {
			BigDecimal value = getRateForSpecficYear(type, years.getYearForScope(yearScope), rateType);
								
				if (value != null) {
					rate = rate.add(value);
					avgCount++;
				}
			}
			
			if (avgCount == 0)
				return -1;

			return rate.divide(new BigDecimal(avgCount), 2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		}
		else {
			Integer yearWeWant = years.getYearForScope(scope);
			if (yearWeWant != null && yearWeWant > 0) {
				return this.getRateForSpecficYear(type, yearWeWant, rateType).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			}
			return -1;
		}
	}

	public BigDecimal getRateForSpecficYear(OshaType type, Integer year, OshaRateType rateType) {
		if (year != null) {
			Map<Integer, SafetyStatistics> typeMap = safetyStatisticsData.get(type);
			SafetyStatistics stats = typeMap.get(year);
			String value = stats.getStats(rateType);
		
			if (value != null) {
				return new BigDecimal(value);
			}
		}
		return null;
	}


	public SafetyStatistics getStatistic(OshaType type, MultiYearScope year) {
		return safetyStatisticsData.get(type).get(new Integer(year.getAuditFor()));
	}

	public String getAnswer2(OshaType type, MultiYearScope year, OshaRateType rateType) {
		/*String auditFor = getAuditFor(type, year);*/
		return null;
	}
	
	@Override
	public void gatherData(SafetyStatistics safetyStatistics) {
		Map<Integer, SafetyStatistics> innerMap = safetyStatisticsData.get(safetyStatistics.getOshaType());
		if (innerMap == null) {
			innerMap = new HashMap<Integer, SafetyStatistics>();
		}
		innerMap.put(safetyStatistics.getYear(), safetyStatistics);
		safetyStatisticsData.put(safetyStatistics.getOshaType(), innerMap);	
	}
	
	public boolean hasOshaType(OshaType oshaType) {
		return safetyStatisticsData.get(oshaType) != null && safetyStatisticsData.get(oshaType).size() > 0;
	}
	public int size() {
		return safetyStatisticsData.size();
	}
	
}