package com.picsauditing.flagcalculator.entities;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class PortugalStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_IR_FOR_THE_GIVEN_YEAR = 17138;
	public static final int QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR = 17137;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 17131;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 17130;

	public PortugalStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.PORTUGAL, data);
		answerMap = new HashMap<>();

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
}
