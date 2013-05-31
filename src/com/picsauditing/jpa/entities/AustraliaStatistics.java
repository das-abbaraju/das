package com.picsauditing.jpa.entities;

import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AustraliaStatistics extends SafetyStatistics {

    public static final int QUESTION_ID_ATLR_FOR_THE_GIVEN_YEAR = 15228;
    public static final int QUESTION_ID_FR_FOR_THE_GIVEN_YEAR = 15227;
    public static final int QUESTION_ID_IR_FOR_THE_GIVEN_YEAR = 15226;
    public static final int QUESTION_ID_LTFR_FOR_THE_GIVEN_YEAR = 15225;
    public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 15217;
    public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 15216 ;

    public AustraliaStatistics(int year, List<AuditData> data, boolean categoryApplies) {
        super(year, OshaType.AUSTRALIA, data);
        answerMap = new HashMap<OshaRateType, AuditData>();

        answerMap.put(OshaRateType.ATLR, makeZeroAnswerData(QUESTION_ID_ATLR_FOR_THE_GIVEN_YEAR));
        answerMap.put(OshaRateType.FR, makeZeroAnswerData(QUESTION_ID_FR_FOR_THE_GIVEN_YEAR));
        answerMap.put(OshaRateType.IR, makeZeroAnswerData(QUESTION_ID_IR_FOR_THE_GIVEN_YEAR));
        answerMap.put(OshaRateType.LTIFR, makeZeroAnswerData(QUESTION_ID_LTFR_FOR_THE_GIVEN_YEAR));
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
            } else if (answer.getQuestion().getId() == QUESTION_ID_ATLR_FOR_THE_GIVEN_YEAR && categoryApplies) {
                answerMap.put(OshaRateType.ATLR, answer);
            } else if (answer.getQuestion().getId() == QUESTION_ID_FR_FOR_THE_GIVEN_YEAR && categoryApplies) {
                answerMap.put(OshaRateType.FR, answer);
            } else if (answer.getQuestion().getId() == QUESTION_ID_IR_FOR_THE_GIVEN_YEAR && categoryApplies) {
                answerMap.put(OshaRateType.IR, answer);
            } else if (answer.getQuestion().getId() == QUESTION_ID_LTFR_FOR_THE_GIVEN_YEAR && categoryApplies) {
                answerMap.put(OshaRateType.LTIFR, answer);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AuditData> getQuestionsToVerify() {
        List<AuditData> questionsToVerify = new ArrayList<AuditData>();

        questionsToVerify.add(answerMap.get(OshaRateType.Hours));
        questionsToVerify.add(answerMap.get(OshaRateType.Fatalities));
        questionsToVerify.add(answerMap.get(OshaRateType.LTIFR));
        questionsToVerify.add(answerMap.get(OshaRateType.IR));
        questionsToVerify.add(answerMap.get(OshaRateType.FR));
        questionsToVerify.add(answerMap.get(OshaRateType.ATLR));

        return questionsToVerify;
    }
}
