package com.picsauditing.PICS.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.util.Strings;
import com.picsauditing.util.Testable;

/**
 * Only to be used by Annual Updates. 
 */
public class MultiYearValueCalculator {

	/**
	 * Private default constructor to discourage creating new instances
	 * and extending this class.
	 */
	private MultiYearValueCalculator() {}

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

		return getValueFromAuditData(criteria, audits.get(yearsBack - 1).getData());
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
	 * @param audits List of ContractorAudit objects in sorted order.
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

		if (sum != null) {
			return (sum / values.size());
		}

		return null;
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
			if(isCheckBoxQuestionWithAnswer(criteria, auditData)) {
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

}
