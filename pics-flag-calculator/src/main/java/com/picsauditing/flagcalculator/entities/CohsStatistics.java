package com.picsauditing.flagcalculator.entities;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class CohsStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR = 8817;
	public static final int QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR = 11119;
	public static final int QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR = 8844;
	public static final int QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR = 8843;
	public static final int QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR = 8842;
	public static final int QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR = 11118;
	public static final int QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR = 11117;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 8841;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 8839;

	public CohsStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.COHS, data);
		answerMap = new HashMap<>();
		
		answerMap.put(OshaRateType.TrirAbsolute, makeZeroAnswerData());
		answerMap.put(OshaRateType.TrirNaics, makeZeroAnswerData());
		answerMap.put(OshaRateType.TrirWIA, makeZeroAnswerData());
		answerMap.put(OshaRateType.LwcrAbsolute, makeZeroAnswerData());
		answerMap.put(OshaRateType.LwcrNaics, makeZeroAnswerData());
		answerMap.put(OshaRateType.Fatalities, makeZeroAnswerData());
		answerMap.put(OshaRateType.Hours, makeZeroAnswerData());
		answerMap.put(OshaRateType.DaysAwayCases, makeZeroAnswerData());
		answerMap.put(OshaRateType.DaysAway, makeZeroAnswerData());
		answerMap.put(OshaRateType.JobTransfersCases, makeZeroAnswerData());
		answerMap.put(OshaRateType.JobTransferDays, makeZeroAnswerData());
		answerMap.put(OshaRateType.OtherRecordables, makeZeroAnswerData());
		
		for (AuditData answer : data) {
			if (answer.getQuestion().getId() == getOshaType().shaKeptQuestionId)
				shaKept = StringUtils.equals(answer.getAnswer(), "Yes");
			if (answer.getQuestion().getId() == QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.TrirAbsolute, answer);
				answerMap.put(OshaRateType.TrirNaics, answer);
				answerMap.put(OshaRateType.TrirWIA, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.LwcrAbsolute, answer);
				answerMap.put(OshaRateType.LwcrNaics, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.Fatalities, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.Hours, answer);
                hoursWorked = answer;
			} else if (answer.getQuestion().getId() == QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.DaysAwayCases, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.DaysAway, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.JobTransfersCases, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.JobTransferDays, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.OtherRecordables, answer);
			}
		}
	}
}
