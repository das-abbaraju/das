package com.picsauditing.PICS;

import java.util.List;

import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.util.AnswerMapByAudits;

public class FlagCalculationBuilder {
	
	protected AnswerMapByAudits answerMapByAudits = null;
	protected List<FlagQuestionCriteria> criteria = null;
	
	
	public FlagCalculationBuilder( AnswerMapByAudits answerMapByAudits, List<FlagQuestionCriteria> criteria) {
		this.answerMapByAudits = answerMapByAudits;
		this.criteria = criteria;
	}
	
	
	public List<FlagCalculationUnit> getFlagCalculationUnits() {
		
		return null;
	}

}
