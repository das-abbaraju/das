package com.picsauditing.flagcalculator.entities;

import org.apache.commons.lang.StringUtils;

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
        answerMap = new HashMap<>();

        answerMap.put(OshaRateType.ATLR, makeZeroAnswerData());
        answerMap.put(OshaRateType.FR, makeZeroAnswerData());
        answerMap.put(OshaRateType.IR, makeZeroAnswerData());
        answerMap.put(OshaRateType.LTIFR, makeZeroAnswerData());
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
}
