package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.AnswerMapByAudits;

public class AuditCriteriaAnswerBuilder {
	
	private AnswerMapByAudits answerMapByAudits = null;
	private List<FlagQuestionCriteria> criterias = null;
	
	private List<AuditCriteriaAnswer> auditCriteriaAnswers = null;
	
	public AuditCriteriaAnswerBuilder( AnswerMapByAudits answerMapByAudits, List<FlagQuestionCriteria> criteria) {
		this.answerMapByAudits = answerMapByAudits;
		this.criterias = criteria;
	}
	
	
	public List<AuditCriteriaAnswer> getAuditCriteriaAnswers() {
	
		if( auditCriteriaAnswers == null ) {
			build();
		}
		
		return auditCriteriaAnswers;
	}
	
	@SuppressWarnings("unchecked")
	private void build() {

		Map<AuditQuestion, Map<FlagColor, FlagQuestionCriteria>> criteriaMapByQuestion = new HashMap<AuditQuestion, Map<FlagColor, FlagQuestionCriteria>>();
		
		ContractorAccount contractor = null;
		
		for( FlagQuestionCriteria criteria : criterias ) {
			if (criteria.getChecked().equals(YesNo.Yes)) { // This question is required by the operator
				Map<FlagColor, FlagQuestionCriteria> criteriaForQuestion = criteriaMapByQuestion.get(criteria.getAuditQuestion());
				
				if( criteriaForQuestion == null ) {
								
					criteriaForQuestion = new TreeMap();
					criteriaMapByQuestion.put(criteria.getAuditQuestion(), criteriaForQuestion);
				}
	
				criteriaForQuestion.put(criteria.getFlagColor(), criteria);
			}
		}

		for (FlagQuestionCriteria criteria : criterias) {
		
			AuditQuestion criteriaQuestion = criteria.getAuditQuestion();
			AuditType criteriaAuditType = criteriaQuestion.getSubCategory().getCategory().getAuditType();
			
			List<ContractorAudit> matchingConAudits = answerMapByAudits.getAuditSet(criteriaAuditType);
			
			if( matchingConAudits.size() > 0 ) {
			
				contractor = matchingConAudits.get(0).getContractorAccount();
				
				MultiYearScope scope = criteria.getMultiYearScope();
				if( scope != null ) {
					if (MultiYearScope.LastYearOnly.equals(scope)) {
						AuditData data = null;

						// Get the most recent year
						int mostRecentYear = 0;
						for (ContractorAudit conAudit : matchingConAudits) {
							try {
								int year = Integer.parseInt(conAudit.getAuditFor());
								if (year > mostRecentYear) {
									mostRecentYear = year;
									data = answerMapByAudits.get(conAudit).get(criteriaQuestion.getId());
								}
							} catch (Exception e) {
								System.out.println("Ignoring answer with year key: " + conAudit.getAuditFor());
							}
						}
						
						auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, criteriaMapByQuestion.get(data.getQuestion())));

					} else if (MultiYearScope.AllThreeYears.equals(scope)) {
						for (ContractorAudit conAudit : matchingConAudits) {
							AuditData data = answerMapByAudits.get(conAudit).get(criteriaQuestion.getId());
							auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, criteriaMapByQuestion.get(data.getQuestion())));
						}

					} else if (MultiYearScope.ThreeYearAverage.equals(scope)) {
						List<AuditData> dataList = new ArrayList<AuditData>();
						for (ContractorAudit conAudit : matchingConAudits) {
							AuditData data = answerMapByAudits.get(conAudit).get(criteriaQuestion.getId());
							if( data != null ) {
								dataList.add(data);
							}
						}
						AuditData data = AuditData.addAverageData(dataList);
						auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, criteriaMapByQuestion.get(data.getQuestion())));
					}

				} else {
					if (criteria.getMultiYearScope() == null && matchingConAudits.size() > 1)
						System.out.println("WARNING! Found more than one " + criteriaAuditType.getAuditName() 
								+ " for conID=" + contractor.getId());
					
					AnswerMap answerMap = answerMapByAudits.get(matchingConAudits.get(0));
					
					if(criteriaQuestion.isAllowMultipleAnswers() ) {
						// this question is an anchor question that can have multiple answers
						// figure out if we should be optimistic or pessimistic here
						// I'm not going to spend much time on this 
						// because there are no existing use cases of using this yet
						for(AuditData data : answerMap.getAnswerList(criteriaQuestion.getId()))
							auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, criteriaMapByQuestion.get(data.getQuestion())));
						
					} else if( criteriaQuestion.getParentQuestion() != null ) {
						// These questions are "child" questions, so we must first find their parent
						for(AuditData parentData : answerMap.getAnswerList(criteriaQuestion.getParentQuestion().getId())) {
							// For each row, get the child answer and evaluate it
							AuditData data = answerMap.get(criteriaQuestion.getId(), parentData.getId());
							auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, criteriaMapByQuestion.get(data.getQuestion())));
						}

					} else {
						// DEFAULT : this is a normal (root/non child/non multiple) question
						AuditData data = answerMap.get(criteriaQuestion.getId());
						auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, criteriaMapByQuestion.get(data.getQuestion())));
					}
				}
			}
		}
	}


	public AnswerMapByAudits getAnswerMapByAudits() {
		return answerMapByAudits;
	}

	public List<FlagQuestionCriteria> getCriteria() {
		return criterias;
	}
}
