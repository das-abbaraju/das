package com.picsauditing.PICS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.flags.FlagAnswerParser;
import com.picsauditing.PICS.flags.MultiYearValueCalculator;
import com.picsauditing.PICS.flags.OshaResult;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class ContractorFlagETL {
	
	@Autowired
	private FlagCriteriaDAO flagCriteriaDao;
	@Autowired
	private AuditDataDAO auditDataDao;
	@Autowired
	private FlagCriteriaContractorDAO flagCriteriaContractorDao;
	
	private static final List<Integer> QUESTION_IDS_FOR_MULTI_YEAR = Arrays.asList(11046, 3547);

	public void calculate(ContractorAccount contractor) {
		// get the information necessary to perform the flagging calculations
		Set<FlagCriteria> distinctFlagCriteria = flagCriteriaDao.getDistinctOperatorFlagCriteria();
		Set<Integer> criteriaQuestionSet = getFlaggableAuditQuestionIds(distinctFlagCriteria);
		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(contractor.getId(), criteriaQuestionSet);

		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();
		for (FlagCriteria flagCriteria : distinctFlagCriteria) {
			PicsLogger.log("Starting to calculate = " + flagCriteria);
			changes.addAll(executeFlagCriteriaCalculation(flagCriteria, contractor, answerMap));
		}

		persistFlagCriteriaContractorChanges(contractor, changes);
	}
	
	/**
	 * Performs the calculation on an individual FlagCriteria
	 * 
	 * @param flagCriteria
	 * @param contractor
	 * @param answerMap
	 * @return 
	 */
	private Set<FlagCriteriaContractor> executeFlagCriteriaCalculation(FlagCriteria flagCriteria, ContractorAccount contractor, 
			Map<Integer, AuditData> answerMap) {

		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		if (flagCriteria.getAuditType() != null) {
			changes.add(new FlagCriteriaContractor(contractor, flagCriteria, "true"));
		}

		if (flagCriteria.getQuestion() != null) {
			if (AuditQuestion.EMR == flagCriteria.getQuestion().getId()) {
				changes.addAll(calculateFlagCriteriaForEMR(flagCriteria, contractor));
			} else if (flagCriteria.getQuestion().getId() == AuditQuestion.CITATIONS) {
				FlagCriteriaContractor flagCriteriaContractor = generateFlaggableData(flagCriteria, contractor, false);
				if (flagCriteriaContractor != null) {
					changes.add(flagCriteriaContractor);
				}
			} else if (flagCriteria.getMultiYearScope() != null && QUESTION_IDS_FOR_MULTI_YEAR.contains(flagCriteria.getQuestion().getId())) {
				FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor, flagCriteria, "");
				flagCriteriaContractor.setAnswer(MultiYearValueCalculator.calculateValueForMultiYear(contractor, flagCriteria));
				changes.add(flagCriteriaContractor);
			} else if (runAnnualUpdateFlaggingForCategoryOnMultiYearScope(flagCriteria)) {
				FlagCriteriaContractor flagCriteriaContractor = generateFlaggableData(flagCriteria, contractor, true);
				if (flagCriteriaContractor != null) {
					changes.add(flagCriteriaContractor);
				}
			} else {
				changes.addAll(performFlaggingForNonEMR(contractor, answerMap, flagCriteria));
			}
		}

		if (flagCriteria.getOshaType() != null) {
			performOshaFlagCalculations(contractor, changes, flagCriteria);
		}
		
		return changes;
	}

	private boolean runAnnualUpdateFlaggingForCategoryOnMultiYearScope(FlagCriteria flagCriteria) {
		return (flagCriteria.getQuestion().getCategory() != null
				&& flagCriteria.getQuestion().getAuditType().isAnnualAddendum());
	}
	
	private FlagCriteriaContractor generateFlaggableData(FlagCriteria flagCriteria, ContractorAccount contractor, boolean applyCategory) {
		FlagCriteriaContractor flagCriteriaContractor = null;
		ContractorAudit annualUpdate = contractor.getCompleteAnnualUpdates().get(flagCriteria.getMultiYearScope());
		
		if (annualUpdate != null) {
			if (checkForApplicableCategory(flagCriteria, annualUpdate, true)) {
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

	private boolean checkForApplicableCategory(FlagCriteria flagCriteria, ContractorAudit annualUpdate, boolean applyCategory) {
		return (applyCategory 
				&& flagCriteria.getQuestion().getCategory() != null 
				&& annualUpdate.isCategoryApplicable(flagCriteria.getQuestion().getCategory().getId()));
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
			if (fc.getQuestion() != null) {
				AuditType type = fc.getQuestion().getAuditType();
				if (type != null && !type.isAnnualAddendum()) {
					PicsLogger.log("Found question for evaluation: " + fc.getQuestion());
					criteriaQuestionSet.add(fc.getQuestion().getId());
					if (fc.includeExcess() != null) {
						criteriaQuestionSet.add(fc.includeExcess());
					}
				}
			}
		}
		
		return criteriaQuestionSet;
	}

	/**
	 * 	Non-EMR questions
	 *	find answer in answerMap if it exists to related question
	 * 
	 * @param contractor
	 * @param changes
	 * @param answerMap
	 * @param flagCriteria
	 */
	private Set<FlagCriteriaContractor> performFlaggingForNonEMR(ContractorAccount contractor, Map<Integer, AuditData> answerMap, FlagCriteria flagCriteria) {
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

	private void performFlaggingForAMBest(FlagCriteria flagCriteria, final AuditData auditData, FlagCriteriaContractor fcc) {
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

	private void persistFlagCriteriaContractorChanges(ContractorAccount contractor, Set<FlagCriteriaContractor> changes) {
		Iterator<FlagCriteriaContractor> flagCriteriaList = BaseTable.insertUpdateDeleteManaged(contractor.getFlagCriteria(), changes).iterator();
		while (flagCriteriaList.hasNext()) {
			FlagCriteriaContractor criteriaData = flagCriteriaList.next();
			contractor.getFlagCriteria().remove(criteriaData);
			flagCriteriaContractorDao.remove(criteriaData);
		}
	}
	
	private Set<FlagCriteriaContractor> calculateFlagCriteriaForEMR(FlagCriteria flagCriteria, ContractorAccount contractor) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		if (flagCriteria.getQuestion().getId() == AuditQuestion.EMR) {
			List<OshaResult> oshaResults = MultiYearValueCalculator.getOshaResults(contractor.getSortedAnnualUpdates());

			if (CollectionUtils.isNotEmpty(oshaResults)) {
				Float answer = null;
				String answer2 = "";
				boolean verified = true; // Has the data been verified?

				try {
					switch (flagCriteria.getMultiYearScope()) {
					case ThreeYearAverage:
						OshaResult oshaResult = MultiYearValueCalculator.calculateAverageEMR(oshaResults); 
						answer = (oshaResult.getAnswer() != null) ? Float.valueOf(Strings.formatNumber(oshaResult.getAnswer())) : null;
						verified = oshaResult.isVerified();
						answer2 = "Years: " + oshaResult.getYear();						
						break;
					case ThreeYearsAgo:
						if (oshaResults.size() >= 3) {
							OshaResult result = oshaResults.get(oshaResults.size() - 3);
							if (result != null) {
								answer = Float.valueOf(Strings.formatNumber(result.getAnswer()));
								verified = result.isVerified();
								answer2 = "Year: " + result.getYear();
							}
						}
						break;
					case TwoYearsAgo:
						if (oshaResults.size() >= 2) {
							OshaResult result = oshaResults.get(oshaResults.size() - 2);
							if (result != null) {
								answer = Float.valueOf(Strings.formatNumber(result.getAnswer()));
								verified = result.isVerified();
								answer2 = "Year: " + result.getYear();
							}
						}
						break;
					case LastYearOnly:
						if (oshaResults.size() >= 1) {
							OshaResult result = oshaResults.get(oshaResults.size() - 1);
							if (result != null && isLast2Years(result.getYear())) {
								answer = Float.valueOf(Strings.formatNumber(result.getAnswer()));
								verified = result.isVerified();
								answer2 = "Year: " + result.getYear();
							}
						}
						break;
					default:
						throw new RuntimeException("Invalid MultiYear scope of "
										+ flagCriteria.getMultiYearScope().toString()
										+ " specified for flag criteria id "
										+ flagCriteria.getId()
										+ ", contractor id "
										+ contractor.getId());
					}
				} catch (Throwable t) {
					PicsLogger.log("Could not cast contractor: "
							+ contractor.getId() + " and answer: "
							+ ((answer != null) ? answer : "null")
							+ " to a value for criteria: "
							+ flagCriteria.getId());

					answer = null; // contractor errors out somewhere
					// during the process of creating
					// their data
					// do not want to enter partially corrupt data
				}

				if (answer != null) {
					final FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteria, answer.toString());
					fcc.setVerified(verified);

					// conditionally add verified tag
					if (verified) {
						answer2 += "<br/><span class=\"verified\">Verified</span>";
					}
					fcc.setAnswer2(answer2);

					changes.add(fcc);
				} else { // The user did not provide an answer
					FlagCriteriaContractor flagCriteriaContractor = checkForMissingAnswer(flagCriteria, contractor);
					if (flagCriteriaContractor != null) {
						changes.add(flagCriteriaContractor);
					}
				}
			}
		}
		
		return changes;
	}

	private boolean isLast2Years(String auditFor) {
		int lastYear = DateBean.getCurrentYear() - 1;
		if (Integer.toString(lastYear).equals(auditFor) || Integer.toString(lastYear - 1).equals(auditFor))
			return true;
	
		return false;
	}
		
	// TODO: find out why it throws an exception with Ancon Marine
	private void performOshaFlagCalculations(ContractorAccount contractor, Set<FlagCriteriaContractor> changes, FlagCriteria flagCriteria) {
//		OshaOrganizer osha = contractor.getOshaOrganizer();
//		Double answer = osha.getRate(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope(), flagCriteria.getOshaRateType());
//		PicsLogger.log("Answer = " + answer);
//
//		if (answer != null && !answer.equals(Double.valueOf(-1))) {
//			FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
//					flagCriteria, Double.toString(answer));
//
//			String answer2 = osha.getAnswer2(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope(),
//					flagCriteria.getOshaRateType());
//			flagCriteriaContractor.setAnswer2(answer2);
//
//			boolean verified = osha.isVerified(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope());
//			flagCriteriaContractor.setVerified(verified);
//
//			changes.add(flagCriteriaContractor);
//		} else { // the User did not provide an answer
//			FlagCriteriaContractor flagCriteriaContractor = checkForMissingAnswer(flagCriteria, contractor);			
//			if (flagCriteriaContractor != null) {
//				changes.add(flagCriteriaContractor);
//			}
//		}
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
