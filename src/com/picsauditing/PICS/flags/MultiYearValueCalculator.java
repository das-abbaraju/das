package com.picsauditing.PICS.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.util.Strings;
import com.picsauditing.util.Testable;
import com.picsauditing.util.YearList;

/**
 * This is a collection of misc. business calculations that are applied to audit
 * question answers. They allow us to extract knowledge from the raw data. This
 * knowledge is currently used in two ways:
 * 
 * 1. As the inputs for flag decisions.
 * 
 * 2. For EMR-specific calculated fields.
 */
public class MultiYearValueCalculator {

	private static final int EMR_YES_NO_QUESTION_ID = 2033;

	/**
	 * Private default constructor to discourage creating new instances and
	 * extending this class.
	 */
	private MultiYearValueCalculator() {
	}

	/**
	 * Used when the audits are expected to be retrieved and the results applied
	 * using the YearList.
	 * 
	 * @param contractor
	 * @param criteria
	 * @return
	 */
	public static String calculateValueForMultiYear(ContractorAccount contractor, FlagCriteria criteria) {
		List<ContractorAudit> audits = buildMultiYearAudits(contractor.getAudits());
		return calculateValueForMultiYear(criteria, audits);
	}

	@Testable
	static List<ContractorAudit> buildMultiYearAudits(List<ContractorAudit> audits) {
		if (CollectionUtils.isEmpty(audits)) {
			return Collections.emptyList();
		}

		Map<String, ContractorAudit> auditsByYear = new HashMap<String, ContractorAudit>();
		YearList yearList = new YearList();

		retrieveAnnualUpdatesForFlagCriteriaProcessing(audits, auditsByYear, yearList);
		List<ContractorAudit> auditsForFlagCriteria = buildAuditsForFlagCriteria(auditsByYear, yearList);

		return auditsForFlagCriteria;
	}

	/**
	 * The List<ContractorAudit> returned can have null entries if there is no
	 * Annual Update audit in a valid Status (Complete or further in the
	 * Workflow) for that year.
	 * 
	 * @param auditsByYear
	 * @param yearList
	 * @return
	 */
	@Testable
	static List<ContractorAudit> buildAuditsForFlagCriteria(Map<String, ContractorAudit> auditsByYear, YearList yearList) {
		List<ContractorAudit> auditsForFlagCriteria = new ArrayList<ContractorAudit>();
		auditsForFlagCriteria.add(retrieveAuditForScope(auditsByYear, yearList, MultiYearScope.LastYearOnly));
		auditsForFlagCriteria.add(retrieveAuditForScope(auditsByYear, yearList, MultiYearScope.TwoYearsAgo));
		auditsForFlagCriteria.add(retrieveAuditForScope(auditsByYear, yearList, MultiYearScope.ThreeYearsAgo));
		return auditsForFlagCriteria;
	}

	@Testable
	static void retrieveAnnualUpdatesForFlagCriteriaProcessing(List<ContractorAudit> audits,
			Map<String, ContractorAudit> auditsByYear, YearList yearList) {
		yearList.setToday(new Date());

		for (ContractorAudit audit : audits) {
			if (auditIsCompleteAnnualUpdate(audit)) {
				String year = audit.getAuditFor();
				if (!Strings.isEmpty(year)) {
					yearList.add(year);
					auditsByYear.put(year, audit);
				}
			}
		}
	}

	@Testable
	static boolean auditIsCompleteAnnualUpdate(ContractorAudit audit) {
		return (audit != null && audit.getAuditType() != null && audit.getAuditType().isAnnualAddendum() && audit
				.hasCaoStatusAfter(AuditStatus.Resubmitted));
	}

	@Testable
	static ContractorAudit retrieveAuditForScope(Map<String, ContractorAudit> auditsByYear, YearList yearList,
			MultiYearScope multiYearScope) {
		Integer yearForScope = yearList.getYearForScope(multiYearScope);
		if (yearForScope != null) {
			return auditsByYear.get(yearForScope.toString());
		}

		return null;
	}

	/**
	 * Used in cases where existing code does not use the YearList, and an
	 * existing set of Audits are used to perform the calculation. The audits in
	 * the list are expected to be in descending order by Year.
	 * 
	 * @param contractor
	 * @param criteria
	 * @param audits
	 * @return
	 */
	@Testable
	public static String calculateValueForMultiYear(FlagCriteria criteria, List<ContractorAudit> audits) {
		Number result = null;

		switch (criteria.getMultiYearScope()) {

		case LastYearOnly:
			result = findValueForSpecificYear(audits, criteria, 1);
			break;

		case TwoYearsAgo:
			result = findValueForSpecificYear(audits, criteria, 2);
			break;

		case ThreeYearsAgo:
			result = findValueForSpecificYear(audits, criteria, 3);
			break;

		case ThreeYearAverage:
			result = calculateMultiYearAverage(criteria, audits);
			break;

		case ThreeYearSum:
			result = calculateMultiYearSum(criteria, audits);
			break;
		}

		if (result != null) {
			return result.toString();
		}

		return null;
	}

	@Testable
	static Double findValueForSpecificYear(List<ContractorAudit> audits, FlagCriteria criteria, int yearsBack) {
		if (yearsBack < 1) {
			throw new IllegalArgumentException("MultiYearValueCalculator: Years back must be greater than 0.");
		} else if (CollectionUtils.isEmpty(audits) || audits.size() < yearsBack || (audits.get(yearsBack - 1) == null)) {
			return null;
		}

		return findValueInAuditData(criteria, audits.get(yearsBack - 1).getData());
	}

	@Testable
	static Number calculateMultiYearSum(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (!"Check Box".equals(criteria.getQuestion().getQuestionType())) {
			List<Double> values = findValuesForMathematicalFunction(criteria, audits);
			Double result = addValues(values);
			return (result == null) ? new Double(0.0) : result;
		}

		return countSelectedCheckBoxes(criteria, audits);
	}

	/**
	 * Null Values are not included in the average calculation
	 * 
	 * @param rule
	 * @param audits
	 * @return
	 */
	static Double calculateMultiYearAverage(FlagCriteria criteria, List<ContractorAudit> audits) {
		List<Double> values = findValuesForMathematicalFunction(criteria, audits);
		Double sum = addValues(values);

		int numberOfValues = totalNonNullValues(values);
		if (sum != null && numberOfValues > 0) {
			return (sum / numberOfValues);
		}

		return null;
	}

	/**
	 * Get a map of the last 3 years of applicable EMR data (verified or not)
	 */
	public static List<OshaResult> getOshaResultsForEMR(List<ContractorAudit> audits) {
		List<OshaResult> oshaResults = buildOshaResultsList(audits);

		if (oshaResults.size() == 4) {
			oshaResults.remove(0);
		} else if (oshaResults.size() > 4) {
			throw new RuntimeException("Found [" + oshaResults.size() + "] EMRs");
		}

		return oshaResults;
	}

	static List<OshaResult> buildOshaResultsList(List<ContractorAudit> audits) {
		List<OshaResult> oshaResults = new ArrayList<OshaResult>();
		int count = 0;

		for (ContractorAudit audit : audits) {
			if (count < 4 && audit.hasCaoStatus(AuditStatus.Complete)) {
				// Store the EMR rates into a list for later use
				for (AuditData answer : audit.getData()) {
					if (answer.getQuestion().getId() == AuditQuestion.EMR
							|| (answer.getQuestion().getId() == EMR_YES_NO_QUESTION_ID && "No".equals(answer
									.getAnswer()))) {
						String answerValue = answer.getAnswer();
						if (!Strings.isEmpty(answerValue)) {
							count++;
							if (answer.getQuestion().getId() != EMR_YES_NO_QUESTION_ID) {
								boolean verified = true; // we assume that
															// everything is
															// verified until we
															// prove otherwise
								if (answer.isUnverified()) {
									verified = false;
								}

								OshaResult oshaResult = new OshaResult.Builder().verified(verified).answer(answerValue)
										.year(audit.getAuditFor()).build();
								oshaResults.add(oshaResult);
							}
						}
					}
				}
			}
		}

		return oshaResults;
	}

	/**
	 * Calculate the Average EMR
	 */
	public static OshaResult calculateAverageEMR(List<OshaResult> values) {
		OshaResult oshaResult = null;
		if (values != null && !values.isEmpty()) {
			return oshaResult;
		}

		String years = null;
		float rateTotal = 0;
		int count = 0;
		boolean verified = true;
		for (OshaResult singleResult : values) {
			if (Strings.isEmpty(years)) {
				years = singleResult.getYear();
			} else {
				years = ", " + singleResult.getYear();
			}

			if (!singleResult.isVerified()) {
				verified = false;
			}

			try {
				float rate = Float.parseFloat(singleResult.getAnswer());
				rateTotal += rate;
				count++;
			} catch (Exception WeCannotDoAnythingAboutThis) {
			}
		}

		if (count > 0) {
			Float avgRateFloat = rateTotal / count;
			avgRateFloat = (float) Math.round(1000 * avgRateFloat) / 1000;
			oshaResult = new OshaResult.Builder().answer(Float.toString(avgRateFloat)).year(years).verified(verified)
					.build();
		}

		return oshaResult;
	}

	@Testable
	static int totalNonNullValues(List<Double> values) {
		int count = 0;
		for (Double value : values) {
			if (value != null) {
				count++;
			}
		}

		return count;
	}

	@Testable
	static Double addValues(List<Double> values) {
		if (CollectionUtils.isEmpty(values)) {
			return null;
		}

		Double total = null;
		for (Double number : values) {
			if (number != null) {
				if (total == null) {
					total = new Double(0.0);
				}

				total += number;
			}
		}

		return total;
	}

	@Testable
	static List<Double> findValuesForMathematicalFunction(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (CollectionUtils.isEmpty(audits)) {
			return null;
		}

		List<Double> values = new ArrayList<Double>();
		for (ContractorAudit audit : audits) {
			if (audit != null) {
				List<AuditData> auditDataList = audit.getData();
				values.add(findValueInAuditData(criteria, auditDataList));
			}
		}

		return values;
	}

	@Testable
	static Integer countSelectedCheckBoxes(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (CollectionUtils.isEmpty(audits)) {
			return null;
		}

		int total = 0;
		for (ContractorAudit audit : audits) {
			if (audit != null) {
				List<AuditData> auditDataList = audit.getData();
				total += totalCheckBoxSelectedForQuestion(criteria, auditDataList);
			}
		}

		return total;
	}

	@Testable
	static int totalCheckBoxSelectedForQuestion(FlagCriteria criteria, List<AuditData> auditDataList) {
		for (AuditData auditData : auditDataList) {
			if (checkBoxQuestionWithAnswer(criteria, auditData)) {
				return 1;
			}
		}

		return 0;
	}

	@Testable
	static Double findValueInAuditData(FlagCriteria criteria, List<AuditData> auditDataList) {
		Double value = null;
		for (AuditData auditData : auditDataList) {
			if (questionWithNumericAnswer(criteria, auditData)) {
				value = NumberUtils.toDouble(auditData.getAnswer(), 0.0);
			}
		}

		return value;
	}

	private static boolean questionWithNumericAnswer(FlagCriteria criteria, AuditData auditData) {
		return (auditData != null && criteria.getQuestion().getId() == auditData.getQuestion().getId()
				&& FlagCriteria.NUMBER.equals(criteria.getDataType()) && !Strings.isEmpty(auditData.getAnswer()));
	}

	private static boolean checkBoxQuestionWithAnswer(FlagCriteria criteria, AuditData auditData) {
		return (auditData != null && criteria.getQuestion().getId() == auditData.getQuestion().getId()
				&& "Check Box".equals(criteria.getQuestion().getQuestionType())
				&& !Strings.isEmpty(auditData.getAnswer()) && "X".equals(auditData.getAnswer()));
	}

}
