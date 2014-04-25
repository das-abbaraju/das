package com.picsauditing.flagcalculator.entities;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class TurkeyStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_IR_FOR_THE_GIVEN_YEAR = 17160;
	public static final int QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR = 17158;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 17151;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 17151;

	public TurkeyStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.TURKEY, data);
		answerMap = new HashMap<OshaRateType, AuditData>();

		answerMap.put(OshaRateType.IR, makeZeroAnswerData());
		answerMap.put(OshaRateType.AFR, makeZeroAnswerData());
		answerMap.put(OshaRateType.Fatalities, makeZeroAnswerData());
		answerMap.put(OshaRateType.Hours, makeZeroAnswerData());

		for (AuditData answer : data) {
			if (StringUtils.isEmpty(answer.getAnswer()))
				continue;
			if (answer.getAnswer().equals(QuestionFunction.MISSING_PARAMETER))
				continue;
			if (answer.getQuestion().getId() == getOshaType().shaKeptQuestionId)
				shaKept = StringUtils.equals(answer.getAnswer(), "Yes");
			if (answer.getQuestion().getId() == QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.Fatalities, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.Hours, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.AFR, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_IR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.IR, answer);
			}
		}
	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<AuditData> getQuestionsToVerify() {
//		List<AuditData> questionsToVerify = new ArrayList<AuditData>();
//
//		questionsToVerify.add(answerMap.get(OshaRateType.Hours));
//		questionsToVerify.add(answerMap.get(OshaRateType.Fatalities));
//		questionsToVerify.add(answerMap.get(OshaRateType.AFR));
//		questionsToVerify.add(answerMap.get(OshaRateType.IR));
//
//		return questionsToVerify;
//	}
}
