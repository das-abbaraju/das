package com.picsauditing.flagcalculator.etl;

import com.picsauditing.flagcalculator.dao.FlagEtlDAO;
import com.picsauditing.flagcalculator.entities.*;
import com.picsauditing.flagcalculator.service.AuditService;
import com.picsauditing.flagcalculator.service.FlagService;
import com.picsauditing.flagcalculator.util.FlagAnswerParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContractorFlagETL {
	private FlagEtlDAO flagEtlDAO;

    private static final Logger logger = LoggerFactory.getLogger(ContractorFlagETL.class);

    public ContractorFlagETL(FlagEtlDAO flagEtlDAO) {
        this.flagEtlDAO = flagEtlDAO;
    }

	public void calculate(ContractorAccount contractor) {
		// get the information necessary to perform the flagging calculations
		Set<FlagCriteria> distinctFlagCriteria = flagEtlDAO.getDistinctOperatorFlagCriteria();
		Set<Integer> criteriaQuestionSet = getFlaggableAuditQuestionIds(distinctFlagCriteria);
		Map<Integer, AuditData> answerMap = flagEtlDAO.findAnswersByContractor(contractor.getId(),
				criteriaQuestionSet);

		Set<FlagCriteriaContractor> changes = new HashSet<>();
		for (FlagCriteria flagCriteria : distinctFlagCriteria) {
			logger.info("Starting to calculate = []", flagCriteria);
			changes.addAll(executeFlagCriteriaCalculation(flagCriteria, contractor, answerMap));
		}

		saveFlagCriteriaContractorChanges(contractor, changes);
	}

	private Set<FlagCriteriaContractor> executeFlagCriteriaCalculation(FlagCriteria flagCriteria,
			ContractorAccount contractor, Map<Integer, AuditData> answerMap) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		if (flagCriteria.getAuditType() != null) {
			changes.add(new FlagCriteriaContractor(contractor, flagCriteria, "true"));
		}

		if (flagCriteria.getQuestion() != null) {
			Set<AuditCategory> applicableCategories = getApplicableCategories(flagCriteria.getQuestion(), contractor);
			if (applicableCategories.contains(flagCriteria.getQuestion().getCategory())) {
				if (AuditQuestion.EMR == flagCriteria.getQuestion().getId()) {
					changes.addAll(calculateFlagCriteriaForEMR(flagCriteria, contractor));
				} else if (flagCriteria.getQuestion().getId() == AuditQuestion.CITATIONS) {
					FlagCriteriaContractor flagCriteriaContractor = generateFlaggableData(flagCriteria, contractor);
					if (flagCriteriaContractor != null) {
						changes.add(flagCriteriaContractor);
					}
				} else if (runAnnualUpdateFlaggingForCategoryOnMultiYearScope(flagCriteria)) {
					FlagCriteriaContractor flagCriteriaContractor = generateFlaggableData(flagCriteria, contractor);
					if (flagCriteriaContractor != null) {
						changes.add(flagCriteriaContractor);
					}
				} else {
					changes.addAll(performFlaggingForNonEMR(contractor, answerMap, flagCriteria));
				}
			}
		}

		if (flagCriteria.getOshaType() != null) {
			performOshaFlagCalculations(contractor, changes, flagCriteria);
		}

		return changes;
	}

	private Set<AuditCategory> getApplicableCategories(AuditQuestion question, ContractorAccount contractor) {
		if (question == null || contractor == null || AuditService.getAuditType(question) == null) {
			return Collections.emptySet();
		}

		Set<AuditCategory> applicableCategories = new TreeSet<>();
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == AuditService.getAuditType(question).getId()) {
				applicableCategories.addAll(AuditService.getVisibleCategories(audit));
			}
		}

		return applicableCategories;
	}

	private boolean runAnnualUpdateFlaggingForCategoryOnMultiYearScope(FlagCriteria flagCriteria) {
		return (flagCriteria.getQuestion().getCategory() != null && AuditService.isAnnualAddendum(AuditService.getAuditType(flagCriteria.getQuestion()).getId()));
	}

	private FlagCriteriaContractor generateFlaggableData(FlagCriteria flagCriteria, ContractorAccount contractor) {
		FlagCriteriaContractor flagCriteriaContractor = null;
		ContractorAudit annualUpdate = AuditService.getCompleteAnnualUpdates(contractor).get(flagCriteria.getMultiYearScope());

		if (annualUpdate != null) {
			if (checkForApplicableCategory(flagCriteria, annualUpdate, true)) {
				// TODO: should the third argument be true all the time?
				for (AuditData data : annualUpdate.getData()) {
					if (data.getQuestion().getId() == flagCriteria.getQuestion().getId()) {
						flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria, "");
						flagCriteriaContractor.setAnswer(FlagAnswerParser.parseAnswer(flagCriteria, data));
						flagCriteriaContractor.setAnswer2("for Year: " + annualUpdate.getAuditFor());
						return flagCriteriaContractor;
					}
				}
			}
		}

		return flagCriteriaContractor;
	}

	private boolean checkForApplicableCategory(FlagCriteria flagCriteria, ContractorAudit annualUpdate,
			boolean applyCategory) {
		return (applyCategory && flagCriteria.getQuestion().getCategory() != null && AuditService.isCategoryApplicable(annualUpdate, flagCriteria.getQuestion().getCategory().getId()));
	}

    /*
	 * Get AuditQuestion IDs that are used
	 *
	 * @param distinctFlagCriteria
	 * @return
	 */
	private Set<Integer> getFlaggableAuditQuestionIds(Set<FlagCriteria> distinctFlagCriteria) {
		Set<Integer> criteriaQuestionSet = new HashSet<Integer>();
		for (FlagCriteria fc : distinctFlagCriteria) {
			AuditQuestion question = fc.getQuestion();
			if (question != null) {
				AuditType type = AuditService.getAuditType(question);
				if (type != null && !AuditService.isAnnualAddendum(type.getId())) {
					logger.info("Found question for evaluation: {}", question);
					criteriaQuestionSet.add(question.getId());
                    if (question.getVisibleQuestion() != null) {
                        criteriaQuestionSet.add(question.getVisibleQuestion().getId());
                    }

					if (FlagService.includeExcess(fc) != null) {
						criteriaQuestionSet.add(FlagService.includeExcess(fc));
					}
				}
			}
		}

		return criteriaQuestionSet;
	}

    /*
	 * Non-EMR questions find answer in answerMap if it exists to related
	 * question
	 *
	 * @param contractor
	 * @param answerMap
	 * @param flagCriteria
     */
	private Set<FlagCriteriaContractor> performFlaggingForNonEMR(ContractorAccount contractor,
			Map<Integer, AuditData> answerMap, FlagCriteria flagCriteria) {
		Set<FlagCriteriaContractor> changes = new HashSet<>();

		// can be null
		final AuditData auditData = answerMap.get(flagCriteria.getQuestion().getId());
		if (isAnswerApplicable(auditData, answerMap)) {
			FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteria, "");
			if (flagCriteria.getQuestion().getQuestionType().equals("AMBest")) {
				performFlaggingForAMBest(flagCriteria, auditData, fcc);
			} else {
				fcc.setAnswer(FlagAnswerParser.parseAnswer(flagCriteria, auditData));
				fcc.setVerified(AuditService.isVerified(auditData));

				if (FlagService.includeExcess(flagCriteria) != null) {
					final AuditData excess = answerMap.get(FlagService.includeExcess(flagCriteria));
					try {
						Float baseLimit = Float.parseFloat(fcc.getAnswer());
						Float excessLimit = Float.parseFloat(excess.getAnswer().replace(",", ""));
						baseLimit += excessLimit;
						fcc.setAnswer2("Includes " + excessLimit.intValue() + " from Excess");
						fcc.setAnswer("" + baseLimit.intValue());
					} catch (Exception doNothingRightHere) {
					}
				}
			}

			changes.add(fcc);
		} else { // the User did not provide an answer
			FlagCriteriaContractor flagCriteriaContractor = checkForMissingAnswer(flagCriteria, contractor);
			if (flagCriteriaContractor != null) {
				changes.add(flagCriteriaContractor);
			}
		}

		return changes;
	}

    private boolean isAnswerApplicable(AuditData auditData, Map<Integer, AuditData> answerMap) {
        if (auditData == null)
            return false;
        if (StringUtils.isEmpty(auditData.getAnswer()))
            return false;

        if (auditData.getQuestion().getVisibleQuestion() != null) {
            AuditData visibleAnswer = answerMap.get(auditData.getQuestion().getVisibleQuestion().getId());
            if (visibleAnswer == null)
                return false;
            if (StringUtils.isEmpty(visibleAnswer.getAnswer()))
                return false;
            if (!visibleAnswer.getAnswer().equals(auditData.getQuestion().getVisibleAnswer()))
                return false;
        }
        return true;
    }

    private void performFlaggingForAMBest(FlagCriteria flagCriteria, final AuditData auditData,
			FlagCriteriaContractor fcc) {
		AmBest amBest = flagEtlDAO.findByNaic(auditData.getComment());

		if (amBest != null) {
			if (flagCriteria.getCategory() == FlagCriteriaCategory.InsuranceAMBRating) {
				fcc.setAnswer(Integer.toString(amBest.getRatingCode()));
			}

			if (flagCriteria.getCategory() == FlagCriteriaCategory.InsuranceAMBClass) {
				fcc.setAnswer(Integer.toString(amBest.getFinancialCode()));
			}
		}
	}

	private void saveFlagCriteriaContractorChanges(ContractorAccount contractor, Set<FlagCriteriaContractor> changes) {
		Iterator<FlagCriteriaContractor> flagCriteriaList = flagEtlDAO.insertUpdateDeleteManaged(
                contractor.getFlagCriteria(), changes).iterator();
		while (flagCriteriaList.hasNext()) {
			FlagCriteriaContractor criteriaData = flagCriteriaList.next();
			contractor.getFlagCriteria().remove(criteriaData);
			flagEtlDAO.remove(criteriaData);
		}
	}

	private Set<FlagCriteriaContractor> calculateFlagCriteriaForEMR(FlagCriteria flagCriteria,
			ContractorAccount contractor) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		OshaOrganizer osha = FlagService.getOshaOrganizer(contractor);
		Double answer = osha.getRate(OshaType.EMR, flagCriteria.getMultiYearScope(),
				OshaRateType.EMR);

		if (answer != null && !answer.equals(Double.valueOf(-1))) {
			FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria,
					Double.toString(answer));

			String answer2 = osha.getAnswer2(OshaType.EMR, flagCriteria.getMultiYearScope());
			flagCriteriaContractor.setAnswer2(answer2);

			boolean verified = osha.isVerified(OshaType.EMR, flagCriteria.getMultiYearScope());
			flagCriteriaContractor.setVerified(verified);

			changes.add(flagCriteriaContractor);
		} else { // The user did not provide an answer
			FlagCriteriaContractor flagCriteriaContractor = checkForMissingAnswer(flagCriteria, contractor);
			if (flagCriteriaContractor != null) {
				changes.add(flagCriteriaContractor);
			}
		}

		return changes;
	}

	private void performOshaFlagCalculations(ContractorAccount contractor, Set<FlagCriteriaContractor> changes,
			FlagCriteria flagCriteria) {
		OshaOrganizer osha = FlagService.getOshaOrganizer(contractor);
		Double answer = osha.getRate(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope(),
				flagCriteria.getOshaRateType());
		logger.info("Answer = {}", answer);

		if (answer != null && !answer.equals(Double.valueOf(-1))) {
			FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria,
					Double.toString(answer));

			String answer2 = osha.getAnswer2(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope());
			flagCriteriaContractor.setAnswer2(answer2);

			boolean verified = osha.isVerified(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope());
			flagCriteriaContractor.setVerified(verified);

			changes.add(flagCriteriaContractor);
		} else { // the User did not provide an answer
			FlagCriteriaContractor flagCriteriaContractor = checkForMissingAnswer(flagCriteria, contractor);
			if (flagCriteriaContractor != null) {
				changes.add(flagCriteriaContractor);
			}
		}
	}

	private FlagCriteriaContractor checkForMissingAnswer(FlagCriteria flagCriteria, ContractorAccount contractor) {
		if (flagCriteria.isFlaggableWhenMissing()) {
			FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria, null);
			flagCriteriaContractor.setAnswer2(null);
			return flagCriteriaContractor;
		}

		return null;
	}
}
