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

		auditCriteriaAnswers = new Vector<AuditCriteriaAnswer>();
		
		Map<AuditQuestion, List<FlagQuestionCriteria>> criteriaMapByQuestion = new HashMap<AuditQuestion, List<FlagQuestionCriteria>>();
		
		ContractorAccount contractor = null;
		
		for( FlagQuestionCriteria criteria : criterias ) {
			if (criteria.getChecked().equals(YesNo.Yes)) { // This question is required by the operator
				List<FlagQuestionCriteria> criteriaForQuestion = criteriaMapByQuestion.get(criteria.getAuditQuestion());
				
				if( criteriaForQuestion == null ) {
								
					criteriaForQuestion = new Vector<FlagQuestionCriteria>();
					criteriaMapByQuestion.put(criteria.getAuditQuestion(), criteriaForQuestion);
				}
				criteriaForQuestion.add(criteria);
			}
		}

		for( AuditQuestion question : criteriaMapByQuestion.keySet() ) {
			
			List<FlagQuestionCriteria> criteriasForThisQuestion = criteriaMapByQuestion.get(question);
			
			if( criteriasForThisQuestion == null || criteriasForThisQuestion.size() == 0 )
				continue;
			
			
			List<Map<FlagColor, FlagQuestionCriteria>> listOfCriteriaMapsForThisQuestion = new Vector<Map<FlagColor, FlagQuestionCriteria>>();
			if( criteriasForThisQuestion.size() == 1 ) {
				FlagQuestionCriteria thisCriteria = criteriasForThisQuestion.get(0);

				Map<FlagColor, FlagQuestionCriteria> tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
				tempMap.put(thisCriteria.getFlagColor(), thisCriteria);
				
				listOfCriteriaMapsForThisQuestion.add( tempMap );
			}
			else if ( criteriasForThisQuestion.size() == 2 ){
				FlagQuestionCriteria criteria1 = criteriasForThisQuestion.get(0);
				FlagQuestionCriteria criteria2 = criteriasForThisQuestion.get(1);
				
				if( criteria1.getMultiYearScope() != null && criteria2.getMultiYearScope() != null
						&& criteria1.getMultiYearScope() != criteria2.getMultiYearScope() ) {
					
					Map<FlagColor, FlagQuestionCriteria> tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
					tempMap.put(criteria1.getFlagColor(), criteria1);
					
					listOfCriteriaMapsForThisQuestion.add( tempMap );
					
					tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
					tempMap.put(criteria2.getFlagColor(), criteria2);
					
					listOfCriteriaMapsForThisQuestion.add( tempMap );
					
				}
				else {
					Map<FlagColor, FlagQuestionCriteria> tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
					tempMap.put(criteria1.getFlagColor(), criteria1);
					tempMap.put(criteria2.getFlagColor(), criteria2);
					
					listOfCriteriaMapsForThisQuestion.add( tempMap );
				}
			}
			else {
				throw new RuntimeException( "system error: more criteria types than expected");
			}
			
			
			//at this point we're going
			for( Map<FlagColor, FlagQuestionCriteria> thisMap : listOfCriteriaMapsForThisQuestion ) {
				
				FlagQuestionCriteria representativeCriteria = thisMap.values().iterator().next();
				
				AuditType criteriaAuditType = question.getSubCategory().getCategory().getAuditType();
				
				List<ContractorAudit> matchingConAudits = answerMapByAudits.getAuditSet(criteriaAuditType);
	
				if( matchingConAudits.size() > 0 ) {
				
					contractor = matchingConAudits.get(0).getContractorAccount();
					
					MultiYearScope scope = representativeCriteria.getMultiYearScope();
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
										data = answerMapByAudits.get(conAudit).get(question.getId());
									}
								} catch (Exception e) {
									System.out.println("Ignoring answer with year key: " + conAudit.getAuditFor());
								}
							}
							
							auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
	
						} else if (MultiYearScope.AllThreeYears.equals(scope)) {
							for (ContractorAudit conAudit : matchingConAudits) {
								AuditData data = answerMapByAudits.get(conAudit).get(question.getId());
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
							}
	
						} else if (MultiYearScope.ThreeYearAverage.equals(scope)) {
							List<AuditData> dataList = new ArrayList<AuditData>();
							for (ContractorAudit conAudit : matchingConAudits) {
								AuditData data = answerMapByAudits.get(conAudit).get(question.getId());
								if( data != null ) {
									dataList.add(data);
								}
							}
							AuditData data = AuditData.addAverageData(dataList);
							auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
						}
	
					} else {
						if (representativeCriteria.getMultiYearScope() == null && matchingConAudits.size() > 1)
							System.out.println("WARNING! Found more than one " + criteriaAuditType.getAuditName() 
									+ " for conID=" + contractor.getId());
						
						AnswerMap answerMap = answerMapByAudits.get(matchingConAudits.get(0));
						
						if(question.isAllowMultipleAnswers() ) {
							// this question is an anchor question that can have multiple answers
							// figure out if we should be optimistic or pessimistic here
							// I'm not going to spend much time on this 
							// because there are no existing use cases of using this yet
							for(AuditData data : answerMap.getAnswerList(question.getId()))
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
							
						} else if( question.getParentQuestion() != null ) {
							// These questions are "child" questions, so we must first find their parent
							for(AuditData parentData : answerMap.getAnswerList(question.getParentQuestion().getId())) {
								// For each row, get the child answer and evaluate it
								AuditData data = answerMap.get(question.getId(), parentData.getId());
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
							}
	
						} else {
							// DEFAULT : this is a normal (root/non child/non multiple) question
							AuditData data = answerMap.get(question.getId());
							auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
						}
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
