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
				yearResult = (yearResult == null) ? osha.getConAudit().getAuditFor() : ", "
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
		case ThreeYearAverage:
			OshaAudit straightAvg = new OshaAudit();

			float straightManHours = 0;
			float straightFatalities = 0;
			float straightInjuries = 0;
			float straightLwc = 0;
			float straightLwcr = 0;
			float straightLwd = 0;
			float straightTri = 0;
			float straightTrir = 0;
			float straightRwc = 0;
			float straightDart = 0;
			float straightNeer = 0;
			float straightCad7 = 0;

			int straightCount = 0;
			for (OshaAudit osha : yearIndex) {
				if (osha.getManHours() > 0) {
					straightCount++;
					straightAvg.setFactor(osha.getFactor());

					straightManHours += osha.getManHours();
					straightFatalities += osha.getFatalities();
					straightInjuries += osha.getInjuryIllnessCasesRate();
					straightLwc += osha.getLostWorkCases();
					straightLwcr += osha.getLostWorkCasesRate();
					straightLwd += osha.getLostWorkDays();
					straightTri += osha.getRecordableTotal();
					straightTrir += osha.getRecordableTotalRate();
					straightRwc += osha.getRestrictedWorkCases();
					straightDart += osha.getRestrictedDaysAwayRate();
					if (osha.getNeer() != null)
						straightNeer += osha.getNeer();
					if (osha.getCad7() != null)
						straightCad7 += osha.getCad7();
				}
			}

			if (straightCount == 0)
				return null;

			straightAvg.setManHours(Math.round(straightManHours / straightCount));
			straightAvg.setFatalities(Math.round(straightFatalities / straightCount));
			straightAvg.setInjuryIllnessCases(Math.round(straightInjuries / straightCount));
			straightAvg.setLostWorkCases(Math.round(straightLwc / straightCount));
			straightAvg.setLostWorkCasesRate(straightLwcr / straightCount);
			straightAvg.setLostWorkDays(Math.round(straightLwd / straightCount));
			straightAvg.setRecordableTotal(Math.round(straightTri / straightCount));
			straightAvg.setRecordableTotalRate(straightTrir / straightCount);
			straightAvg.setRestrictedWorkCases(Math.round(straightRwc / straightCount));
			straightAvg.setRestrictedDaysAwayRate(straightDart / straightCount);
			straightAvg.setNeer(straightNeer / straightCount);
			straightAvg.setCad7(straightCad7 / straightCount);
			return straightAvg;
		case ThreeYearWeightedAverage:
			OshaAudit avg = new OshaAudit();

			float manHours = 0;
			float fatalities = 0;
			float injuries = 0;
			float lwc = 0;
			float lwcr = 0;
			float lwd = 0;
			float tri = 0;
			float rwc = 0;
			float dart = 0;
			float neer = 0;
			float cad7 = 0;

			int count = 0;
			for (OshaAudit osha : yearIndex) {
				if (osha.getManHours() > 0) {
					count++;
					avg.setFactor(osha.getFactor());

					manHours += osha.getManHours();
					fatalities += osha.getFatalities();
					injuries += osha.getInjuryIllnessCases();
					lwc += osha.getLostWorkCases();
					lwd += osha.getLostWorkDays();
					tri += osha.getRecordableTotal();
					rwc += osha.getRestrictedWorkCases();
					dart += osha.getRestrictedDaysAwayRate();
					if (osha.getNeer() != null)
						neer += osha.getNeer();
					if (osha.getCad7() != null)
						cad7 += osha.getCad7();
				}
			}

			if (count == 0)
				return null;

			avg.setManHours(Math.round(manHours / count));
			avg.setFatalities(Math.round(fatalities / count));
			avg.setInjuryIllnessCases(Math.round(injuries / count));
			avg.setLostWorkCases(Math.round(lwc / count));
			avg.setLostWorkCasesRate(lwc * 200000 / (float)avg.getManHours());
			avg.setLostWorkDays(Math.round(lwd / count));
			avg.setRecordableTotal(Math.round(tri / count));
			avg.setRecordableTotalRate(tri * 200000 / (float)avg.getManHours());
			avg.setRestrictedWorkCases(Math.round(rwc / count));
			avg.setRestrictedDaysAwayRate(dart * 200000 / (float)avg.getManHours());
			avg.setNeer(neer / count);
			avg.setCad7(cad7 / count);
			return avg;
		}
		return null;
	}
}
