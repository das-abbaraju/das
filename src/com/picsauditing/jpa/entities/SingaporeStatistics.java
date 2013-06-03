package com.picsauditing.jpa.entities;

import com.picsauditing.util.Strings;

import java.util.ArrayList;
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
		answerMap = new HashMap<OshaRateType, AuditData>();

		answerMap.put(OshaRateType.WIR, makeZeroAnswerData(QUESTION_ID_WIR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.AFR, makeZeroAnswerData(QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.ODI, makeZeroAnswerData(QUESTION_ID_ODI_FOR_THE_GIVEN_YEAR));
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
			} else if (answer.getQuestion().getId() == QUESTION_ID_WIR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.WIR, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.AFR, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_ODI_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.ODI, answer);
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
		questionsToVerify.add(answerMap.get(OshaRateType.WIR));
		questionsToVerify.add(answerMap.get(OshaRateType.ODI));

		return questionsToVerify;
	}
}
