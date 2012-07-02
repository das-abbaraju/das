package com.picsauditing.actions.contractors;

import java.util.Collection;
import java.util.HashMap;
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
	public static final String SELF_SAFETY = "Self Safety";
	public static final String SELF_PRODUCT = "Self Product";
	// Service Safety Evaluation
	public static final int SAFETY_CONDUCTED_FROM_OFFICE = 12341;
	public static final int SAFETY_HAND_POWER_PNEUMATIC_TOOLS = 12342;
	public static final int SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT = 12343;
	public static final int SAFETY_PERMIT_TO_WORK = 12344;
	public static final int SAFETY_MOBILE_EQUIPMENT = 12345;
	public static final int SAFETY_PERFORMS_HIGH_RISK = 12346;
	// Product Safety Evaluation
	public static final int PRODUCT_FAILURE_WORK_STOPPAGE = 7660;
	public static final int PRODUCT_DELIVERY_WORK_STOPPAGE = 7661;
	// Self Evaluations
	public static final int SAFETY_SELF_EVALUATION = 12347;
	public static final int PRODUCT_SELF_EVALUATION = 7679;

	private final int[] SAFETY_QUESTIONS = new int[] { SAFETY_CONDUCTED_FROM_OFFICE, SAFETY_HAND_POWER_PNEUMATIC_TOOLS,
			SAFETY_PERSONAL_PROTECTIVE_EQUIPMENT, SAFETY_PERMIT_TO_WORK, SAFETY_MOBILE_EQUIPMENT,
			SAFETY_PERFORMS_HIGH_RISK };

	private final int[] PRODUCT_QUESTIONS = new int[] { PRODUCT_FAILURE_WORK_STOPPAGE, PRODUCT_DELIVERY_WORK_STOPPAGE };

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
			case PRODUCT_FAILURE_WORK_STOPPAGE:
			case PRODUCT_DELIVERY_WORK_STOPPAGE:
				return determineRiskLevel(auditData.getAnswer(), LowMedHigh.High, LowMedHigh.Low);
			default:
				return LowMedHigh.None;
		}
	}

	public Map<String, LowMedHigh> getHighestRiskLevel(Collection<AuditData> auditDatas) {
		Map<String, LowMedHigh> highestRisks = new HashMap<String, LowMedHigh>();

		LowMedHigh safetyRisk = LowMedHigh.None;
		LowMedHigh productRisk = LowMedHigh.None;
		LowMedHigh transportationRisk = LowMedHigh.None;

		for (AuditData auditData : auditDatas) {
			int auditQuestionID = auditData.getQuestion().getId();
			LowMedHigh risk = getRiskLevel(auditData);

			if (isSafetyQuestion(auditQuestionID) && safetyRisk.ordinal() < risk.ordinal()) {
				safetyRisk = risk;
			}

			if (isProductQuestion(auditQuestionID) && productRisk.ordinal() < risk.ordinal()) {
				productRisk = risk;
			}

			if (auditQuestionID == SAFETY_SELF_EVALUATION) {
				highestRisks.put(SELF_SAFETY, determineSelfEvaluation(auditData));
			}

			if (auditQuestionID == PRODUCT_SELF_EVALUATION) {
				highestRisks.put(SELF_PRODUCT, determineSelfEvaluation(auditData));
			}
		}

		highestRisks.put(SAFETY, safetyRisk);
		highestRisks.put(PRODUCT, productRisk);
		highestRisks.put(TRANSPORTATION, transportationRisk);

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

	private boolean isProductQuestion(int auditQuestionID) {
		for (int productQuestion : PRODUCT_QUESTIONS) {
			if (auditQuestionID == productQuestion) {
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

	private LowMedHigh determineSelfEvaluation(AuditData auditData) {
		int auditQuestionID = auditData.getQuestion().getId();
		String answer = auditData.getAnswer();

		if (!Strings.isEmpty(answer)
				&& (auditQuestionID == SAFETY_SELF_EVALUATION || auditQuestionID == PRODUCT_SELF_EVALUATION)) {
			if ("Medium".equals(answer)) {
				return LowMedHigh.Med;
			}

			return LowMedHigh.valueOf(answer);
		}

		return LowMedHigh.None;
	}
}
