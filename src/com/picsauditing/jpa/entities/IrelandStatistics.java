package com.picsauditing.jpa.entities;

import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IrelandStatistics extends SafetyStatistics{

	public static final int QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR = 15669;
	public static final int QUESTION_ID_IR_FOR_THE_GIVEN_YEAR = 15670;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 15663;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 15662 ;

	public IrelandStatistics(int year, List<AuditData> data, boolean categoryApplies) {
	super(year, OshaType.IRELAND, data);
	answerMap = new HashMap<OshaRateType, AuditData>();

	answerMap.put(OshaRateType.IFR, makeZeroAnswerData(QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR));
	answerMap.put(OshaRateType.IR, makeZeroAnswerData(QUESTION_ID_IR_FOR_THE_GIVEN_YEAR));
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
			answerMap.put(OshaRateType.IFR, answer);
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
		questionsToVerify.add(answerMap.get(OshaRateType.IR));
		questionsToVerify.add(answerMap.get(OshaRateType.IFR));

		return questionsToVerify;
	}
}
