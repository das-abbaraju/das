package com.picsauditing.flagcalculator.entities;

import com.picsauditing.flagcalculator.service.AuditService;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class EmrStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR = 2034;

	public EmrStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.EMR, data);
		answerMap = new HashMap<>();
		
		for (AuditData answer : data) {
			if (answer.getQuestion().getId() == getOshaType().shaKeptQuestionId)
				shaKept = StringUtils.equals(answer.getAnswer(), "Yes");
			if (answer.getQuestion().getId() == QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR
					&& AuditService.isVisibleInAudit(answer.getQuestion(), answer.getAudit())) {
				answerMap.put(OshaRateType.EMR, answer);
			}
		}
	}
//
//	@Override
//	public List<AuditData> getQuestionsToVerify() {
//		List<AuditData> questionsToVerify = new ArrayList<AuditData>();
//		return questionsToVerify;
//	}
}
