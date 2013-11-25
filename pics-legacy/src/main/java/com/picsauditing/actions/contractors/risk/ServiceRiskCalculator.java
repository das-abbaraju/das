package com.picsauditing.actions.contractors.risk;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.toggle.FeatureToggle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.LowMedHigh;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceRiskCalculator {
	private static final Logger logger = LoggerFactory.getLogger(ServiceRiskCalculator.class);

    @Autowired
    protected FeatureToggle featureToggleChecker;

	public enum RiskCategory {
		SAFETY, PRODUCT, TRANSPORTATION, SELF_SAFETY, SELF_PRODUCT;
	}

    public void calculateContractorsRiskLevels(ContractorAccount contractor, Map<Integer, AuditData> answerMap) {
        Collection<AuditData> auditList = answerMap.values();
        Map<RiskCategory, LowMedHigh> highestRisks = getHighestRiskLevelMap(auditList);

        // Calculated assessments
        LowMedHigh safety = highestRisks.get(RiskCategory.SAFETY);
        LowMedHigh product = highestRisks.get(RiskCategory.PRODUCT);
        LowMedHigh transportation = highestRisks.get(RiskCategory.TRANSPORTATION);
        // Self assessments
        LowMedHigh safetySelfRating = highestRisks.get(RiskCategory.SELF_SAFETY);
        LowMedHigh productSelfRating = highestRisks.get(RiskCategory.SELF_PRODUCT);

        // Contractor's assessments are the same (or higher?) than what
        // we've calculated
        if (!contractor.isMaterialSupplierOnly()) {
            if (safetySelfRating.isLessThan(safety)) {
                contractor.setSafetyRisk(safety);
            } else {
                contractor.setSafetyRisk(safetySelfRating);
            }
        }

        if (contractor.isMaterialSupplier()) {
            if (productSelfRating.isLessThan(product)) {
                contractor.setProductRisk(product);
            } else {
                contractor.setProductRisk(productSelfRating);
            }
        }

        if (contractor.isTransportationServices()) {
            // Safety risk also now includes transportation calculations
            if (safety.isLessThan(transportation)) {
                contractor.setSafetyRisk(transportation);
            }

            contractor.setTransportationRisk(transportation);
        }
        if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SAFETY_SENSITIVE_ENABLED)) {
            if (safety.isGreaterThan(LowMedHigh.Low)) {
                contractor.setSafetySensitive(true);
            }
        }
    }

	public LowMedHigh getRiskLevel(AuditData auditData) {
		try {
			RiskAssessment assessment = getAssessment(auditData.getQuestion().getId());
            if (assessment == null) {
                logger.info("Unable to parse risk assessment for auditDataID {} with questionID {} and answer {}\n{}",
                        new Object[] { auditData.getId(), auditData.getQuestion().getId(), auditData.getAnswer() });

                return LowMedHigh.None;
            }
			return assessment.getRiskLevel(auditData.getAnswer());
		} catch (Exception e) {
			logger.error("Error parsing risk assessment for auditDataID {} with questionID {} and answer {}\n{}",
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
                if (assessment == null) {
                    logger.info("Error parsing the risk assessment for auditDataID {} with questionID {} and answer {}\n{}",
                            new Object[] { auditData.getId(), auditData.getQuestion().getId(), auditData.getAnswer()});
                continue;
                }
				RiskCategory category = getCategory(assessment);
				LowMedHigh currentRisk = highestRisks.get(category);

				if (calculatedRiskIsHigher(calculatedRisk, currentRisk)) {
					highestRisks.put(category, calculatedRisk);
				}
			} catch (Exception e) {
				logger.info("Unable to parse risk assessment for auditDataID {} with questionID {} and answer {}\n{}",
						new Object[] { auditData.getId(), auditData.getQuestion().getId(), auditData.getAnswer(), e });
			}
		}

		return highestRisks;
	}

	private RiskAssessment getAssessment(int questionID) {
		for (SafetyAssessment safetyAssessment : SafetyAssessment.values()) {
			if (questionID == safetyAssessment.getQuestionID()) {
				return safetyAssessment;
			}
		}

		for (ProductAssessment productAssessment : ProductAssessment.values()) {
			if (questionID == productAssessment.getQuestionID()) {
				return productAssessment;
			}
		}

		for (TransportationAssessment transportationAssessment : TransportationAssessment.values()) {
			if (questionID == transportationAssessment.getQuestionID()) {
				return transportationAssessment;
			}
		}

		return null;
	}

	private void initializeRiskCategoriesToNone(Map<RiskCategory, LowMedHigh> highestRisks) {
		for (RiskCategory riskCategory : RiskCategory.values()) {
			highestRisks.put(riskCategory, LowMedHigh.None);
		}
	}

	private RiskCategory getCategory(RiskAssessment assessment) throws Exception {
		if (assessment != null) {
			if (assessment.isSelfEvaluation()) {
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