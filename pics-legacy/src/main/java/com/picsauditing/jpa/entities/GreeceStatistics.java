package com.picsauditing.jpa.entities;

import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GreeceStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_IR_FOR_THE_GIVEN_YEAR = 17212;
	public static final int QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR = 17211;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 17205;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 17204;

	public GreeceStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.GREECE, data);
		answerMap = new HashMap<OshaRateType, AuditData>();

		answerMap.put(OshaRateType.IR, makeZeroAnswerData(QUESTION_ID_IR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.AFR, makeZeroAnswerData(QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.Fatalities, makeZeroAnswerData(QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.Hours, makeZeroAnswerData(QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR));

		for (AuditData answer : data) {
			if (Strings.isEmpty(answer.getAnswer()))
				continue;
			if (answer.getAnswer().equals(QuestionFunction.MISSING_PARAMETER))
				continue;
			if (answer.getQuestion().getId() == getOshaType().shaKeptQuestionId)
				shaKept = Strings.isEqualNullSafe(answer.getAnswer(), "Yes");
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

	@SuppressWarnings("unchecked")
	@Override
	public List<AuditData> getQuestionsToVerify() {
		List<AuditData> questionsToVerify = new ArrayList<AuditData>();

		questionsToVerify.add(answerMap.get(OshaRateType.Hours));
		questionsToVerify.add(answerMap.get(OshaRateType.Fatalities));
		questionsToVerify.add(answerMap.get(OshaRateType.AFR));
		questionsToVerify.add(answerMap.get(OshaRateType.IR));

		return questionsToVerify;
	}
}