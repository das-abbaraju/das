package com.picsauditing.jpa.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MexicoStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR = 15345;
	public static final int QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR = 15344;
	public static final int QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR = 15343;
	public static final int QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR = 15342;
	public static final int QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR = 15341;
	public static final int QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR = 15354;
	public static final int QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR = 15353;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 15340;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 15339;

	public MexicoStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.MEXICO, data);
		answerMap = new HashMap<OshaRateType, AuditData>();
		
		answerMap.put(OshaRateType.TrirAbsolute, makeZeroAnswerData(QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.TrirNaics, makeZeroAnswerData(QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.TrirWIA, makeZeroAnswerData(QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.LwcrAbsolute, makeZeroAnswerData(QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.LwcrNaics, makeZeroAnswerData(QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.Fatalities, makeZeroAnswerData(QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.Hours, makeZeroAnswerData(QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.DaysAwayCases, makeZeroAnswerData(QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.DaysAway, makeZeroAnswerData(QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.JobTransfersCases, makeZeroAnswerData(QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.JobTransferDays, makeZeroAnswerData(QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.OtherRecordables, makeZeroAnswerData(QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR));
		
		for (AuditData answer : data) {
			if (answer.getAnswer().equals(QuestionFunction.MISSING_PARAMETER))
				continue;
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

	@SuppressWarnings("unchecked")
	@Override
	public List<AuditData> getQuestionsToVerify() {
		// We don't verify this country
		return Collections.emptyList();
	}
}
