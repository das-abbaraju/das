package com.picsauditing.PICS;

import java.util.Map;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;

public class AuditCriteriaAnswer {

	private AuditData answer = null;
	private Map<FlagColor, FlagQuestionCriteria> criteriaMap = null;
	private FlagColor resultColor = null;

	public AuditCriteriaAnswer(AuditData answer,
			Map<FlagColor, FlagQuestionCriteria> criteriaMap) {
		this.answer = answer;
		this.criteriaMap = criteriaMap;
	}

	public AuditData getAnswer() {
		return answer;
	}

	public Map<FlagColor, FlagQuestionCriteria> getCriteria() {
		return criteriaMap;
	}

	public FlagQuestionCriteria getResultReason() {
		return criteriaMap.get(getResultColor());
	}

	
	public FlagColor getResultColor() {
		if (resultColor == null) {
			calculate();
		}

		return resultColor;
	}

	private void calculate() {

		FlagColor response = null;
		
		for( FlagColor currentColor : criteriaMap.keySet() ) {
			
			FlagQuestionCriteria criteria = criteriaMap.get(currentColor);
			
			if (answer != null && answer.getAnswer() != null && answer.getAnswer().length() > 0) {
				// The contractor has answered this question
				// so it needs to be correct
				boolean isFlagged = false;
	
				if (criteria.isValidationRequired() && !answer.isVerified()) {
					isFlagged = true;
				}
	
				if (criteria.isFlagged(answer.getAnswer())) {
					isFlagged = true;
				}
	
				if (isFlagged) {
					response = FlagColor.getWorseColor(response, criteria.getFlagColor());
				} else {
					response = FlagColor.Green;
				}
			}
		}
		resultColor = response;
	}

	
	
	public AuditTypeClass getClassType() {
		return answer.getAudit().getAuditType().getClassType();
	}

}
