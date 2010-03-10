package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
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
		List<OshaAudit> list = data.get(type);
		PicsLogger.log(type + " has [" + list.size() + "] entries");
		if (list.size() < 4)
			return;
		if (list.size() > 4)
			throw new RuntimeException("Found [" + list.size() + "] OshaAudits of type " + type);
		
		// We have 4 years worth of data, get rid of either the first or the last
		// We trim the fourth year ONLY if it's not verified but all three previous years are.
		if (!list.get(3).isVerified() && list.get(2).isVerified()
				&& list.get(1).isVerified() && list.get(0).isVerified()) {
			PicsLogger.log("removed fourthYear" + list.get(3).getConAudit().getAuditFor());
			list.remove(3);
		} else {
			list.remove(0);
		}
	}

	public boolean isVerified(OshaType type, MultiYearScope year) {
		List<OshaAudit> yearIndex = data.get(type);

		if (yearIndex.isEmpty())
			return false;

		PicsLogger.log("OshaOrganizer.isVerified(" + type + "," + year + ")");
		switch (year) {
		case LastYearOnly:
			return yearIndex.get(yearIndex.size() - 1).isVerified();
		case TwoYearsAgo:
			return (yearIndex.size() > 1) ? yearIndex.get(yearIndex.size() - 2).isVerified() : false;
		case ThreeYearsAgo:
			return (yearIndex.size() > 2) ? yearIndex.get(0).isVerified() : false;
		case ThreeYearAverage:
		case ThreeYearWeightedAverage:
			for (OshaAudit osha : data.get(type)) {
				if (!osha.isVerified())
					return false;
			}
			return true;
		}
		return false;
	}

	public Float getRate(OshaType type, MultiYearScope year, OshaRateType rateType) {
		List<OshaAudit> yearIndex = data.get(type);

		if (yearIndex.isEmpty())
			return null;

		PicsLogger.log("OshaOrganizer.getRate(" + type + "," + year + "," + rateType + ")");
		switch (year) {
		case LastYearOnly:
			return yearIndex.get(yearIndex.size() - 1).getRate(rateType);
		case TwoYearsAgo:
			return (yearIndex.size() > 1) ? yearIndex.get(yearIndex.size() - 2).getRate(rateType) : null;
		case ThreeYearsAgo:
			return (yearIndex.size() > 2) ? yearIndex.get(0).getRate(rateType) : null;
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

		if (yearIndex.isEmpty())
			return "";

		switch (year) {
		case LastYearOnly:
			return "Year: " + yearIndex.get(yearIndex.size() - 1).getConAudit().getAuditFor();
		case TwoYearsAgo:
			return (yearIndex.size() > 1) ? "Year: " + yearIndex.get(yearIndex.size() - 2).getConAudit().getAuditFor()
					: "";
		case ThreeYearsAgo:
			return (yearIndex.size() > 2) ? "Year: " + yearIndex.get(0).getConAudit().getAuditFor() : "";
		case ThreeYearWeightedAverage:
		case ThreeYearAverage:
			String yearResult = "";
			for (OshaAudit osha : data.get(type))
				yearResult += (yearResult.isEmpty()) ? "Years: " + osha.getConAudit().getAuditFor() : ", "
						+ osha.getConAudit().getAuditFor();

			return yearResult;
		}

		throw new RuntimeException("OshaOrganizer.getAuditFor() is undefined for MultiYearScope = " + year);
	}

	public OshaAudit getOshaAudit(OshaType type, MultiYearScope year) {
		List<OshaAudit> yearIndex = data.get(type);

		if (yearIndex.isEmpty())
			return null;

		switch (year) {
		case LastYearOnly:
			return yearIndex.get(yearIndex.size() - 1);
		case TwoYearsAgo:
			return (yearIndex.size() > 1) ? yearIndex.get(yearIndex.size() - 2) : null;
		case ThreeYearsAgo:
			return (yearIndex.size() > 2) ? yearIndex.get(0) : null;
		case ThreeYearWeightedAverage:
			OshaAudit weightedAvg = new OshaAudit();
			
			boolean weightedAllVerified = true;
			Date weightedLastVerified = new Date();

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
				// Need to set a proper verification value on average OSHAs
				// for inserting verified tag in ContractorFlagETL answer2
				if(!osha.isVerified())
					weightedAllVerified = false;
				if(osha.getVerifiedDate() != null)
					weightedLastVerified = osha.getVerifiedDate();

				if (osha.getManHours() > 0) {
					weightedCount++;

					// calculating cumulative values
					weightedManHours += osha.getManHours();
					weightedFatalities += osha.getFatalities();
					weightedLostWorkCases += osha.getLostWorkCases();
					weightedLostWorkDays += osha.getLostWorkDays();
					weightedInjuryIllnessCases += osha.getInjuryIllnessCases();
					weightedRestrictedWorkCases += osha.getRestrictedWorkCases();
					weightedRecordableTotal += osha.getRecordableTotal(); // Total
					// Recordable Incidents
					weightedFirstAidInjuries += osha.getFirstAidInjuries();
					weightedModifiedWorkDay += osha.getModifiedWorkDay();

					if(osha.getCad7() != null)
						weightedCad7 += osha.getCad7();
					if(osha.getNeer() != null)
						weightedNeer += osha.getNeer();
				}
			}

			if (weightedCount == 0)
				return null;
			
			// If all are verified, set verified to last recorded date
			if(weightedAllVerified)
				weightedAvg.setVerifiedDate(weightedLastVerified);

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
			
			// setting individual values to their average value for display
			weightedAvg.setManHours(weightedAvg.getManHours()/weightedCount);
			weightedAvg.setFatalities(weightedAvg.getFatalities()/weightedCount);
			weightedAvg.setLostWorkCases(weightedAvg.getLostWorkCases()/weightedCount);
			weightedAvg.setLostWorkDays(weightedAvg.getLostWorkDays()/weightedCount);
			weightedAvg.setInjuryIllnessCases(weightedAvg.getInjuryIllnessCases()/weightedCount);
			weightedAvg.setRestrictedWorkCases(weightedAvg.getRestrictedWorkCases()/weightedCount);
			weightedAvg.setRecordableTotal(weightedAvg.getRecordableTotal()/weightedCount);
			weightedAvg.setFirstAidInjuries(weightedAvg.getFirstAidInjuries()/weightedCount);
			weightedAvg.setModifiedWorkDay(weightedAvg.getModifiedWorkDay()/weightedCount);

			return weightedAvg;
		case ThreeYearAverage:
			OshaAudit straightAvg = new OshaAudit();

			boolean straightAllVerified = true;
			Date straightLastVerified = null;
			
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
				
				// Need to set a proper verification value on average OSHAs
				// for inserting verified tag in ContractorFlagETL answer2
				if(!osha.isVerified())
					straightAllVerified = false;
				if(osha.getVerifiedDate() != null)
					straightLastVerified = osha.getVerifiedDate();

				if (osha.getManHours() > 0) {
					straightCount++;

					// calculating cumulative values
					straightManHours += osha.getManHours();
					straightFatalities += osha.getFatalities();
					straightLostWorkCases += osha.getLostWorkCases();
					straightLostWorkDays += osha.getLostWorkDays();
					straightInjuryIllnessCases += osha.getInjuryIllnessCases();
					straightRestrictedWorkCases += osha.getRestrictedWorkCases();
					straightRecordableTotal += osha.getRecordableTotal();
					straightFirstAidInjuries += osha.getFirstAidInjuries();
					straightModifiedWorkDay += osha.getModifiedWorkDay();

					if(osha.getCad7() != null)
						straightCad7 += osha.getCad7();
					if(osha.getNeer() != null)
						straightNeer += osha.getNeer();

					straightTrir += osha.getRecordableTotalRate();
					straightLwcr += osha.getLostWorkCasesRate();
					straightDart += osha.getRestrictedDaysAwayRate();
					straightSeverityRate += osha.getRestrictedOrJobTransferDays();
				}
			}

			if (straightCount == 0)
				return null;

			// If all are verified, set verified to last recorded date
			if(straightAllVerified)
				straightAvg.setVerifiedDate(straightLastVerified);
			
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
			
			// setting individual values to their average value for display
			straightAvg.setManHours(straightAvg.getManHours()/straightCount);
			straightAvg.setFatalities(straightAvg.getFatalities()/straightCount);
			straightAvg.setLostWorkCases(straightAvg.getLostWorkCases()/straightCount);
			straightAvg.setLostWorkDays(straightAvg.getLostWorkDays()/straightCount);
			straightAvg.setInjuryIllnessCases(straightAvg.getInjuryIllnessCases()/straightCount);
			straightAvg.setRestrictedWorkCases(straightAvg.getRestrictedWorkCases()/straightCount);
			straightAvg.setRecordableTotal(straightAvg.getRecordableTotal()/straightCount);
			straightAvg.setFirstAidInjuries(straightAvg.getFirstAidInjuries()/straightCount);
			straightAvg.setModifiedWorkDay(straightAvg.getModifiedWorkDay()/straightCount);

			return straightAvg;
		}
		return null;
	}
	
	public String getAnswer2(OshaType type, MultiYearScope year, OshaRateType rateType){
		String auditFor = getAuditFor(type,year);
		
		// decorate
		// Appending absolute answer for answer2 for Naics OSHAs
		if (rateType.equals(OshaRateType.LwcrNaics)) {
			auditFor += "<br/>Contractor Answer: "
					+ PicsActionSupport.format(getRate(type, year,
							OshaRateType.LwcrAbsolute));
		} else if (rateType.equals(OshaRateType.TrirNaics)) {
			auditFor += "<br/>Contractor Answer: "
					+ PicsActionSupport.format(getRate(type, year,
							OshaRateType.TrirAbsolute));
		}
		
		// conditionally add verified tag
		if(isVerified(type, year)){
			auditFor += "<br/><span class=\"verified\">Verified</span>";
		}
		
		return auditFor;
	}
}
