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
	private Map<OshaType, Map<Integer, OshaAudit>> data = new HashMap<OshaType, Map<Integer, OshaAudit>>();

	public OshaOrganizer(List<ContractorAudit> audits) {
		PicsLogger.log("Constructing OshaOrganizer");
		data.put(OshaType.OSHA, new HashMap<Integer, OshaAudit>());
		data.put(OshaType.MSHA, new HashMap<Integer, OshaAudit>());
		data.put(OshaType.COHS, new HashMap<Integer, OshaAudit>());

		for (ContractorAudit contractorAudit : audits) {
			if (contractorAudit.getAuditType().isAnnualAddendum()
					&& (contractorAudit.getAuditStatus().isActiveSubmitted() || contractorAudit.getAuditStatus()
							.isResubmitted())) {
				for (OshaAudit osha : contractorAudit.getOshas()) {
					if (osha.isCorporate()) {
						data.get(osha.getType()).put(Integer.parseInt(contractorAudit.getAuditFor()), osha);
					}
				}
			}
		}

		trim(OshaType.OSHA);
		trim(OshaType.MSHA);
		trim(OshaType.COHS);
	}

	/**
	 * If for a given OshaType, there are more than 3 valid year, then trim one of the years. We always trim either the
	 * first year (2006) or the fourth year (2009). We trim the fourth year ONLY if it's not verified but all three
	 * previous years are.
	 * 
	 * @param type
	 */
	private void trim(OshaType type) {
		List<Integer> yearIndex = getYearIndex(type);
		PicsLogger.log(type + " has [" + yearIndex.size() + "] entries");
		if (yearIndex.size() < 4)
			return;

		if (yearIndex.size() > 4)
			throw new RuntimeException("Found [" + yearIndex.size() + "] OshaAudits of type " + type);

		// We have 4 years worth of data, get rid of either the first or the last
		int firstYear = yearIndex.get(0);
		int secondYear = yearIndex.get(1);
		int thirdYear = yearIndex.get(2);
		int fourthYear = yearIndex.get(3);
		Map<Integer, OshaAudit> typeData = data.get(type);

		if (!typeData.get(fourthYear).isVerified() && typeData.get(firstYear).isVerified()
				&& typeData.get(secondYear).isVerified() && typeData.get(thirdYear).isVerified()) {
			PicsLogger.log("removed fourthYear" + typeData.get(fourthYear).getConAudit().getAuditFor());
			typeData.remove(fourthYear);
		} else {
			PicsLogger.log("removed firstYear " + typeData.get(firstYear).getConAudit().getAuditFor());
			typeData.remove(firstYear);
		}
	}

	public OshaAudit getOshaAudit(OshaType type, MultiYearScope year) {
		Map<Integer, OshaAudit> typeData = data.get(type);
		List<Integer> yearIndex = getYearIndex(type);

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
			return typeData.get(yearIndex.get(index));
		case ThreeYearAverage:
			throw new RuntimeException(
					"Straight ThreeYearAverage is not supported via getOshaAudit(OshaType type, MultiYearScope year)");
		case ThreeYearWeightedAverage:
			OshaAudit avg = new OshaAudit();

			float manHours = 0;
			float fatalities = 0;
			float injuries = 0;
			float lwc = 0;
			float lwcr = 0;
			float lwd = 0;
			float tri = 0;
			// float trir = 0;
			float rwc = 0;
			float dart = 0;
			float neer = 0;
			float cad7 = 0;

			int count = 0;
			for (OshaAudit osha : typeData.values()) {
				if (osha.getManHours() > 0) {
					count++;
					avg.setFactor(osha.getFactor());

					manHours += osha.getManHours();
					fatalities += osha.getFatalities();
					injuries += osha.getInjuryIllnessCases();
					lwc += osha.getLostWorkCases();
					lwcr += osha.getLostWorkCasesRate();
					lwd += osha.getLostWorkDays();
					tri += osha.getRecordableTotal();
					// trir += osha.getRecordableTotalRate();
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
			avg.setLostWorkCasesRate(lwcr / count);
			avg.setLostWorkDays(Math.round(lwd / count));
			avg.setRecordableTotal(Math.round(tri / count));
			// avg.setRecordableTotalRate(trir / count);
			avg.setRestrictedWorkCases(Math.round(rwc / count));
			avg.setRestrictedDaysAwayRate(dart / count);
			avg.setNeer(neer / count);
			avg.setCad7(cad7 / count);
			return avg;
		}
		return null;
	}

	public boolean isVerified(OshaType type, MultiYearScope year, OshaRateType rateType) {
		switch (year) {
		case LastYearOnly:
		case TwoYearsAgo:
		case ThreeYearsAgo:
		case ThreeYearWeightedAverage:
			return getOshaAudit(type, year).isVerified();
		case ThreeYearAverage:
			for (OshaAudit osha : data.get(type).values()) {
				if (!osha.isVerified())
					return false;
			}
			return true;
		}
		return true;
	}

	public float getRate(OshaType type, MultiYearScope year, OshaRateType rateType) {
		PicsLogger.log("OshaOrganizer.getRate(" + type + "," + year + "," + rateType + ")");
		switch (year) {
		case LastYearOnly:
		case TwoYearsAgo:
		case ThreeYearsAgo:
		case ThreeYearWeightedAverage:
			OshaAudit oshaAudit = getOshaAudit(type, year);
			if (oshaAudit == null) {
				PicsLogger.log("oshaAudit was missing");
				return -1f;
			}
			PicsLogger.log("found oshaAudit = " + oshaAudit);
			return oshaAudit.getRate(rateType);
		case ThreeYearAverage:
			int count = 0;
			float rate = 0f;
			for (OshaAudit osha : data.get(type).values()) {
				if (osha.getManHours() > 0) {
					count++;
					rate += osha.getRate(rateType);
				}
			}

			if (count == 0)
				return -1f;

			return rate / count;
		}

		throw new RuntimeException("getRate() is undefined for MultiYearScope = " + year);
	}

	private List<Integer> getYearIndex(OshaType forType) {
		List<Integer> yearIndex = new ArrayList<Integer>(data.get(forType).keySet());
		Collections.sort(yearIndex);
		return yearIndex;
	}

}
