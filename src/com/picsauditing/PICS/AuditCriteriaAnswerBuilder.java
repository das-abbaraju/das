package com.picsauditing.PICS;

import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.util.AnswerMapByAudits;

public class AuditCriteriaAnswerBuilder {
	
	private AnswerMapByAudits answerMapByAudits = null;
	private List<FlagQuestionCriteria> criteria = null;
	
	private List<AuditCriteriaAnswer> auditCriteriaAnswers = null;
	
	public AuditCriteriaAnswerBuilder( AnswerMapByAudits answerMapByAudits, List<FlagQuestionCriteria> criteria) {
		this.answerMapByAudits = answerMapByAudits;
		this.criteria = criteria;
	}
	
	
	public List<AuditCriteriaAnswer> getAuditCriteriaAnswers() {
	
		if( auditCriteriaAnswers == null ) {
			build();
		}
		
		return auditCriteriaAnswers;
	}
	
	
	private void build() {
		
		auditCriteriaAnswers = new Vector<AuditCriteriaAnswer>();
	}


	public AnswerMapByAudits getAnswerMapByAudits() {
		return answerMapByAudits;
	}

	public List<FlagQuestionCriteria> getCriteria() {
		return criteria;
	}
}
