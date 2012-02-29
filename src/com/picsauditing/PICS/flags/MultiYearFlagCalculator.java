package com.picsauditing.PICS.flags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaRule;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.util.Strings;
import com.picsauditing.util.Testable;

/** 
 * Users should not extend this class.
 */
final public class MultiYearFlagCalculator {
	
	/**
	 * Private default constructor to discourage creating new instances of this class.
	 */
	private MultiYearFlagCalculator() {}
	
	public static boolean performCalculationForMultiYear(ContractorAccount contractor, FlagCriteria criteria) {
		 int yearsBack = determineYearsBack(criteria);
		 List<ContractorAudit> audits = getAnnualUpdateAudits(contractor, yearsBack);
		 Double multiYearValue = calculateMuliYearValue(criteria, audits);
		 
//		 String hurdle = FlagUtilities.getHurdle(criteria.get
		 String dataType = criteria.getDataType();
		 
//		 FlagUtilities.compare(dataType, criteria.getComparison(), hurdle, multiYearValue.toString());
		 
		 // this needs to call the new Flag Utility class to determine whether or not it meets the Flag Criteria
		 return false;
	}
	
	@Testable
	static int determineYearsBack(FlagCriteria criteria) {
		switch (criteria.getMultiYearScope()) {
			case LastYearOnly:
				return 1;
			
			case TwoYearsAgo:
				return 2;
				
			case ThreeYearAverage:
			case ThreeYearAggregate:
				return 3;
							
			default:
				throw new IllegalArgumentException("Invalid MultiYearScope = " + criteria.getMultiYearScope());
		}
	}
	
	@Testable
	static List<ContractorAudit> getAnnualUpdateAudits(ContractorAccount contractor, int yearsBack) {
		List<ContractorAudit> audits = contractor.getSortedAnnualUpdates();
		if (CollectionUtils.isEmpty(audits)) {
			return Collections.emptyList();
		}
		
		if (yearsBack > audits.size()) {
			return audits;
		}
		
		return audits.subList(0, yearsBack - 1);
	}	

	static Double calculateMuliYearValue(FlagCriteria criteria, List<ContractorAudit> audits) {
		if (MultiYearScope.ThreeYearAggregate == criteria.getMultiYearScope()) {
			return calculateMultiYearAggregate(criteria, audits);
		} 
		
		return null;
	}
	
	@Testable
	static Double calculateMultiYearAggregate(FlagCriteria criteria, List<ContractorAudit> audits) {
		List<Double> values = getValuesForMathematicalFunction(criteria, audits);
		return addValues(values);
	}
	
	/**
	 * Question: Whether or not we should calculate a "Null" value as part of the Average or not
	 * (currently, they are calculated in the average).
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
			List<AuditData> auditDatas = audit.getData();
			for (AuditData auditData : auditDatas) {
				if (criteria.getQuestion().getId() == auditData.getQuestion().getId()
						&& criteria.getDataType() == FlagCriteria.NUMBER) {
					if (Strings.isEmpty(auditData.getAnswer())) {
						values.add(null);
					} else {
						values.add(NumberUtils.toDouble(auditData.getAnswer(), 0.0));
					}
				}
			}
		}
				
		return values;
	}

}
