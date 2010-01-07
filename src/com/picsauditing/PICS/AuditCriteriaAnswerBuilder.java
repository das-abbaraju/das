package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.AnswerMapByAudits;
import com.picsauditing.util.log.PicsLogger;

/**
 * Utility used to create ACAs {@link AuditCriteriaAnswer} given
 * AnswerMapByAudits (usually for a given operator) and a full list of criteria
 * 
 * @author Trevor
 * 
 */
public class AuditCriteriaAnswerBuilder {

	private AnswerMapByAudits answerMapByAudits = null;
	private List<FlagQuestionCriteria> criterias = null;

	private List<AuditCriteriaAnswer> auditCriteriaAnswers = null;

	public AuditCriteriaAnswerBuilder(AnswerMapByAudits answerMapByAudits, List<FlagQuestionCriteria> criteria) {
		this.answerMapByAudits = answerMapByAudits;
		this.criterias = criteria;
	}

	public List<AuditCriteriaAnswer> getAuditCriteriaAnswers() {

		if (auditCriteriaAnswers == null) {
			build();
		}

		return auditCriteriaAnswers;
	}

	private void build() {
		PicsLogger.start("AuditCriteriaAnswerBuilder.build");

		if (criterias == null)
			System.out.println("AuditCriteriaAnswerBuilder WARNING: List<FlagQuestionCriteria> criterias is NULL");
		// else if (criterias.size() == 0)
		// System.out.println("AuditCriteriaAnswerBuilder WARNING: List<FlagQuestionCriteria> criterias is empty");

		if (answerMapByAudits == null)
			System.out.println("AuditCriteriaAnswerBuilder WARNING: AnswerMapByAudits answerMapByAudits is NULL");

		auditCriteriaAnswers = new Vector<AuditCriteriaAnswer>();

		Map<AuditQuestion, List<FlagQuestionCriteria>> criteriaMapByQuestion = new HashMap<AuditQuestion, List<FlagQuestionCriteria>>();

		for (FlagQuestionCriteria criteria : criterias) {
			List<FlagQuestionCriteria> criteriaForQuestion = criteriaMapByQuestion.get(criteria.getAuditQuestion());

			if (criteriaForQuestion == null) {

				criteriaForQuestion = new Vector<FlagQuestionCriteria>();
				criteriaMapByQuestion.put(criteria.getAuditQuestion(), criteriaForQuestion);
			}
			PicsLogger.log("loading criteria into map - " + criteria.getFlagColor() + " for "
					+ criteria.getAuditQuestion().getQuestion());
			criteriaForQuestion.add(criteria);
		}

		for (AuditQuestion question : criteriaMapByQuestion.keySet()) {
			PicsLogger.log("building ACA for " + question.getQuestion());

			List<FlagQuestionCriteria> criteriasForThisQuestion = criteriaMapByQuestion.get(question);

			if (criteriasForThisQuestion == null || criteriasForThisQuestion.size() == 0)
				continue;

			List<Map<FlagColor, FlagQuestionCriteria>> listOfCriteriaMapsForThisQuestion = new Vector<Map<FlagColor, FlagQuestionCriteria>>();
			Map<FlagColor, FlagQuestionCriteria> tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
			if (criteriasForThisQuestion.size() == 1) {
				FlagQuestionCriteria thisCriteria = criteriasForThisQuestion.get(0);

				tempMap.put(thisCriteria.getFlagColor(), thisCriteria);

				listOfCriteriaMapsForThisQuestion.add(tempMap);
				PicsLogger.log("found 1 criteria" + thisCriteria.getFlagColor());
			} else if (criteriasForThisQuestion.size() == 2) {
				FlagQuestionCriteria criteria1 = criteriasForThisQuestion.get(0);
				FlagQuestionCriteria criteria2 = criteriasForThisQuestion.get(1);

				if (criteria1.getMultiYearScope() != null && criteria2.getMultiYearScope() != null
						&& criteria1.getMultiYearScope() != criteria2.getMultiYearScope()) {

					tempMap.put(criteria1.getFlagColor(), criteria1);

					listOfCriteriaMapsForThisQuestion.add(tempMap);

					tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
					tempMap.put(criteria2.getFlagColor(), criteria2);

					PicsLogger.log("found 2 criteria with MultiYearScope ");
					listOfCriteriaMapsForThisQuestion.add(tempMap);

				} else {
					tempMap.put(criteria1.getFlagColor(), criteria1);
					tempMap.put(criteria2.getFlagColor(), criteria2);

					PicsLogger.log("found 2 criteria");
					listOfCriteriaMapsForThisQuestion.add(tempMap);
				}
			} else {
				throw new RuntimeException("system error: more criteria types than expected");
			}

			// at this point we finally have our list of maps and we can start
			// looking for answers
			for (Map<FlagColor, FlagQuestionCriteria> thisMap : listOfCriteriaMapsForThisQuestion) {

				// Get the first criteria from the map to use in "common"
				// calculations below
				MultiYearScope scope = null;
				for (FlagQuestionCriteria fqc : thisMap.values())
					if (scope == null)
						scope = fqc.getMultiYearScope();

				AuditType criteriaAuditType = question.getSubCategory().getCategory().getAuditType();

				List<ContractorAudit> matchingConAudits = answerMapByAudits.getAuditSet(criteriaAuditType);

				if (matchingConAudits.size() > 0) {
					PicsLogger.log("found audit that may contain answer");

					if (scope != null) {
						PicsLogger.log("getting answer using scope=" + scope);
						if (MultiYearScope.LastYearOnly.equals(scope)) {
							AuditData data = null;

							// Get the most recent year
							int mostRecentYear = 0;
							for (ContractorAudit conAudit : matchingConAudits) {
								try {
									int year = Integer.parseInt(conAudit.getAuditFor());
									if (year > mostRecentYear) {
										mostRecentYear = year;
										data = answerMapByAudits.get(conAudit).get(question.getId());
									}
								} catch (Exception e) {
									System.out.println("Ignoring answer with year key: " + conAudit.getAuditFor());
								}
							}
							if (data != null && data.getAnswer() != null && data.getAnswer().length() > 0)
								auditCriteriaAnswers.add(new AuditCriteriaAnswer(data, thisMap));

						} else if (MultiYearScope.AllThreeYears.equals(scope)) {
							for (ContractorAudit conAudit : matchingConAudits) {
								AuditData data = answerMapByAudits.get(conAudit).get(question.getId());
								if (data != null && data.getAnswer() != null && data.getAnswer().length() > 0)
									auditCriteriaAnswers.add(new AuditCriteriaAnswer(data, thisMap));
							}

						} else if (MultiYearScope.ThreeYearAverage.equals(scope)) {
							List<AuditData> dataList = new ArrayList<AuditData>();
							int count = 0;
							for (ContractorAudit conAudit : matchingConAudits) {
								if (count < 3) {
									AuditData data = answerMapByAudits.get(conAudit).get(question.getId());
									if (data != null) {
										dataList.add(data);
										count++;
									}
								}
							}
							AuditData data = AuditData.addAverageData(dataList);
							if (data != null && data.getAnswer() != null && data.getAnswer().length() > 0)
								auditCriteriaAnswers.add(new AuditCriteriaAnswer(data, thisMap));
						}

					} else {
						if (matchingConAudits.size() > 1) {
							ContractorAccount contractor = matchingConAudits.get(0).getContractorAccount();
							System.out.println("WARNING! Found more than one " + criteriaAuditType.getAuditName()
									+ " for conID=" + contractor.getId());
						}

						AnswerMap answerMap = answerMapByAudits.get(matchingConAudits.get(0));

						// DEFAULT : this is a normal (root/non child/non
						// multiple) question
						AuditData data = answerMap.get(question.getId());
						if (data != null && data.getAnswer() != null && data.getAnswer().length() > 0) {
							PicsLogger.log("found answer = " + data.getAnswer());
							auditCriteriaAnswers.add(new AuditCriteriaAnswer(data, thisMap));
						} else if (thisMap.values().iterator().next().getAuditQuestion().getQuestionType().equals(
								"NULLSAREBAD")) {
							auditCriteriaAnswers.add(new AuditCriteriaAnswer(data, thisMap));
						}
					}
				}
			}
		}
		PicsLogger.stop();
	}

	public AnswerMapByAudits getAnswerMapByAudits() {
		return answerMapByAudits;
	}

	public List<FlagQuestionCriteria> getCriteria() {
		return criterias;
	}
}
