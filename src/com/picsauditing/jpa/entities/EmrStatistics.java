package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmrStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR = 2034;

	public EmrStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.EMR, data);
		answerMap = new HashMap<OshaRateType, AuditData>();
		
		answerMap.put(OshaRateType.EMR, makeZeroAnswerData(QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR));

		for (AuditData answer : data) {
			if (answer.getQuestion().getId() == QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR
					&& answer.getQuestion().isVisibleInAudit(answer.getAudit())) {
				answerMap.put(OshaRateType.EMR, answer);
			}
		}
	}

	@Override
	public List<AuditData> getQuestionsToVerify() {
		List<AuditData> questionsToVerify = new ArrayList<AuditData>();
		return questionsToVerify;
	}
}
