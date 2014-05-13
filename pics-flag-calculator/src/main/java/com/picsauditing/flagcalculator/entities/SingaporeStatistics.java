package com.picsauditing.flagcalculator.entities;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class SingaporeStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_WIR_FOR_THE_GIVEN_YEAR = 16631;
	public static final int QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR = 16632;
	public static final int QUESTION_ID_ODI_FOR_THE_GIVEN_YEAR = 16633;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 16624;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 16592 ;

	public SingaporeStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.SINGAPORE_MOM, data);
		answerMap = new HashMap<>();

		answerMap.put(OshaRateType.WIR, makeZeroAnswerData());
		answerMap.put(OshaRateType.AFR, makeZeroAnswerData());
		answerMap.put(OshaRateType.ODI, makeZeroAnswerData());
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
			} else if (answer.getQuestion().getId() == QUESTION_ID_WIR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.WIR, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.AFR, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_ODI_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.ODI, answer);
			}
		}
	}
}
