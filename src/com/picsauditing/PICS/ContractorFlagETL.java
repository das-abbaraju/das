package com.picsauditing.PICS;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.flags.FlagAnswerParser;
import com.picsauditing.PICS.flags.MultiYearValueCalculator;
import com.picsauditing.PICS.flags.OshaResult;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class ContractorFlagETL {
	@Autowired
	private FlagCriteriaDAO flagCriteriaDao;
	@Autowired
	private AuditDataDAO auditDataDao;
	@Autowired
	private FlagCriteriaContractorDAO flagCriteriaContractorDao;

	private static final Logger logger = LoggerFactory.getLogger(ContractorFlagETL.class);

	public void calculate(ContractorAccount contractor) {
		// get the information necessary to perform the flagging calculations
		Set<FlagCriteria> distinctFlagCriteria = flagCriteriaDao.getDistinctOperatorFlagCriteria();
		Set<Integer> criteriaQuestionSet = getFlaggableAuditQuestionIds(distinctFlagCriteria);
		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(contractor.getId(),
				criteriaQuestionSet);

		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();
		for (FlagCriteria flagCriteria : distinctFlagCriteria) {
			logger.info("Starting to calculate = []", flagCriteria);
			changes.addAll(executeFlagCriteriaCalculation(flagCriteria, contractor, answerMap));
		}

		saveFlagCriteriaContractorChanges(contractor, changes);
	}

	/**
	 * Performs the calculation on an individual FlagCriteria
	 * 
	 * @param flagCriteria
	 * @param contractor
	 * @param answerMap
	 * @return
	 */
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
					FlagCriteriaContractor flagCriteriaContractor = generateFlaggableData(flagCriteria, contractor,
							false);
					if (flagCriteriaContractor != null) {
						changes.add(flagCriteriaContractor);
					}
				} else if (runAnnualUpdateFlaggingForCategoryOnMultiYearScope(flagCriteria)) {
					FlagCriteriaContractor flagCriteriaContractor = generateFlaggableData(flagCriteria, contractor,
							true);
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
		if (question == null || contractor == null || question.getAuditType() == null) {
			return Collections.emptySet();
		}

		Set<AuditCategory> applicableCategories = new TreeSet<AuditCategory>();
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == question.getAuditType().getId()) {
				applicableCategories.addAll(audit.getVisibleCategories());
			}
		}

		return applicableCategories;
	}

	private boolean runAnnualUpdateFlaggingForCategoryOnMultiYearScope(FlagCriteria flagCriteria) {
		return (flagCriteria.getQuestion().getCategory() != null && flagCriteria.getQuestion().getAuditType()
				.isAnnualAddendum());
	}

	private FlagCriteriaContractor generateFlaggableData(FlagCriteria flagCriteria, ContractorAccount contractor,
			boolean applyCategory) {
		FlagCriteriaContractor flagCriteriaContractor = null;
		ContractorAudit annualUpdate = contractor.getCompleteAnnualUpdates().get(flagCriteria.getMultiYearScope());

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
		return (applyCategory && flagCriteria.getQuestion().getCategory() != null && annualUpdate
				.isCategoryApplicable(flagCriteria.getQuestion().getCategory().getId()));
	}

	/**
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
				AuditType type = question.getAuditType();
				if (type != null && !type.isAnnualAddendum()) {
					logger.info("Found question for evaluation: {}", question);
					criteriaQuestionSet.add(question.getId());

					if (fc.includeExcess() != null) {
						criteriaQuestionSet.add(fc.includeExcess());
					}
				}
			}
		}

		return criteriaQuestionSet;
	}

	/**
	 * Non-EMR questions find answer in answerMap if it exists to related
	 * question
	 * 
	 * @param contractor
	 * @param answerMap
	 * @param flagCriteria
	 */
	private Set<FlagCriteriaContractor> performFlaggingForNonEMR(ContractorAccount contractor,
			Map<Integer, AuditData> answerMap, FlagCriteria flagCriteria) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		// can be null
		final AuditData auditData = answerMap.get(flagCriteria.getQuestion().getId());
		if (auditData != null && !Strings.isEmpty(auditData.getAnswer())) {
			FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteria, "");
			if (flagCriteria.getQuestion().getQuestionType().equals("AMBest")) {
				performFlaggingForAMBest(flagCriteria, auditData, fcc);
			} else {
				fcc.setAnswer(FlagAnswerParser.parseAnswer(flagCriteria, auditData));
				fcc.setVerified(auditData.isVerified());

				if (flagCriteria.includeExcess() != null) {
					final AuditData excess = answerMap.get(flagCriteria.includeExcess());
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

	private void performFlaggingForAMBest(FlagCriteria flagCriteria, final AuditData auditData,
			FlagCriteriaContractor fcc) {
		AmBestDAO amBestDAO = SpringUtils.getBean("AmBestDAO");
		AmBest amBest = amBestDAO.findByNaic(auditData.getComment());

		if (amBest != null) {
			if (flagCriteria.getCategory().equals("Insurance AMB Rating")) {
				fcc.setAnswer(Integer.toString(amBest.getRatingCode()));
			}

			if (flagCriteria.getCategory().equals("Insurance AMB Class")) {
				fcc.setAnswer(Integer.toString(amBest.getFinancialCode()));
			}
		}
	}

	private void saveFlagCriteriaContractorChanges(ContractorAccount contractor, Set<FlagCriteriaContractor> changes) {
		Iterator<FlagCriteriaContractor> flagCriteriaList = BaseTable.insertUpdateDeleteManaged(
				contractor.getFlagCriteria(), changes).iterator();
		while (flagCriteriaList.hasNext()) {
			FlagCriteriaContractor criteriaData = flagCriteriaList.next();
			contractor.getFlagCriteria().remove(criteriaData);
			flagCriteriaContractorDao.remove(criteriaData);
		}
	}

	private Set<FlagCriteriaContractor> calculateFlagCriteriaForEMR(FlagCriteria flagCriteria,
			ContractorAccount contractor) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		OshaOrganizer osha = contractor.getOshaOrganizer();
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
		OshaOrganizer osha = contractor.getOshaOrganizer();
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