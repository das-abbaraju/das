package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.YearList;

public class OshaOrganizer {

	// OSHA Audits will be sorted by their auditYears
	private Map<OshaType, ArrayList<OshaAudit>> data = new HashMap<OshaType, ArrayList<OshaAudit>>();
	private Map<OshaType, YearList> dataYears = new HashMap<OshaType, YearList>();

	public OshaOrganizer(List<ContractorAudit> audits) {
		data.put(OshaType.OSHA, new ArrayList<OshaAudit>());
		data.put(OshaType.MSHA, new ArrayList<OshaAudit>());
		data.put(OshaType.COHS, new ArrayList<OshaAudit>());
		dataYears.put(OshaType.OSHA, new YearList());
		dataYears.put(OshaType.MSHA, new YearList());
		dataYears.put(OshaType.COHS, new YearList());

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
									dataYears.get(osha.getType()).add(osha.getConAudit().getAuditFor());
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean isVerified(OshaType type, MultiYearScope year) {
		if (data.get(type).isEmpty())
			return false;

		OshaAudit oshaAudit = getOshaAudit(type, year);
		if (oshaAudit == null)
			return false;
		return oshaAudit.isVerified();
	}

	public Float getRate(OshaType type, MultiYearScope year, OshaRateType rateType) {
		OshaAudit oshaAudit = getOshaAudit(type, year);
		if (oshaAudit == null)
			return null;
		return oshaAudit.getRate(rateType);
	}

	public OshaAudit getOshaAudit(OshaType type, MultiYearScope scope) {
		if (data.get(type).isEmpty())
			return null;

		if (MultiYearScope.ThreeYearAverage.equals(scope))
			return getAverageOshaAudit(type);

		Integer targetYear = dataYears.get(type).getYearForScope(scope);
		if (targetYear == null)
			return null;

		for (OshaAudit oshaAudit : data.get(type)) {
			if (oshaAudit.getConAudit().getAuditFor().equals(targetYear.toString())) {
				return oshaAudit;
			}
		}

		// TODO Maybe throw Exception because this should never happen
		return null;
	}

	private OshaAudit getAverageOshaAudit(OshaType type) {
		// TODO Move this into a separate utility class

		OshaAudit straightAvg = new OshaAudit();
		straightAvg.setConAudit(new ContractorAudit());

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
		
		String yearsOfAverage = "";

		for (OshaAudit osha : data.get(type)) {
			if (osha != null && straightCount < 3) {
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
					
					if (yearsOfAverage.length() > 0) {
						yearsOfAverage +=", ";
					}
					yearsOfAverage +=osha.getConAudit().getAuditFor();
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

		straightAvg.getConAudit().setAuditFor("Years: " + yearsOfAverage);

		return straightAvg;
	}

	public String getAnswer2(OshaType type, MultiYearScope year, OshaRateType rateType) {
		OshaAudit oshaAudit = getOshaAudit(type, year);
		if (oshaAudit == null || oshaAudit.getConAudit() == null)
			return "";
		String auditFor = oshaAudit.getConAudit().getAuditFor();

		// conditionally add verified tag
		if (isVerified(type, year)) {
			auditFor += "<br/><span class=\"verified\">Verified</span>";
		}

		return auditFor;
	}

	static private OshaType getOshaType(AuditQuestion auditQuestion) {
		if (auditQuestion.getId() == 2065)
			return OshaType.MSHA;
		if (auditQuestion.getId() == 2066)
			return OshaType.COHS;
		return OshaType.OSHA;
	}
}
