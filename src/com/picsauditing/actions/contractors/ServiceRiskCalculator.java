package com.picsauditing.actions.contractors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public class ServiceRiskCalculator {
	// Risk types
	public static final String SAFETY = "Safety";
	public static final String PRODUCT = "Product";
	public static final String TRANSPORTATION = "Transportation";
	// Service Safety Evaluation
	public static final int SAFETY_CONDUCTED_FROM_OFFICE = 12341;
	public static final int SAFETY_HAND_POWER_PNEUMATIC_TOOLS = 12342;
	public static final int SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT = 12343;
	public static final int SAFETY_PERMIT_TO_WORK = 12344;
	public static final int SAFETY_MOBILE_EQUIPMENT = 12345;
	public static final int SAFETY_PERFORMS_HIGH_RISK = 12346;
	// Self Service Safety Evaluation
	public static final int SAFETY_LEVEL_HAZARD_EXPOSURE = 12347;

	public final int[] SAFETY_QUESTIONS = new int[] { SAFETY_CONDUCTED_FROM_OFFICE, SAFETY_HAND_POWER_PNEUMATIC_TOOLS,
			SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT, SAFETY_PERMIT_TO_WORK, SAFETY_MOBILE_EQUIPMENT,
			SAFETY_PERFORMS_HIGH_RISK, SAFETY_LEVEL_HAZARD_EXPOSURE };

	public LowMedHigh getRiskLevel(AuditData auditData) {
		switch (auditData.getQuestion().getId()) {
			case SAFETY_CONDUCTED_FROM_OFFICE:
				return determineRiskLevel(auditData.getAnswer(), LowMedHigh.Low, LowMedHigh.Med);
			case SAFETY_HAND_POWER_PNEUMATIC_TOOLS:
			case SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT:
				return determineRiskLevel(auditData.getAnswer(), LowMedHigh.Med, LowMedHigh.Low);
			case SAFETY_PERMIT_TO_WORK:
			case SAFETY_MOBILE_EQUIPMENT:
			case SAFETY_PERFORMS_HIGH_RISK:
				return determineRiskLevel(auditData.getAnswer(), LowMedHigh.High, LowMedHigh.Med);
			default:
				return LowMedHigh.None;
		}
	}

	public Map<String, LowMedHigh> getHighestRiskLevel(List<AuditData> auditDatas) {
		Map<String, LowMedHigh> highestRisks = new HashMap<String, LowMedHigh>();
		
		for (AuditData auditData : auditDatas) {
			if (isSafetyQuestion(auditData.getQuestion().getId())) {
				if (highestRisks.containsKey(SAFETY)) {
					LowMedHigh currentSafety = getRiskLevel(auditData);
				}
			}
			
			LowMedHigh risk = getRiskLevel(auditData);
			LowMedHigh highestRisk = LowMedHigh.None;

			if (risk.ordinal() > highestRisk.ordinal()) {
				highestRisk = risk;
			}
		}

		return highestRisks;
	}
	
	private boolean isSafetyQuestion(int auditQuestionID) {
		for (int safetyQuestion : SAFETY_QUESTIONS) {
			if (auditQuestionID == safetyQuestion) {
				return true;
			}
		}
		
		return false;
	}

	private LowMedHigh determineRiskLevel(String answer, LowMedHigh yes, LowMedHigh no) {
		if (!Strings.isEmpty(answer)) {
			if (YesNo.Yes == YesNo.valueOf(answer)) {
				return yes;
			} else {
				return no;
			}
		}

		return LowMedHigh.None;
	}
}
