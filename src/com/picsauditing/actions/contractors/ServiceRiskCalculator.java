package com.picsauditing.actions.contractors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public class ServiceRiskCalculator {
	public enum RiskCategory {
		SAFETY, PRODUCT, TRANSPORTATION, SELF_SAFETY, SELF_PRODUCT;
	}

	public LowMedHigh getRiskLevel(AuditData auditData) {
		SafetyAssessment safetyAssessment = SafetyAssessment.getSafetyAssessment(auditData.getQuestion().getId());
		if (safetyAssessment != null) {
			if (safetyAssessment.isSelfEvaluation()) {
				return determineSelfEvaluation(auditData);
			}

			return determineRiskLevel(auditData.getAnswer(), safetyAssessment.getYes(), safetyAssessment.getNo());
		}

		ProductAssessment productAssessment = ProductAssessment.getProductAssessment(auditData.getQuestion().getId());
		if (productAssessment != null) {
			if (productAssessment.isSelfEvaluation()) {
				return determineSelfEvaluation(auditData);
			}

			return determineRiskLevel(auditData.getAnswer(), productAssessment.getYes(), productAssessment.getNo());
		}

		return LowMedHigh.None;
	}

	public Map<RiskCategory, LowMedHigh> getHighestRiskLevel(Collection<AuditData> auditDatas) {
		Map<RiskCategory, LowMedHigh> highestRisks = new HashMap<RiskCategory, LowMedHigh>();

		for (RiskCategory riskCategory : RiskCategory.values()) {
			highestRisks.put(riskCategory, LowMedHigh.None);
		}

		for (AuditData auditData : auditDatas) {
			int auditQuestionID = auditData.getQuestion().getId();
			LowMedHigh risk = getRiskLevel(auditData);

			if (isSafetyQuestion(auditQuestionID)) {
				if (SafetyAssessment.isSelfAssessment(auditQuestionID)) {
					highestRisks.put(RiskCategory.SELF_SAFETY, risk);
				} else if (highestRisks.get(RiskCategory.SAFETY).ordinal() < risk.ordinal()) {
					highestRisks.put(RiskCategory.SAFETY, risk);
				}
			}

			if (isProductQuestion(auditQuestionID)) {
				if (ProductAssessment.isSelfAssessment(auditQuestionID)) {
					highestRisks.put(RiskCategory.SELF_PRODUCT, risk);
				} else if (highestRisks.get(RiskCategory.PRODUCT).ordinal() < risk.ordinal()) {
					highestRisks.put(RiskCategory.PRODUCT, risk);
				}
			}
		}

		return highestRisks;
	}

	private boolean isSafetyQuestion(int auditQuestionID) {
		SafetyAssessment safetyAssessment = SafetyAssessment.getSafetyAssessment(auditQuestionID);
		if (safetyAssessment != null) {
			return true;
		}

		return false;
	}

	private boolean isProductQuestion(int auditQuestionID) {
		ProductAssessment productAssessment = ProductAssessment.getProductAssessment(auditQuestionID);
		if (productAssessment != null) {
			return true;
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
		String answer = auditData.getAnswer();

		if (!Strings.isEmpty(answer)) {
			if ("Medium".equals(answer)) {
				return LowMedHigh.Med;
			}

			return LowMedHigh.valueOf(answer);
		}

		return LowMedHigh.None;
	}
}
