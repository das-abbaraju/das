package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.log.PicsLogger;

public class OshaOrganizer {
	// OSHA Audits will be sorted by their auditYears
	private Map<OshaType, ArrayList<OshaAudit>> data = new HashMap<OshaType, ArrayList<OshaAudit>>();

	public OshaOrganizer(List<ContractorAudit> audits) {
		PicsLogger.log("Constructing OshaOrganizer");
		data.put(OshaType.OSHA, new ArrayList<OshaAudit>());
		data.put(OshaType.MSHA, new ArrayList<OshaAudit>());
		data.put(OshaType.COHS, new ArrayList<OshaAudit>());

		for (ContractorAudit contractorAudit : audits) {
			if (contractorAudit.getAuditType().isAnnualAddendum()
					&& (contractorAudit.getAuditStatus().isActiveSubmitted() || contractorAudit.getAuditStatus()
							.isResubmitted())) {
				for (OshaAudit osha : contractorAudit.getOshas()) {
					if (osha.isCorporate()) {
						data.get(osha.getType()).add(osha);
					}
					Collections.sort(data.get(osha.getType()));
				}
			}
		}

		trim(OshaType.OSHA);
		trim(OshaType.MSHA);
		trim(OshaType.COHS);
	}

	/**
	 * If for a given OshaType, there are more than 3 valid year, then trim one
	 * of the years. We always trim either the first year (2006) or the fourth
	 * year (2009). We trim the fourth year ONLY if it's not verified but all
	 * three previous years are.
	 * 
	 * @param type
	 */
	private void trim(OshaType type) {
		List<OshaAudit> yearIndex = data.get(type);
		PicsLogger.log(type + " has [" + yearIndex.size() + "] entries");
		if (yearIndex.size() <= 3)
			return;

		// We trim the fourth year ONLY if it's not verified but all three
		// previous years are.
		if (!yearIndex.get(yearIndex.size() - 1).isVerified() && yearIndex.get(yearIndex.size() - 2).isVerified()
				&& yearIndex.get(yearIndex.size() - 3).isVerified() && yearIndex.get(yearIndex.size() - 4).isVerified()) {
			PicsLogger.log("removed fourthYear" + yearIndex.get(3).getConAudit().getAuditFor());
			data.get(type).remove(3);
		} else {
			while (yearIndex.size() > 3)
				yearIndex.remove(0);
		}
	}

	public boolean isVerified(OshaType type, MultiYearScope year, OshaRateType rateType) {
		switch (year) {
		case LastYearOnly:
		case TwoYearsAgo:
		case ThreeYearsAgo:
		case ThreeYearWeightedAverage:
			return data.get(type).get(data.get(type).size() - 1).isVerified();
		case ThreeYearAverage:
			for (OshaAudit osha : data.get(type)) {
				if (!osha.isVerified())
					return false;
			}
			return true;
		}
		return true;
	}

	public Float getRate(OshaType type, MultiYearScope year, OshaRateType rateType) {
		List<OshaAudit> yearIndex = data.get(type);
		PicsLogger.log("OshaOrganizer.getRate(" + type + "," + year + "," + rateType + ")");
		switch (year) {
		case LastYearOnly:
		case TwoYearsAgo:
		case ThreeYearsAgo:
			int index = 0;
			if (year.equals(MultiYearScope.LastYearOnly))
				index = yearIndex.size() - 1;
			if (year.equals(MultiYearScope.TwoYearsAgo))
				index = yearIndex.size() - 2;
			if (year.equals(MultiYearScope.ThreeYearsAgo))
				index = yearIndex.size() - 3;
			PicsLogger.log("Getting year " + year + " with index " + index);
			if (index < 0)
				return null;
			return yearIndex.get(index).getRate(rateType);
		case ThreeYearWeightedAverage:
			Float manHours = 0f,
			value = 0f;
			for (OshaAudit osha : data.get(type)) {
				if (osha.getManHours() > 0) {
					manHours += osha.getManHours();
					value += osha.getValue(rateType);
				}
			}

			if (manHours == 0f)
				return null;

			return (value * 200000) / manHours;
		case ThreeYearAverage:
			int avgCount = 0;
			Float rate = 0f;
			for (OshaAudit osha : data.get(type)) {
				if (osha.getManHours() > 0) {
					avgCount++;
					rate += osha.getRate(rateType);
				}
			}

			if (avgCount == 0)
				return null;

			return rate / avgCount;
		}

		throw new RuntimeException("OshaOrganizer.getRate() is undefined for MultiYearScope = " + year);
	}

	public String getAuditFor(OshaType type, MultiYearScope year) {
		List<OshaAudit> yearIndex = data.get(type);
		switch (year) {
		case LastYearOnly:
		case TwoYearsAgo:
		case ThreeYearsAgo:
			int index = 0;
			if (year.equals(MultiYearScope.LastYearOnly))
				index = yearIndex.size() - 1;
			if (year.equals(MultiYearScope.TwoYearsAgo))
				index = yearIndex.size() - 2;
			if (year.equals(MultiYearScope.ThreeYearsAgo))
				index = yearIndex.size() - 3;
			PicsLogger.log("Getting year " + year + " with index " + index);
			if (index < 0)
				return null;
			return "Year: " + yearIndex.get(index).getConAudit().getAuditFor();
		case ThreeYearWeightedAverage:
		case ThreeYearAverage:
			String yearResult = null;
			for (OshaAudit osha : data.get(type))
				yearResult += (yearResult == null) ? "" + osha.getConAudit().getAuditFor() : ", "
						+ osha.getConAudit().getAuditFor();

			return yearResult;
		}

		throw new RuntimeException("OshaOrganizer.getAuditFor() is undefined for MultiYearScope = " + year);
	}

	public OshaAudit getOshaAudit(OshaType type, MultiYearScope year) {
		List<OshaAudit> yearIndex = data.get(type);
		switch (year) {
		case LastYearOnly:
		case TwoYearsAgo:
		case ThreeYearsAgo:
			int index = 0;
			if (year.equals(MultiYearScope.LastYearOnly))
				index = yearIndex.size() - 1;
			if (year.equals(MultiYearScope.TwoYearsAgo))
				index = yearIndex.size() - 2;
			if (year.equals(MultiYearScope.ThreeYearsAgo))
				index = yearIndex.size() - 3;
			PicsLogger.log("Getting year " + year + " with index " + index);
			if (index < 0)
				return null;
			return yearIndex.get(index);
		case ThreeYearWeightedAverage:
			OshaAudit weightedAvg = new OshaAudit();

			int weightedManHours = 0;
			int weightedFatalities = 0;
			int weightedLostWorkCases = 0;
			int weightedLostWorkDays = 0;
			int weightedInjuryIllnessCases = 0;
			int weightedRestrictedWorkCases = 0;
			int weightedRecordableTotal = 0; // Total Recordable Incidents
			int weightedFirstAidInjuries = 0;
			int weightedModifiedWorkDay = 0;

			float weightedCad7 = 0;
			float weightedNeer = 0;

			int weightedCount = 0;

			for (OshaAudit osha : yearIndex) {
				weightedAvg.setFactor(osha.getFactor());

				if (osha.getManHours() > 0) {
					weightedCount++;

					// calculating cumulative values
					weightedManHours += osha.getManHours();
					weightedFatalities += osha.getFatalities();
					weightedLostWorkCases += osha.getLostWorkCases();
					weightedLostWorkDays += osha.getLostWorkDays();
					weightedInjuryIllnessCases += osha.getInjuryIllnessCases();
					weightedRestrictedWorkCases = osha.getRestrictedWorkCases();
					weightedRecordableTotal = osha.getRecordableTotal(); // Total
					// Recordable
					// Incidents
					weightedFirstAidInjuries = osha.getFirstAidInjuries();
					weightedModifiedWorkDay = osha.getModifiedWorkDay();

					if(osha.getCad7() != null)
						weightedCad7 += osha.getCad7();
					if(osha.getNeer() != null)
						weightedNeer += osha.getNeer();
				}
			}

			if (weightedCount == 0)
				return null;

			// setting cumulative values
			weightedAvg.setManHours(weightedManHours);
			weightedAvg.setFatalities(weightedFatalities);
			weightedAvg.setLostWorkCases(weightedLostWorkCases);
			weightedAvg.setLostWorkDays(weightedLostWorkDays);
			weightedAvg.setInjuryIllnessCases(weightedInjuryIllnessCases);
			weightedAvg.setRestrictedWorkCases(weightedRestrictedWorkCases);
			weightedAvg.setRecordableTotal(weightedRecordableTotal);
			weightedAvg.setFirstAidInjuries(weightedFirstAidInjuries);
			weightedAvg.setModifiedWorkDay(weightedModifiedWorkDay);

			// rate is based on the cumulative values, so final is weighted
			weightedAvg.setRecordableTotalRate(weightedAvg.getRecordableTotalRate());
			weightedAvg.setLostWorkCasesRate(weightedAvg.getLostWorkCasesRate());
			weightedAvg.setRestrictedDaysAwayRate(weightedAvg.getRestrictedDaysAwayRate());
			weightedAvg.setRestrictedOrJobTransferDays(weightedAvg.getRestrictedOrJobTransferDays());
			weightedAvg.setCad7(weightedCad7 / (float) weightedCount);
			weightedAvg.setNeer(weightedNeer / (float) weightedNeer);

			return weightedAvg;
		case ThreeYearAverage:
			OshaAudit straightAvg = new OshaAudit();

			int straightManHours = 0;
			int straightFatalities = 0;
			int straightLostWorkCases = 0;
			int straightLostWorkDays = 0;
			int straightInjuryIllnessCases = 0;
			int straightRestrictedWorkCases = 0;
			int straightRecordableTotal = 0;
			int straightFirstAidInjuries = 0;
			int straightModifiedWorkDay = 0;

			float straightTrir = 0;
			float straightLwcr = 0;
			float straightDart = 0;
			float straightSeverityRate = 0;
			float straightCad7 = 0;
			float straightNeer = 0;

			int straightCount = 0;

			for (OshaAudit osha : yearIndex) {
				straightAvg.setFactor(osha.getFactor());

				if (osha.getManHours() > 0) {
					straightCount++;

					// calculating cumulative values
					straightManHours += osha.getManHours();
					straightFatalities += osha.getFatalities();
					straightLostWorkCases += osha.getLostWorkCases();
					straightLostWorkDays += osha.getLostWorkDays();
					straightInjuryIllnessCases += osha.getInjuryIllnessCases();
					straightRestrictedWorkCases = osha.getRestrictedWorkCases();
					straightRecordableTotal = osha.getRecordableTotal();
					straightFirstAidInjuries = osha.getFirstAidInjuries();
					straightModifiedWorkDay = osha.getModifiedWorkDay();

					if(osha.getCad7() != null)
						straightCad7 += osha.getCad7();
					if(osha.getNeer() != null)
						straightNeer += osha.getNeer();

					straightTrir = osha.getRecordableTotalRate();
					straightLwcr = osha.getLostWorkCasesRate();
					straightDart = osha.getRestrictedDaysAwayRate();
					straightSeverityRate = osha.getRestrictedOrJobTransferDays();
				}
			}

			if (straightCount == 0)
				return null;

			// setting cumulative values
			straightAvg.setManHours(straightManHours);
			straightAvg.setFatalities(straightFatalities);
			straightAvg.setLostWorkCases(straightLostWorkCases);
			straightAvg.setLostWorkDays(straightLostWorkDays);
			straightAvg.setInjuryIllnessCases(straightInjuryIllnessCases);
			straightAvg.setRestrictedWorkCases(straightRestrictedWorkCases);
			straightAvg.setRecordableTotal(straightRecordableTotal);
			straightAvg.setFirstAidInjuries(straightFirstAidInjuries);
			straightAvg.setModifiedWorkDay(straightModifiedWorkDay);

			// rate is based on the cumulative RATES, so final is NOT straight
			straightAvg.setRecordableTotalRate(straightTrir / (float) straightCount);
			straightAvg.setLostWorkCasesRate(straightLwcr / (float) straightCount);
			straightAvg.setRestrictedDaysAwayRate(straightDart / (float) straightCount);
			straightAvg.setRestrictedOrJobTransferDays(straightSeverityRate / (float) straightCount);
			straightAvg.setCad7(straightCad7 / (float) straightCount);
			straightAvg.setNeer(straightNeer / (float) straightNeer);

			return straightAvg;
		}
		return null;
	}
}
