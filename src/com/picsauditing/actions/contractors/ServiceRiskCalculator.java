package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public class ServiceRiskCalculator {
	// Service Safety Evaluation
	public static final int SAFETY_CONDUCTED_FROM_OFFICE = 12341;
	public static final int SAFETY_HAND_POWER_PNEUMATIC_TOOLS = 12342;
	public static final int SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT = 12343;
	public static final int SAFETY_PERMIT_TO_WORK = 12344;
	public static final int SAFETY_MOBILE_EQUIPMENT = 12345;
	public static final int SAFETY_PERFORMS_HIGH_RISK = 12346;
	// Self Service Safety Evaluation
	public static final int SAFETY_LEVEL_HAZARD_EXPOSURE = 12347;

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

	public LowMedHigh getHighestRiskLevel(List<AuditData> auditDatas) {
		LowMedHigh highestRisk = LowMedHigh.None;

		for (AuditData auditData : auditDatas) {
			LowMedHigh risk = getRiskLevel(auditData);

			if (risk.ordinal() > highestRisk.ordinal()) {
				highestRisk = risk;
			}
		}

		return highestRisk;
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
