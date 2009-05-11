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
import com.picsauditing.util.log.PicsLogger;

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
		PicsLogger.start("AuditCriteriaAnswerBuilder.build");

		auditCriteriaAnswers = new Vector<AuditCriteriaAnswer>();
		
		Map<AuditQuestion, List<FlagQuestionCriteria>> criteriaMapByQuestion = new HashMap<AuditQuestion, List<FlagQuestionCriteria>>();
		
		for( FlagQuestionCriteria criteria : criterias ) {
			List<FlagQuestionCriteria> criteriaForQuestion = criteriaMapByQuestion.get(criteria.getAuditQuestion());
			
			if( criteriaForQuestion == null ) {
							
				criteriaForQuestion = new Vector<FlagQuestionCriteria>();
				criteriaMapByQuestion.put(criteria.getAuditQuestion(), criteriaForQuestion);
			}
			criteriaForQuestion.add(criteria);
		}

		for( AuditQuestion question : criteriaMapByQuestion.keySet() ) {
			
			List<FlagQuestionCriteria> criteriasForThisQuestion = criteriaMapByQuestion.get(question);
			
			if( criteriasForThisQuestion == null || criteriasForThisQuestion.size() == 0 )
				continue;
			
			
			List<Map<FlagColor, FlagQuestionCriteria>> listOfCriteriaMapsForThisQuestion = new Vector<Map<FlagColor, FlagQuestionCriteria>>();
			Map<FlagColor, FlagQuestionCriteria> tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
			if( criteriasForThisQuestion.size() == 1 ) {
				FlagQuestionCriteria thisCriteria = criteriasForThisQuestion.get(0);

				tempMap.put(thisCriteria.getFlagColor(), thisCriteria);
				
				listOfCriteriaMapsForThisQuestion.add( tempMap );
			}
			else if ( criteriasForThisQuestion.size() == 2 ){
				FlagQuestionCriteria criteria1 = criteriasForThisQuestion.get(0);
				FlagQuestionCriteria criteria2 = criteriasForThisQuestion.get(1);
				
				if( criteria1.getMultiYearScope() != null && criteria2.getMultiYearScope() != null
						&& criteria1.getMultiYearScope() != criteria2.getMultiYearScope() ) {
					
					tempMap.put(criteria1.getFlagColor(), criteria1);
					
					listOfCriteriaMapsForThisQuestion.add( tempMap );
					
					tempMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
					tempMap.put(criteria2.getFlagColor(), criteria2);
					
					listOfCriteriaMapsForThisQuestion.add( tempMap );
					
				}
				else {
					tempMap.put(criteria1.getFlagColor(), criteria1);
					tempMap.put(criteria2.getFlagColor(), criteria2);
					
					listOfCriteriaMapsForThisQuestion.add( tempMap );
				}
			}
			else {
				throw new RuntimeException( "system error: more criteria types than expected");
			}
			
			
			//at this point we finally have our list of maps and we can start looking for answers
			for( Map<FlagColor, FlagQuestionCriteria> thisMap : listOfCriteriaMapsForThisQuestion ) {
				
				// Get the first criteria from the map to use in "common" calculations below
				MultiYearScope scope = null;
				for(FlagQuestionCriteria fqc : thisMap.values())
					if (scope == null)
						scope = fqc.getMultiYearScope();
				
				AuditType criteriaAuditType = question.getSubCategory().getCategory().getAuditType();
				
				List<ContractorAudit> matchingConAudits = answerMapByAudits.getAuditSet(criteriaAuditType);
	
				if( matchingConAudits.size() > 0 ) {
				
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
							if( data != null && data.getAnswer() != null && data.getAnswer().length() > 0 )
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
	
						} else if (MultiYearScope.AllThreeYears.equals(scope)) {
							for (ContractorAudit conAudit : matchingConAudits) {
								AuditData data = answerMapByAudits.get(conAudit).get(question.getId());
								if( data != null && data.getAnswer() != null && data.getAnswer().length() > 0 )
									auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
							}
	
						} else if (MultiYearScope.ThreeYearAverage.equals(scope)) {
							List<AuditData> dataList = new ArrayList<AuditData>();
							int count = 0;
							if(count < 3) {
								for (ContractorAudit conAudit : matchingConAudits) {
									AuditData data = answerMapByAudits.get(conAudit).get(question.getId());
									if( data != null ) {
										dataList.add(data);
										count++;
									}
								}
							}
							AuditData data = AuditData.addAverageData(dataList);
							if( data != null && data.getAnswer() != null && data.getAnswer().length() > 0 )
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
						}
	
					} else {
						if (matchingConAudits.size() > 1) {
							ContractorAccount contractor = matchingConAudits.get(0).getContractorAccount();
							PicsLogger.start("acaBuilder_warnings");
							PicsLogger.log("WARNING! Found more than one " + criteriaAuditType.getAuditName() 
									+ " for conID=" + contractor.getId());
							PicsLogger.stop();
						}
						
						AnswerMap answerMap = answerMapByAudits.get(matchingConAudits.get(0));
						
						if(question.isAllowMultipleAnswers() ) {
							// this question is an anchor question that can have multiple answers
							// figure out if we should be optimistic or pessimistic here
							// I'm not going to spend much time on this 
							// because there are no existing use cases of using this yet
							for(AuditData data : answerMap.getAnswerList(question.getId())) {
								if( data != null && data.getAnswer() != null && data.getAnswer().length() > 0 ) {
									auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
								}
								else if ( thisMap.values().iterator().next().getAuditQuestion().getQuestionType().equals("NULLSAREBAD") ) {
									auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
								}
							}
							
						} else if( question.getParentQuestion() != null ) {
							// These questions are "child" questions, so we must first find their parent
							for(AuditData parentData : answerMap.getAnswerList(question.getParentQuestion().getId())) {
								// For each row, get the child answer and evaluate it
								AuditData data = answerMap.get(question.getId(), parentData.getId());
								if( data != null && data.getAnswer() != null && data.getAnswer().length() > 0 ) {
									auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
								}
								else if ( thisMap.values().iterator().next().getAuditQuestion().getQuestionType().equals("NULLSAREBAD") ) {
									auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
								}
							}
	
						} else {
							// DEFAULT : this is a normal (root/non child/non multiple) question
							AuditData data = answerMap.get(question.getId());
							if( data != null && data.getAnswer() != null && data.getAnswer().length() > 0 )
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
							else if ( thisMap.values().iterator().next().getAuditQuestion().getQuestionType().equals("NULLSAREBAD") ) {
								auditCriteriaAnswers.add(new AuditCriteriaAnswer( data, thisMap));
							}
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
