package com.picsauditing.actions.contractors.risk;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.LowMedHigh;

public class ServiceRiskCalculator {
	private static final Logger logger = LoggerFactory.getLogger(ServiceRiskCalculator.class);

	public enum RiskCategory {
		SAFETY, PRODUCT, TRANSPORTATION, SELF_SAFETY, SELF_PRODUCT;
	}

	public LowMedHigh getRiskLevel(AuditData auditData) {
		try {
			RiskAssessment assessment = getAssessment(auditData.getQuestion().getId());
			return assessment.getRiskLevelBasedOn(auditData.getAnswer());
		} catch (Exception e) {
			logger.error("Unable to parse risk assessment for auditDataID {} with questionID {} and answer {}\n{}",
					new Object[] { auditData.getId(), auditData.getQuestion().getId(), auditData.getAnswer(), e });

			return LowMedHigh.None;
		}
	}

	public Map<RiskCategory, LowMedHigh> getHighestRiskLevelMap(Collection<AuditData> auditDatas) {
		Map<RiskCategory, LowMedHigh> highestRisks = new HashMap<RiskCategory, LowMedHigh>();
		initializeRiskCategoriesToNone(highestRisks);

		for (AuditData auditData : auditDatas) {
			int auditQuestionID = auditData.getQuestion().getId();
			LowMedHigh calculatedRisk = getRiskLevel(auditData);

			try {
				RiskAssessment assessment = getAssessment(auditQuestionID);
				RiskCategory category = getCategory(assessment);
				LowMedHigh currentRisk = highestRisks.get(category);

				if (calculatedRiskIsHigher(calculatedRisk, currentRisk)) {
					highestRisks.put(category, calculatedRisk);
				}
			} catch (Exception e) {
				logger.error("Unable to parse risk assessment for auditDataID {} with questionID {} and answer {}\n{}",
						new Object[] { auditData.getId(), auditData.getQuestion().getId(), auditData.getAnswer(), e });
			}
		}

		return highestRisks;
	}

	private RiskAssessment getAssessment(int questionID) throws Exception {
        SafetyAssessment safetyAssessment = SafetyAssessment.findByQuestionID(questionID);
        if (safetyAssessment != null) {
            return safetyAssessment;
        }

        ProductAssessment productAssessment = ProductAssessment.findByQuestionID(questionID);
        if (productAssessment != null) {
            return productAssessment;
        }

        TransportationAssessment transportationAssessment = TransportationAssessment.findByQuestionID(questionID);
        if (transportationAssessment != null) {
            return transportationAssessment;
        }

		throw new IllegalArgumentException();
	}

	private void initializeRiskCategoriesToNone(Map<RiskCategory, LowMedHigh> highestRisks) {
		for (RiskCategory riskCategory : RiskCategory.values()) {
			highestRisks.put(riskCategory, LowMedHigh.None);
		}
	}

	private RiskCategory getCategory(RiskAssessment assessment) throws Exception {
		if (assessment != null) {
			if (assessment.isQuestionSelfEvaluation()) {
				if (assessment instanceof SafetyAssessment) {
					return RiskCategory.SELF_SAFETY;
				} else if (assessment instanceof ProductAssessment) {
					return RiskCategory.SELF_PRODUCT;
				}
			} else {
				if (assessment instanceof SafetyAssessment) {
					return RiskCategory.SAFETY;
				} else if (assessment instanceof ProductAssessment) {
					return RiskCategory.PRODUCT;
				} else if (assessment instanceof TransportationAssessment) {
					return RiskCategory.TRANSPORTATION;
				}
			}
		}

		throw new IllegalArgumentException();
	}

	private boolean calculatedRiskIsHigher(LowMedHigh risk, LowMedHigh currentRisk) {
		return currentRisk.ordinal() < risk.ordinal();
	}
}