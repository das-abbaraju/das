package com.picsauditing.PICS;

import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;

public class AuditCriteriaAnswer {

	private AuditData answer = null;
	private Map<FlagColor, FlagQuestionCriteria> criteria = null;
	private FlagColor resultColor = null;

	public AuditCriteriaAnswer(AuditData answer,
			Map<FlagColor, FlagQuestionCriteria> criteria) {
		this.answer = answer;
		this.criteria = criteria;
	}

	public AuditData getAnswer() {
		return answer;
	}

	public Map<FlagColor, FlagQuestionCriteria> getCriteria() {
		return criteria;
	}

	public FlagQuestionCriteria getResultReason() {
		return criteria.get(getResultColor());
	}

	
	public FlagColor getResultColor() {
		if (resultColor == null) {
			calculate();
		}

		return resultColor;
	}

	private void calculate() {

		resultColor = FlagColor.Green;
	}
	
	public AuditTypeClass getAuditClassType() {
		return answer.getAudit().getAuditType().getClassType();
	}
	
}
