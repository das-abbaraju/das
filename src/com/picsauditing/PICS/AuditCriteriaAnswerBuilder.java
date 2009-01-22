package com.picsauditing.PICS;

import java.util.List;

import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.util.AnswerMapByAudits;

public class AuditCriteriaAnswerBuilder {
	
	protected AnswerMapByAudits answerMapByAudits = null;
	protected List<FlagQuestionCriteria> criteria = null;
	
	
	public AuditCriteriaAnswerBuilder( AnswerMapByAudits answerMapByAudits, List<FlagQuestionCriteria> criteria) {
		this.answerMapByAudits = answerMapByAudits;
		this.criteria = criteria;
	}
	
	
	public List<AuditCriteriaAnswer> getFlagCalculationUnits() {
		
		return null;
	}

}
