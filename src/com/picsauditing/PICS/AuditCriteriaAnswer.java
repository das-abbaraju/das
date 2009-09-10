package com.picsauditing.PICS;

import java.util.Map;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.util.SpringUtils;

/**
 * A simple POJO that evaluates an Audit answer based against a given set of criteria and returns a flag color
 * This is often referred to an "ACA"
 * @author Trevor
 *
 */
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
		return getResultColor(false);
	}

	public FlagColor getResultColor(boolean recalculate) {
		if (!recalculate && resultColor != null)
			return resultColor;

		resultColor = FlagColor.Green;
		for( FlagColor currentColor : criteriaMap.keySet() ) {
			
			FlagQuestionCriteria criteria = criteriaMap.get(currentColor);
			
			if (answer != null && answer.getAnswer() != null && answer.getAnswer().length() > 0) {
				// The contractor has answered this question
				// so it needs to be correct
				boolean isFlagged = false;
	
				if (criteria.isValidationRequired() && !answer.isVerified()) {
					isFlagged = true;
				}
				
				if(criteria.getAuditQuestion().getQuestionType().equals("AMBest")) {
					AmBestDAO amBestDAO = (AmBestDAO) SpringUtils.getBean("AmBestDAO");
					AmBest amBest = amBestDAO.findByNaic(answer.getComment());
					if(amBest != null) {
						if(criteria.isFlagged(amBest.getRatingCode()+"|"+amBest.getFinancialCode()))
							isFlagged = true;
					}
				}
				else {
					if (criteria.isFlagged(answer.getAnswer())) {
						isFlagged = true;
					}
				}
				if (isFlagged) {
					resultColor = FlagColor.getWorseColor(resultColor, criteria.getFlagColor());
				}
			}
			else if ( criteria.getAuditQuestion().getQuestionType().equals("NULLSAREBAD") ) {
				resultColor = FlagColor.getWorseColor(resultColor, criteria.getFlagColor());	
			}

		}
		return resultColor;
	}

	
	
	public AuditTypeClass getClassType() {
		return answer.getAudit().getAuditType().getClassType();
	}

}
