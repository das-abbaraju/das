package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
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
			if (contractorAudit.hasCaoStatus(AuditStatus.Complete)) {
				for (AuditData auditData : contractorAudit.getData()) {
					if (auditData.getQuestion().getId() == 2064 || auditData.getQuestion().getId() == 2065
							|| auditData.getQuestion().getId() == 2066) {
						OshaType oshaType = getOshaType(auditData.getQuestion());
						if ("Yes".equals(auditData.getAnswer())) {
							for (OshaAudit osha : contractorAudit.getOshas()) {
								if (osha.isCorporate() && osha.getType().equals(oshaType)) {
									data.get(osha.getType()).add(osha);
								}
							}
						}
					}
				}
			}
		}

		trim(OshaType.OSHA);
		trim(OshaType.MSHA);
		trim(OshaType.COHS);
	}

	/**
	 * If for a given OshaType, there are more than 3 valid year, then we remove the last year in the list, as that will
	 * be the oldest year
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

		// removing the 4th year
		list.remove(3);
		// We have 4 years worth of data, get rid of either the first or the
		// last
		// We trim the fourth year ONLY if it's not verified but all three
		// previous years are.
		// if (!list.get(3).isVerified() && list.get(2).isVerified()
		// && list.get(1).isVerified() && list.get(0).isVerified()) {
		// PicsLogger.log("removed fourthYear" +
		// list.get(3).getConAudit().getAuditFor());
		// list.remove(3);
		// } else {
		// list.remove(0);
		// }
	}

	public boolean isVerified(OshaType type, MultiYearScope year) {
		if (data.get(type).isEmpty())
			return false;

		PicsLogger.log("OshaOrganizer.isVerified(" + type + "," + year + ")");
		OshaAudit oshaAudit = getOshaAudit(type, year);

		return oshaAudit != null && oshaAudit.isVerified();
	}

	public Float getRate(OshaType type, MultiYearScope year, OshaRateType rateType) {
		if (data.get(type).isEmpty())
			return null;

		PicsLogger.log("OshaOrganizer.getRate(" + type + "," + year + "," + rateType + ")");
		
		OshaAudit oshaAudit = getOshaAudit(type, year);
		int validYear = DateBean.getCurrentYear() - 1;

		switch (year) {
		case LastYearOnly:
			if (oshaAudit != null && isLast2Years(oshaAudit.getConAudit().getAuditFor(), validYear)) {
				return oshaAudit.getRate(rateType);
			}
			return null;
		case TwoYearsAgo:
			if (oshaAudit != null && isLast2Years(oshaAudit.getConAudit().getAuditFor(), validYear - 1)) {
				return oshaAudit.getRate(rateType);
			}
			return null;
		case ThreeYearsAgo:
			if (oshaAudit != null && isLast2Years(oshaAudit.getConAudit().getAuditFor(), validYear - 2)) {
				return oshaAudit.getRate(rateType);
			}
			return null;
		case ThreeYearAverage:
			int avgCount = 0;
			Float rate = 0f;
			for (OshaAudit osha : data.get(type)) {
				if (osha != null && osha.getManHours() > 0) {
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
		if (data.get(type).isEmpty())
			return "";

		if (MultiYearScope.ThreeYearAverage.equals(year)) {
			String yearResult = "";

			for (OshaAudit osha : data.get(type)) {
				if (osha != null) {
					yearResult += (yearResult.isEmpty()) ? "Years: " + osha.getConAudit().getAuditFor() : ", "
							+ osha.getConAudit().getAuditFor();
				}
			}

			return yearResult;
		} else {
			for (OshaAudit oshaAudit : data.get(type)) {
				int auditFor = Integer.parseInt(oshaAudit.getConAudit().getAuditFor());

				if (year.equals(MultiYearScope.getScopeFromYear(auditFor)))
					return "Year: " + auditFor;
			}
		}

		throw new RuntimeException("OshaOrganizer.getAuditFor() is undefined for MultiYearScope = " + year);
	}

	public OshaAudit getOshaAudit(OshaType type, MultiYearScope year) {
		if (data.get(type).isEmpty())
			return null;

		if (MultiYearScope.ThreeYearAverage.equals(year)) {
			OshaAudit straightAvg = new OshaAudit();

			boolean straightAllVerified = true;
			Date straightLastVerified = null;

			float straightManHours = 0;
			float straightFatalities = 0;
			float straightLostWorkCases = 0;
			float straightLostWorkDays = 0;
			float straightInjuryIllnessCases = 0;
			float straightRestrictedWorkCases = 0;
			float straightRecordableTotal = 0;
			float straightFirstAidInjuries = 0;
			float straightModifiedWorkDay = 0;

			float straightTrir = 0;
			float straightLwcr = 0;
			float straightDart = 0;
			float straightSeverityRate = 0;
			float straightCad7 = 0;
			float straightNeer = 0;

			int straightCount = 0;

			for (OshaAudit osha : data.get(type)) {
				if (osha != null) {
					straightAvg.setFactor(osha.getFactor());

					// Need to set a proper verification value on average OSHAs
					// for inserting verified tag in ContractorFlagETL answer2
					if (!osha.isVerified())
						straightAllVerified = false;
					if (osha.getVerifiedDate() != null)
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

						if (osha.getCad7() != null)
							straightCad7 += osha.getCad7();
						if (osha.getNeer() != null)
							straightNeer += osha.getNeer();

						straightTrir += osha.getRecordableTotalRate();
						straightLwcr += osha.getLostWorkCasesRate();
						straightDart += osha.getRestrictedDaysAwayRate();
						straightSeverityRate += osha.getRestrictedOrJobTransferDays();
					}
				}
			}

			if (straightCount == 0)
				return null;

			// If all are verified, set verified to last recorded date
			if (straightAllVerified)
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
			straightAvg.setManHours(straightAvg.getManHours() / straightCount);
			straightAvg.setFatalities(straightAvg.getFatalities() / straightCount);
			straightAvg.setLostWorkCases(straightAvg.getLostWorkCases() / straightCount);
			straightAvg.setLostWorkDays(straightAvg.getLostWorkDays() / straightCount);
			straightAvg.setInjuryIllnessCases(straightAvg.getInjuryIllnessCases() / straightCount);
			straightAvg.setRestrictedWorkCases(straightAvg.getRestrictedWorkCases() / straightCount);
			straightAvg.setRecordableTotal(straightAvg.getRecordableTotal() / straightCount);
			straightAvg.setFirstAidInjuries(straightAvg.getFirstAidInjuries() / straightCount);
			straightAvg.setModifiedWorkDay(straightAvg.getModifiedWorkDay() / straightCount);

			return straightAvg;
		} else {
			for (OshaAudit oshaAudit : data.get(type)) {
				int auditFor = Integer.parseInt(oshaAudit.getConAudit().getAuditFor());

				if (year.equals(MultiYearScope.getScopeFromYear(auditFor)))
					return oshaAudit;
			}
		}

		return null;
	}

	public String getAnswer2(OshaType type, MultiYearScope year, OshaRateType rateType) {
		String auditFor = getAuditFor(type, year);

		// conditionally add verified tag
		if (isVerified(type, year)) {
			auditFor += "<br/><span class=\"verified\">Verified</span>";
		}

		return auditFor;
	}

	private OshaType getOshaType(AuditQuestion auditQuestion) {
		if (auditQuestion.getId() == 2065)
			return OshaType.MSHA;
		if (auditQuestion.getId() == 2066)
			return OshaType.COHS;
		return OshaType.OSHA;
	}

	private boolean isLast2Years(String auditFor, int year) {
		if (Integer.toString(year).equals(auditFor) || Integer.toString(year - 1).equals(auditFor))
			return true;
		return false;
	}

}
