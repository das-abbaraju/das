package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Transient;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.util.Strings;

public class ContractorFlagETL {
	private FlagCriteriaContractorDAO flagCriteriaContractorDao;
	private AuditDataDAO auditDataDao;

	private List<FlagCriteria> flagCriteriaList = null;
	private HashSet<Integer> criteriaQuestionSet = new HashSet<Integer>();
	protected boolean hasOqEmployees = false;
	protected boolean hasCOR = false;

	public ContractorFlagETL(FlagCriteriaDAO flagCriteriaDao, AuditDataDAO auditDataDao,
			FlagCriteriaContractorDAO flagCriteriaContractorDao) {
		this.flagCriteriaContractorDao = flagCriteriaContractorDao;
		this.auditDataDao = auditDataDao;

		flagCriteriaList = flagCriteriaDao.getDistinctOperatorFlagCriteria();
		// Get AuditQuestion ids that are used
		for (FlagCriteria fc : flagCriteriaList) {
			if (fc.getQuestion() != null && fc.getAuditType() != null && !fc.getAuditType().isAnnualAddendum())
				criteriaQuestionSet.add(fc.getQuestion().getId());
		}
	}

	public void calculate(ContractorAccount contractor) {
		flagCriteriaContractorDao.deleteEntriesForContractor(contractor.getId());

		List<FlagCriteriaContractor> changes = contractor.getFlagCriteria();

		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(contractor.getId(),
				criteriaQuestionSet);

		for (FlagCriteria flagCriteria : flagCriteriaList) {
			// Processing Audits
			if (flagCriteria.getAuditType() != null) {
				// Checking Audit Type
				if (flagCriteria.getAuditType().getClassType().isPolicy()) { // Insurance
					// Audit
					// Contractors are evaluated by their CAO,
					// so it's operator specific and we can't calculate exact
					// data here
					// Just put in a place holder row
					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, "true"));
				} else if (flagCriteria.getAuditType().isAnnualAddendum()) { // Annual
					// Update
					// Audit
					int count = 0;

					// Checking for at least 3 active annual updates
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().equals(flagCriteria.getAuditType())) {
							if (ca.getAuditStatus().isActiveResubmittedExempt())
								count++;
						}
					}

					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, (count >= 3 ? "true" : "false")));
				} else { // Any other audit, PQF/IM/Desktop/D&A/COR
					Boolean hasProperStatus = false;
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().equals(flagCriteria.getAuditType())) {
							if (flagCriteria.getAuditType().getId() == AuditType.DA
									&& !isHasOqEmployees(contractor.getId()))
								hasProperStatus = true;
							else if (flagCriteria.getAuditType().getId() == AuditType.COR
									&& !isHasCOR(contractor.getId()))
								hasProperStatus = true;
							else if (ca.getAuditStatus().isActiveResubmittedExempt())
								hasProperStatus = true;
							else if (!flagCriteria.isValidationRequired() && ca.getAuditStatus().isSubmitted())
								hasProperStatus = true;
						}
					}
					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, hasProperStatus.toString()));
				}
			}

			// Non-EMR questions (emrs handled seperately)
			if (flagCriteria.getQuestion() != null && flagCriteria.getQuestion().getId() != AuditQuestion.EMR) {
				// find answer in answermap if exists to related question
				// can be null
				final AuditData auditData = answerMap.get(flagCriteria.getQuestion().getId());
				if (auditData != null) {
					final FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
							flagCriteria, "");
					if (flagCriteria.getDataType().equals("boolean")) {
						// parse to boolean
						Boolean b = Boolean.parseBoolean(auditData.getAnswer());
						flagCriteriaContractor.setAnswer(b.toString());
					} else if (flagCriteria.getDataType().equals("number")) {
						// parse to number
						Float f = Float.parseFloat(auditData.getAnswer());
						flagCriteriaContractor.setAnswer(f.toString());
					} else if (flagCriteria.getDataType().equals("date")) {
						// parse to date
						Date d = DateBean.parseDate(auditData.getAnswer());
						flagCriteriaContractor.setAnswer(d.toString());
					} else if (flagCriteria.getDataType().equals("string")) {
						flagCriteriaContractor.setAnswer(auditData.getAnswer());
					}
					changes.add(flagCriteriaContractor);
				}
			}

			// Checking OSHA
			if (flagCriteria.getOshaType() != null) {
				// expecting to contain 4 or less of the most current audits
				final Map<String, OshaAudit> auditsOfThisSHAType = contractor.getOshas()
						.get(flagCriteria.getOshaType());
				// TODO: VERIFY ORDER IS PRESERVED
				// <-----------------------*******
				List<OshaAudit> auditYears = new ArrayList<OshaAudit>(auditsOfThisSHAType.values());
				if (auditYears.size() > 0) {
					if (auditYears.size() > 3) {
						// Removing year which is not needed for transition
						// years
						if (!auditYears.get(0).isVerified())
							auditYears.remove(0);
						else
							auditYears.remove(3);
					}

					Float answer = null;
					boolean verified = true; // Has the data been verified?

					switch (flagCriteria.getMultiYearScope()) {
					case ThreeYearAverage:
						answer = 0.0f;
						for (OshaAudit year : auditYears) {
							answer += year.getRate(flagCriteria.getOshaRateType());
							if (!year.isVerified())
								verified = false;
						}
						answer /= (float) auditYears.size();
						break;
					case ThreeYearsAgo:
						if (auditYears.size() >= 3) {
							answer = auditYears.get(2).getRate(flagCriteria.getOshaRateType());
							verified = auditYears.get(2).isVerified();
						}
						break;
					case TwoYearsAgo:
						if (auditYears.size() >= 2) {
							answer = auditYears.get(1).getRate(flagCriteria.getOshaRateType());
							verified = auditYears.get(1).isVerified();
						}
						break;
					case LastYearOnly:
						answer = auditYears.get(0).getRate(flagCriteria.getOshaRateType());
						verified = auditYears.get(0).isVerified();
						break;
					default:
						throw new RuntimeException("Invalid MultiYear scope of "
								+ flagCriteria.getMultiYearScope().toString() + " specified for flag criteria id "
								+ flagCriteria.getId() + ", contractor id " + contractor.getId());
					}

					if (answer != null) {
						final FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
								flagCriteria, answer.toString());
						flagCriteriaContractor.setVerified(verified);
						changes.add(flagCriteriaContractor);
					}
				}
			}

			// EMR Questions
			if (flagCriteria.getQuestion() != null && flagCriteria.getQuestion().getId() == AuditQuestion.EMR) {
				Map<String, AuditData> auditsOfThisEMRType = new TreeMap<String, AuditData>(contractor.getEmrs());
				List<AuditData> years = new ArrayList<AuditData>(auditsOfThisEMRType.values());

				if (years != null && years.size() > 0) {
					if (years.size() > 3) {
						// Removing year which is not needed for transition
						// years
						if (!years.get(0).isVerified())
							years.remove(0);
						else
							years.remove(3);
					}

					Float answer = null;
					boolean verified = true; // Has the data been verified?

					try {
						switch (flagCriteria.getMultiYearScope()) {
						case ThreeYearAverage:
							answer = 0.0f;
							for (AuditData year : years) {
								answer += Float.valueOf(year.getAnswer());
								if (!year.isVerified())
									verified = false;
							}
							answer /= (float) years.size();

							break;
						case ThreeYearsAgo:
							if (years.size() >= 3) {
								answer = Float.valueOf(years.get(2).getAnswer());
								verified = years.get(2).isVerified();
							}
							break;
						case TwoYearsAgo:
							if (years.size() >= 2) {
								answer = Float.valueOf(years.get(1).getAnswer());
								System.out.println(years.get(1).getId());
								verified = years.get(1).isVerified();
							}
							break;
						case LastYearOnly:
							answer = Float.valueOf(years.get(0).getAnswer());
							verified = years.get(0).isVerified();
							break;
						default:
							throw new RuntimeException("Invalid MultiYear scope of "
									+ flagCriteria.getMultiYearScope().toString() + " specified for flag criteria id "
									+ flagCriteria.getId() + ", contractor id " + contractor.getId());
						}

						/*
						 * Legacy code From FlagCalculator2 OshaAudit oshaAvg =
						 * contractor
						 * .getOshas().get(OshaType.OSHA).get(OshaAudit.AVG);
						 * AuditData emrAvg =
						 * contractor.getEmrs().get(OshaAudit.AVG); if(emrAvg !=
						 * null && !Strings.isEmpty(emrAvg.getAnswer()))
						 * contractor.setEmrAverage
						 * (Float.valueOf(emrAvg.getAnswer()).floatValue()); if
						 * (oshaAvg != null) {
						 * contractor.setTrirAverage(oshaAvg.
						 * getRecordableTotalRate ());
						 * contractor.setLwcrAverage(
						 * oshaAvg.getLostWorkCasesRate ()); }
						 */

					} catch (NumberFormatException e) {
						answer = 0.0f;
					}

					if (answer != null) {
						final FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
								flagCriteria, answer.toString());
						flagCriteriaContractor.setVerified(verified);
						changes.add(flagCriteriaContractor);
					}
				}
			}
		}

		// TODO: MAKE SURE BATCH INSERT IS BEING PERFORMED PROPERLY
		for (FlagCriteriaContractor change : changes) {
			// flagCriteriaContractorDao.save(change);
		}
	}

	private boolean isHasOqEmployees(int conID) {
		List<Integer> questions = new ArrayList<Integer>();
		questions.add(AuditQuestion.OQ_EMPLOYEES);
		List<AuditData> auditDataList = auditDataDao.findAnswerByConQuestions(conID, questions);
		if (auditDataList != null && auditDataList.size() > 0) {
			AuditData auditData = auditDataList.get(0);
			if (auditData != null && "Yes".equals(auditData.getAnswer()))
				return true;
		}
		return false;
	}

	private boolean isHasCOR(int conID) {
		List<Integer> questions = new ArrayList<Integer>();
		questions.add(2954);
		List<AuditData> auditDataList = auditDataDao.findAnswerByConQuestions(conID, questions);
		if (auditDataList != null && auditDataList.size() > 0) {
			AuditData auditData = auditDataList.get(0);
			if (auditData != null && "Yes".equals(auditData.getAnswer()))
				return true;
		}
		return false;
	}
}
