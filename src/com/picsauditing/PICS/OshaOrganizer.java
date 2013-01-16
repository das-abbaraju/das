package com.picsauditing.PICS;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.QuestionFunction;
import com.picsauditing.jpa.entities.SafetyStatistics;
import com.picsauditing.util.Strings;
import com.picsauditing.util.YearList;

public class OshaOrganizer implements OshaVisitor {
	private static final Logger logger = LoggerFactory.getLogger(OshaOrganizer.class);
	// OSHA Audits will be sorted by their auditYears
	Map<OshaType, Map<Integer, SafetyStatistics>> safetyStatisticsData = new HashMap<OshaType, Map<Integer, SafetyStatistics>>();

	/**
	 * For the given OshaType, determine (up to) the three most resent years for
	 * which we data.
	 * 
	 * @param type
	 */
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
		if (!scope.isIndividualYearScope()) {
			return determineMultiYearVerificationStatus(oshaType);
		}

		YearList yearList = mostRecentThreeYears(oshaType);
		return determineVerificationStatus(oshaType, yearList.getYearForScope(scope));
	}

	private boolean determineMultiYearVerificationStatus(OshaType oshaType) {
		YearList yearList = mostRecentThreeYears(oshaType);

		boolean result = true;
		for (MultiYearScope scope : MultiYearScope.getListOfIndividualYearScopes()) {
			Integer year = yearList.getYearForScope(scope);
			if (year != null && !determineVerificationStatus(oshaType, year)) {
				return false;
			}
		}

		return result;
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
	 * Returns the contractor's rate for a specified year, OshaType and
	 * RateType. If a value is not found for the given parameters this method
	 * will return -1 (since rates are always positive).
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

			for (MultiYearScope yearScope : MultiYearScope.getListOfIndividualYearScopes()) {
				BigDecimal value = getRateForSpecificYear(type, years.getYearForScope(yearScope), rateType);

				if (value != null) {
					rate = rate.add(value);
					avgCount++;
				}
			}

			if (avgCount == 0)
				return -1;

			return rate.divide(new BigDecimal(avgCount), 2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		} else {
			Integer yearWeWant = years.getYearForScope(scope);
			if (yearWeWant != null && yearWeWant > 0) {
				BigDecimal rate = getRateForSpecificYear(type, yearWeWant, rateType);

				if (rate != null) {
					return rate.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
				}
			}

			return -1;
		}
	}

	/* testable */ BigDecimal getRateForSpecificYear(OshaType type, Integer year, OshaRateType rateType) {
		if (year != null) {
			Map<Integer, SafetyStatistics> typeMap = safetyStatisticsData.get(type);
			SafetyStatistics stats = typeMap.get(year);
			String value = stats.getStats(rateType);

			if (!Strings.isEmpty(value)) {
				try {
					return new BigDecimal(value);
				} catch (NumberFormatException valueIsNotAValidNumberSoJustReturnNull) {
					if (QuestionFunction.MISSING_PARAMETER.equals(value)) {
						return null;
					}

					logger.warn("NumberFormatException for value {} with OSHA type {}, rate type {}, and year {}",
							new Object[] { value, type, rateType, year });
				}
			}
		}

		return null;
	}

	public SafetyStatistics getStatistic(OshaType type, MultiYearScope scope) {
		Integer year = this.mostRecentThreeYears(type).getYearForScope(scope);
		if (year == null) {
			return null;
		}

		return safetyStatisticsData.get(type).get(year);
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
					} else {
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
		return (safetyStatisticsData.get(oshaType) != null && safetyStatisticsData.get(oshaType).size() > 0);
	}

	public int size() {
		return safetyStatisticsData.size();
	}

}
