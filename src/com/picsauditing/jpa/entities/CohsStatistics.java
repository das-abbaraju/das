package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

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

	public CohsStatistics(int year, List<AuditData> data, OshaAudit oshaAudit) {
		super(year, OshaType.COHS, data, oshaAudit);
		answerMap = new HashMap<OshaRateType, AuditData>();
		for (AuditData answer : data) {
			if (answer.getQuestion().getId() == QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.TrirAbsolute, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.LwcrAbsolute, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Fatalities, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Hours, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.DaysAwayCases, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.DaysAway, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.JobTransfersCases, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.JobTransferDays, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.OtherRecordables, answer);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuditData> getQuestionsToVerify() {
		// We don't verify COHS
		return Collections.emptyList();
	}
}
