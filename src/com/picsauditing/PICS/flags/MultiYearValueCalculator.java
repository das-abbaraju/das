package com.picsauditing.PICS.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.util.Strings;
import com.picsauditing.util.Testable;
import com.picsauditing.util.log.PicsLogger;

/**
 * Only to be used by Annual Updates.
 */
public class MultiYearValueCalculator {

	private enum StrategyType { EMR, CITATIONS, SERIOUS_CITATION, WILLFUL_CITATIONS }
	
//	private static StrategyType determineStrategyType(ContractorAccount contractor, FlagCriteria criteria) {
//		if (criteria.getQuestion().getId() == AuditQuestion.CITATIONS) {
//			return StrategyType.CITATIONS;
//		} else if (criteria.getQuestion().getId() == AuditQuestion.EMR) {
//			return StrategyType.EMR;
//		}
//		
//		return StrategyType.EMR;
//	}
	
	/**
	 * Private default constructor to discourage creating new instances and
	 * extending this class.
	 */
	private MultiYearValueCalculator() {
	}

	public static String returnValueForMultiYear(ContractorAccount contractor, FlagCriteria criteria) {
		List<ContractorAudit> audits = contractor.getAudits();
		Number result = null;

		switch (criteria.getMultiYearScope()) {
		case LastYearOnly:
			result = getValueForSpecificYear(contractor, criteria, 1);
			break;

		case TwoYearsAgo:
			result = getValueForSpecificYear(contractor, criteria, 2);
			break;

		case ThreeYearsAgo:
			result = getValueForSpecificYear(contractor, criteria, 3);
			break;

		case ThreeYearAverage:
			audits = getAnnualUpdateAudits(contractor, 3);
			result = calculateMultiYearAverage(criteria, audits);
			break;

		case ThreeYearAggregate:
			audits = getAnnualUpdateAudits(contractor, 3);
			result = calculateMultiYearAggregate(criteria, audits);
			break;
		}

		if (result != null) {
			return result.toString();
		}

		return null;
	}

	static Double getValueForSpecificYear(ContractorAccount contractor, FlagCriteria criteria, int yearsBack) {
		List<ContractorAudit> audits = getAnnualUpdateAudits(contractor, yearsBack);
		if (CollectionUtils.isEmpty(audits) || audits.size() < yearsBack) {
			return null;
		}

		return getValueFromAuditData(criteria, audits.get(yearsBack - 1)
				.getData());
	}

	@Testable
	static List<ContractorAudit> getAnnualUpdateAudits(ContractorAccount contractor, int yearsBack) {
		List<ContractorAudit> audits = removeFirstIncomplete(contractor.getSortedAnnualUpdates(), yearsBack);
		if (CollectionUtils.isEmpty(audits)) {
			return Collections.emptyList();
		}

		if (yearsBack > audits.size()) {
			return audits;
		}

		return audits.subList(0, yearsBack);
	}

	/**
	 * Returns a list with the first incomplete audit removed.
	 * 
	 * @param audits
	 *            List of ContractorAudit objects in sorted order.
	 * @param yearsBack
	 * @return
	 */
	static List<ContractorAudit> removeFirstIncomplete(List<ContractorAudit> audits, int yearsBack) {
		if (CollectionUtils.isEmpty(audits)) {
			return Collections.emptyList();
		}

		if (audits.get(0).hasCaoStatusBefore(AuditStatus.Complete)) {
			audits.remove(0);
		}

		return audits;
	}

	@Testable
	static Number calculateMultiYearAggregate(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (!"Check Box".equals(criteria.getQuestion().getQuestionType())) {
			List<Double> values = getValuesForMathematicalFunction(criteria, audits);
			Double result = addValues(values);
			return (result == null) ? new Double(0.0) : result;
		}

		return getTotalCheckBoxCount(criteria, audits);
	}

	/**
	 * Question: Whether or not we should calculate a "Null" value as part of
	 * the Average or not (currently, they are calculated in the average).
	 * 
	 * @param rule
	 * @param audits
	 * @return
	 */
	@Testable
	static Double calculateMultiYearAverage(FlagCriteria criteria, List<ContractorAudit> audits) {
		List<Double> values = getValuesForMathematicalFunction(criteria, audits);
		Double sum = addValues(values);

		int numberOfValues = totalNonNullValues(values);
		if (sum != null && numberOfValues > 0) {
			return (sum / numberOfValues);
		}

		return null;
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
	static List<Double> getValuesForMathematicalFunction(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (CollectionUtils.isEmpty(audits)) {
			return null;
		}

		List<Double> values = new ArrayList<Double>();
		for (ContractorAudit audit : audits) {
			List<AuditData> auditDataList = audit.getData();
			values.add(getValueFromAuditData(criteria, auditDataList));
		}

		return values;
	}

	@Testable
	static Integer getTotalCheckBoxCount(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (CollectionUtils.isEmpty(audits)) {
			return null;
		}

		int total = 0;
		for (ContractorAudit audit : audits) {
			List<AuditData> auditDataList = audit.getData();
			total += totalCheckBoxSelectedForQuestion(criteria, auditDataList);
		}

		return total;
	}

	@Testable
	static int totalCheckBoxSelectedForQuestion(FlagCriteria criteria, List<AuditData> auditDataList) {
		for (AuditData auditData : auditDataList) {
			if (isCheckBoxQuestionWithAnswer(criteria, auditData)) {
				return 1;
			}
		}

		return 0;
	}

	@Testable
	static Double getValueFromAuditData(FlagCriteria criteria, List<AuditData> auditDataList) {
		Double value = null;
		for (AuditData auditData : auditDataList) {
			if (isQuestionWithNumericAnswer(criteria, auditData)) {
				value = NumberUtils.toDouble(auditData.getAnswer(), 0.0);
			}
		}

		return value;
	}

	private static boolean isQuestionWithNumericAnswer(FlagCriteria criteria, AuditData auditData) {
		return (criteria.getQuestion().getId() == auditData.getQuestion().getId() 
				&& FlagCriteria.NUMBER.equals(criteria.getDataType()) 
				&& !Strings.isEmpty(auditData.getAnswer()));
	}

	private static boolean isCheckBoxQuestionWithAnswer(FlagCriteria criteria, AuditData auditData) {
		return (criteria.getQuestion().getId() == auditData.getQuestion().getId()
				&& "Check Box".equals(criteria.getQuestion().getQuestionType())
				&& !Strings.isEmpty(auditData.getAnswer()) 
				&& "X".equals(auditData.getAnswer()));
	}

	// =================================

	public static Set<FlagCriteriaContractor> doOldStuffForEMR(FlagCriteria flagCriteria, ContractorAccount contractor) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		if (flagCriteria.getQuestion().getId() == AuditQuestion.EMR) {
			Map<String, AuditData> auditsOfThisEMRType = contractor.getEmrs();

			List<AuditData> years = new ArrayList<AuditData>();
			for (String year : auditsOfThisEMRType.keySet()) {
				if (!year.equals("Average"))
					years.add(auditsOfThisEMRType.get(year));
			}

			if (years != null && years.size() > 0) {
				Float answer = null;
				String answer2 = "";
				boolean verified = true; // Has the data been verified?

				try {
					switch (flagCriteria.getMultiYearScope()) {
					case ThreeYearAverage:
						AuditData average = auditsOfThisEMRType.get("Average");
						answer = (average != null) ? Float.valueOf(Strings
								.formatNumber(average.getAnswer())) : null;
						for (AuditData year : years) {
							if (year != null) {
								answer2 += (answer2.isEmpty()) ? "Years: "
										+ year.getAudit().getAuditFor() : ", "
										+ year.getAudit().getAuditFor();
							}
						}
						if (average == null || !average.isVerified())
							verified = false;
						break;
					case ThreeYearsAgo:
						if (years.size() >= 3) {
							if (years.get(years.size() - 3) != null) {
								answer = Float.valueOf(Strings
										.formatNumber(years.get(
												years.size() - 3).getAnswer()));
								verified = years.get(years.size() - 3)
										.isVerified();
								answer2 = "Year: "
										+ years.get(years.size() - 3)
												.getAudit().getAuditFor();
							}
						}
						break;
					case TwoYearsAgo:
						if (years.size() >= 2) {
							if (years.get(years.size() - 2) != null) {
								answer = Float.valueOf(Strings
										.formatNumber(years.get(
												years.size() - 2).getAnswer()));
								verified = years.get(years.size() - 2)
										.isVerified();
								answer2 = "Year: "
										+ years.get(years.size() - 2)
												.getAudit().getAuditFor();
							}
						}
						break;
					case LastYearOnly:
						if (years.size() >= 1) {
							AuditData lastYear = years.get(years.size() - 1);
							if (lastYear != null
									&& isLast2Years(lastYear.getAudit()
											.getAuditFor())) {
								answer = Float.valueOf(Strings
										.formatNumber(lastYear.getAnswer()));
								verified = lastYear.isVerified();
								answer2 = "Year: "
										+ lastYear.getAudit().getAuditFor();
							}
						}
						break;
					default:
						throw new RuntimeException(
								"Invalid MultiYear scope of "
										+ flagCriteria.getMultiYearScope()
												.toString()
										+ " specified for flag criteria id "
										+ flagCriteria.getId()
										+ ", contractor id "
										+ contractor.getId());
					}
				} catch (Throwable t) {
					PicsLogger.log("Could not cast contractor: "
							+ contractor.getId() + " and answer: "
							+ ((answer != null) ? answer : "null")
							+ " to a value for criteria: "
							+ flagCriteria.getId());

					answer = null; // contractor errors out somewhere
					// during the process of creating
					// their data
					// do not want to enter partially corrupt data
				}

				if (answer != null) {
					final FlagCriteriaContractor fcc = new FlagCriteriaContractor(
							contractor, flagCriteria, answer.toString());
					fcc.setVerified(verified);

					// conditionally add verified tag
					if (verified) {
						answer2 += "<br/><span class=\"verified\">Verified</span>";
					}
					fcc.setAnswer2(answer2);

					changes.add(fcc);
				} else {
					if (flagCriteria.isFlaggableWhenMissing()) {
						FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria, null);
						flagCriteriaContractor.setAnswer2(null);
						changes.add(flagCriteriaContractor);
					}
				}
			}
		}
		
		
		return changes;
	}

	private static boolean isLast2Years(String auditFor) {
		int lastYear = DateBean.getCurrentYear() - 1;
		if (Integer.toString(lastYear).equals(auditFor)
				|| Integer.toString(lastYear - 1).equals(auditFor))
			return true;
		return false;
	}

}
