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
	
	// TODO: remove ThreeYearAverage from here if its not being used anywhere
	// in this class from the YEARS_ONLY array
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
			for (int year : safetyStatisticsData.get(type).keySet()) {
				yearList.add(year);
			}
		}
		return yearList;
	}

	/**
	 * Returns whether or the audit has been verified.
	 * 
	 * @param type
	 * @param year
	 * @return
	 */
	public boolean isVerified(OshaType oshaType, MultiYearScope scope) {		
		if (scope.isIndividualYearScope()) {
			return determineMultiYearVerificationStatus(oshaType);
		} 
		
		YearList yearList = mostRecentThreeYears(oshaType);
		return determineVerificationStatus(oshaType, yearList.getYearForScope(scope));
	}
	
	private boolean determineMultiYearVerificationStatus(OshaType oshaType) {
		YearList yearList = mostRecentThreeYears(oshaType);
		
		boolean lastYear = determineVerificationStatus(oshaType, yearList.getYearForScope(MultiYearScope.LastYearOnly));
		boolean twoYears = determineVerificationStatus(oshaType, yearList.getYearForScope(MultiYearScope.TwoYearsAgo));
		boolean threeYears = determineVerificationStatus(oshaType, yearList.getYearForScope(MultiYearScope.ThreeYearsAgo));
		
		return (lastYear && twoYears && threeYears);
	}
	
	private boolean determineVerificationStatus(OshaType oshaType, Integer year) {
		if (year == null) {
			return false;
		}
		
		boolean verified = false;
		Map<Integer, SafetyStatistics> statisticsByYear = safetyStatisticsData.get(oshaType);
		if (statisticsByYear != null) {
			SafetyStatistics stats = statisticsByYear.get(year);
			if (stats != null) {
				verified = stats.isVerified();
			}
		}
		
		return verified;
	}

	/**
	 * Returns the contractor's rate for a specified year, OshaType and RateType. 
	 * If a value is not found for the given parameters this method will
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
			
			for (MultiYearScope yearScope : YEARS_ONLY) {
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
				BigDecimal rate = getRateForSpecficYear(type, yearWeWant, rateType);
				if (rate != null) {
					return rate.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
				}
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
				try {
					return new BigDecimal(value);
				}
				catch (NumberFormatException valueIsNotAValidNumberSoJustReturnNull) {
					return null;
				}
			}
		}
		
		return null;
	}


	public SafetyStatistics getStatistic(OshaType type, MultiYearScope year) {
		return safetyStatisticsData.get(type).get(new Integer(year.getAuditFor()));
	}

	public String getAnswer2(OshaType oshaType, MultiYearScope scope) {
		YearList yearList = mostRecentThreeYears(oshaType);
		yearList.getYearForScope(scope);	
		
		StringBuilder answer2 = new StringBuilder();
		if (scope.isIndividualYearScope()) {
			Integer year = yearList.getYearForScope(scope);
			if (year != null) {
				answer2.append(year.intValue());
			}
		} else {
			for (MultiYearScope yearScope : MultiYearScope.getListOfIndividualYearScopes()) {
				Integer year = yearList.getYearForScope(yearScope);
				if (year != null) {
					if (answer2.length() != 0) {
						answer2.append(", ").append(year.intValue()); 
					}
					else {
						answer2.append(year.intValue());
					}
				}
			}
		}
		
		if (isVerified(oshaType, scope)) {
			answer2.append("<br/><span class=\"verified\">Verified</span>");
		}
		
		return answer2.toString();
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