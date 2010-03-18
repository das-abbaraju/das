package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private FlagCriteriaContractorDAO flagCriteriaContractorDao;
	private AuditDataDAO auditDataDao;

	private Set<FlagCriteria> distinctFlagCriteria = null;
	private Set<Integer> criteriaQuestionSet = new HashSet<Integer>();
	protected boolean hasOqEmployees = false;
	protected boolean hasCOR = false;

	public ContractorFlagETL(FlagCriteriaDAO flagCriteriaDao, AuditDataDAO auditDataDao,
			FlagCriteriaContractorDAO flagCriteriaContractorDao) {
		this.auditDataDao = auditDataDao;
		this.flagCriteriaContractorDao = flagCriteriaContractorDao;

		distinctFlagCriteria = flagCriteriaDao.getDistinctOperatorFlagCriteria();

		// Get AuditQuestion ids that are used
		for (FlagCriteria fc : distinctFlagCriteria) {
			if (fc.getQuestion() != null) {
				AuditType type = fc.getQuestion().getAuditType();
				if (type != null && !type.isAnnualAddendum()) {
					PicsLogger.log("Found question for evaluation: " + fc.getQuestion());
					criteriaQuestionSet.add(fc.getQuestion().getId());
				}
			}
		}
	}

	public void calculate(ContractorAccount contractor) {
		Set<FlagCriteriaContractor> changes = new HashSet<FlagCriteriaContractor>();

		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(contractor.getId(),
				criteriaQuestionSet);

		for (FlagCriteria flagCriteria : distinctFlagCriteria) {
			PicsLogger.log("Starting to calculate = " + flagCriteria);
			if (flagCriteria.getAuditType() != null) {
				// Checking Audit Type
				if (flagCriteria.getAuditType().getClassType().isPolicy()) {
					// Insurance Audit
					// Contractors are evaluated by their CAO,
					// so it's operator specific and we can't calculate exact
					// data here. Just put in a place holder row
					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, "true"));
				} else if (flagCriteria.getAuditType().isAnnualAddendum()) {
					// Annual Update Audit
					int count = 0;

					// Checking for at least 3 active annual updates
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().equals(flagCriteria.getAuditType())) {
							if (ca.getAuditStatus().isActiveResubmittedExempt())
								count++;
						}
					}

					changes.add(new FlagCriteriaContractor(contractor, flagCriteria, (count >= 3 ? "true" : "false")));
				} else {
					// Any other audit, PQF/IM/Desktop/D&A/COR
					Boolean hasProperStatus = null;
					for (ContractorAudit ca : contractor.getAudits()) {
						if (ca.getAuditType().equals(flagCriteria.getAuditType())) {
							// I have a matching audit
							if (hasProperStatus == null)
								hasProperStatus = false;
							if (ca.getAuditStatus().isActiveResubmittedExempt())
								hasProperStatus = true;
							else if (!flagCriteria.isValidationRequired() && ca.getAuditStatus().isSubmitted())
								hasProperStatus = true;
						}
					}
					// isFlaggableWhenMissing would be really useful for Manual
					// Audits or Implementation Audits
					if (hasProperStatus != null || flagCriteria.isFlaggableWhenMissing())
						changes.add(new FlagCriteriaContractor(contractor, flagCriteria,
								hasProperStatus == null ? "null" : hasProperStatus.toString()));
				}
			}

			if (flagCriteria.getQuestion() != null) {
				if (flagCriteria.getQuestion().getId() == AuditQuestion.EMR) {
					Map<String, AuditData> auditsOfThisEMRType = contractor.getEmrs();

					List<AuditData> years = new ArrayList<AuditData>();
					for (String year : auditsOfThisEMRType.keySet()) {
						if (!year.equals("Average"))
							years.add(auditsOfThisEMRType.get(year));
					}

					if (years != null && years.size() > 0) {
						Float answer = null;
						String answer2 = "";
						boolean verified = true; // Has the data been verified?

						try {
							switch (flagCriteria.getMultiYearScope()) {
							case ThreeYearAverage:
								AuditData average = auditsOfThisEMRType.get("Average");
								answer = (average != null) ? Float.valueOf(average.getAnswer()) : null;
								for (AuditData year : years) {
									answer2 += (answer2.isEmpty()) ? "Years: " + year.getAudit().getAuditFor() : ", "
											+ year.getAudit().getAuditFor();
									if (!average.isVerified())
										verified = false;
								}
								break;
							case ThreeYearsAgo:
								if (years.size() >= 3) {
									answer = Float.valueOf(years.get(years.size() - 3).getAnswer());
									verified = years.get(years.size() - 3).isVerified();
									answer2 = "Year: " + years.get(years.size() - 3).getAudit().getAuditFor();
								}
								break;
							case TwoYearsAgo:
								if (years.size() >= 2) {
									answer = Float.valueOf(years.get(years.size() - 2).getAnswer());
									verified = years.get(years.size() - 2).isVerified();
									answer2 = "Year: " + years.get(years.size() - 2).getAudit().getAuditFor();
								}
								break;
							case LastYearOnly:
								if (years.size() >= 1) {
									answer = Float.valueOf(years.get(years.size() - 1).getAnswer());
									verified = years.get(years.size() - 1).isVerified();
									answer2 = "Year: " + years.get(years.size() - 1).getAudit().getAuditFor();
								}
								break;
							default:
								throw new RuntimeException("Invalid MultiYear scope of "
										+ flagCriteria.getMultiYearScope().toString()
										+ " specified for flag criteria id " + flagCriteria.getId()
										+ ", contractor id " + contractor.getId());
							}
						} catch (Throwable t) {
							PicsLogger.log("Could not cast contractor: " + contractor.getId() + " and answer: "
									+ ((answer != null) ? answer : "null") + " to a value for criteria: "
									+ flagCriteria.getId());

							answer = null; // contractor errored out somewhere
							// during the process of creating
							// their data
							// do not want to enter partially corrupt data
						}

						if (answer != null) {
							final FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteria,
									answer.toString());
							fcc.setVerified(verified);

							// conditionally add verified tag
							if (verified) {
								answer2 += "<br/><span class=\"verified\">Verified</span>";
							}
							fcc.setAnswer2(answer2);

							changes.add(fcc);
						} else {
							if (flagCriteria.isFlaggableWhenMissing()) {
								FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
										flagCriteria, null);
								flagCriteriaContractor.setAnswer2(null);
								changes.add(flagCriteriaContractor);
							}
						}
					}

				} else {
					// Non-EMR questions
					// find answer in answermap if it exists to related question
					// can be null
					final AuditData auditData = answerMap.get(flagCriteria.getQuestion().getId());
					if (auditData != null && !Strings.isEmpty(auditData.getAnswer())) {
						FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteria, "");
						if (flagCriteria.getQuestion().getQuestionType().equals("AMBest")) {
							AmBestDAO amBestDAO = (AmBestDAO) SpringUtils.getBean("AmBestDAO");
							AmBest amBest = amBestDAO.findByNaic(auditData.getComment());
							if (amBest != null) {
								if (flagCriteria.getLabel().contains("Rating")) {
									fcc.setAnswer(Integer.toString(amBest.getRatingCode()));
								}
								if (flagCriteria.getLabel().contains("Class")) {
									fcc.setAnswer(Integer.toString(amBest.getFinancialCode()));
								}
							}
						} else {
							fcc.setAnswer(parseAnswer(flagCriteria, auditData));
							fcc.setVerified(auditData.isVerified());
						}

						changes.add(fcc);
					} else {
						if (flagCriteria.isFlaggableWhenMissing()) {
							FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
									flagCriteria, null);
							flagCriteriaContractor.setAnswer2(null);
							changes.add(flagCriteriaContractor);
						}
					}
				}
			} // end of questions

			// Checking OSHA
			if (flagCriteria.getOshaType() != null) {
				OshaOrganizer osha = contractor.getOshaOrganizer();
				Float answer = osha.getRate(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope(), flagCriteria
						.getOshaRateType());
				PicsLogger.log("Answer = " + answer);

				if (answer != null) {
					FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
							flagCriteria, Float.toString(answer));

					String answer2 = osha.getAnswer2(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope(),
							flagCriteria.getOshaRateType());
					flagCriteriaContractor.setAnswer2(answer2);

					boolean verified = osha.isVerified(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope());
					flagCriteriaContractor.setVerified(verified);

					changes.add(flagCriteriaContractor);
				} else { // check if flaggable when missing
					if (flagCriteria.isFlaggableWhenMissing()) {
						FlagCriteriaContractor flagCriteriaContractor = new FlagCriteriaContractor(contractor,
								flagCriteria, null);
						flagCriteriaContractor.setAnswer2(null);
						changes.add(flagCriteriaContractor);
					}
				}
			}

		}

		BaseTable.insertUpdateDeleteManaged(contractor.getFlagCriteria(), changes);

	}

	public String parseAnswer(FlagCriteria flagCriteria, AuditData auditData) {
		String qType = auditData.getQuestion().getQuestionType();
		String cType = flagCriteria.getDataType();
		String answer = auditData.getAnswer();

		if ("Check Box".equals(qType)) {
			if (!"boolean".equals(cType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
			if ("X".equals(answer))
				return "true";
			else
				return "false";
		}
		if ("Yes/No/NA".equals(qType) || "Yes/No".equals(qType) || "Manual".equals(qType)) {
			if (!"string".equals(cType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
			return answer;
		}
		if ("Date".equals(qType)) {
			if (!"date".equals(cType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to date but isn't");
			try {
				DateBean.parseDate(answer);
				return answer;
			} catch (Exception doNothingRightHere) {
				System.out.println("Failed to parse date [" + answer + "]");
				return "";
			}
		}
		if ("number".equals(cType)) {
			answer = answer.replace(",", "");
			try {
				Float parsedAnswer = Float.parseFloat(answer);
				return parsedAnswer.toString();
			} catch (Exception doNothingRightHere) {
				System.out.println("Failed to parse date [" + answer + "]");
				return "";
			}
		}
		if ("string".equals(cType)) {
			return answer;
		}
		System.out.println("Failed to parse type " + cType + " " + qType);
		return "";
	}
}
