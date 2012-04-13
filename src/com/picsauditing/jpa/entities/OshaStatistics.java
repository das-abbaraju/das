package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class OshaStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_PICS_SEVERITY_RATE_FOR_THE_GIVEN_YEAR = 9781;
	public static final int QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR = 8817;
	public static final int QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR = 8816;
	public static final int QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR = 8815;
	public static final int QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR = 8814;
	public static final int QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR = 8813;
	public static final int QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR = 8978;
	public static final int QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR = 8977;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 8812;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 8810;
	public static final int QUESTION_ID_FILE_UPLOAD_FOR_THE_GIVEN_YEAR = 8811;
	public static final int QUESTION_ID_DART_FOR_THE_GIVEN_YEAR = 9778;
	
	public OshaStatistics(int year, List<AuditData> data) {
		super(year, OshaType.OSHA, data);
		answerMap = new HashMap<OshaRateType, AuditData>();
		for (AuditData answer: data) {
			if (answer.getQuestion().getId() == QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.TrirAbsolute, answer);
				answerMap.put(OshaRateType.TrirNaics, answer);
				answerMap.put(OshaRateType.TrirWIA, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.LwcrAbsolute, answer);
				answerMap.put(OshaRateType.LwcrNaics, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Fatalities, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Hours, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_DAYS_AWAY_CASES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.DaysAwayCases, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_DAYS_AWAY_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.DaysAway, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_JOB_TRANSFER_CASES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.JobTransfersCases, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_JOB_TRANSFER_DAYS_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.JobTransferDays, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_OTHER_RECORDABLES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.OtherRecordables, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_FILE_UPLOAD_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.FileUpload, answer);
				fileUpload = answer;
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_DART_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Dart, answer);
				answerMap.put(OshaRateType.DartNaics, answer);
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_PICS_SEVERITY_RATE_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.SeverityRate, answer);
			}
			else if (answer.getQuestion().getId() == 2034) {
				answerMap.put(OshaRateType.EMR, answer);
			}
		}
	}

	@Override
	public List<AuditData> getQuestionsToVerify() {
		List<AuditData> questionsToVerify = new ArrayList<AuditData>();
		
		questionsToVerify.add(answerMap.get(OshaRateType.Hours));
		questionsToVerify.add(answerMap.get(OshaRateType.Fatalities));
		questionsToVerify.add(answerMap.get(OshaRateType.DaysAwayCases));
		questionsToVerify.add(answerMap.get(OshaRateType.DaysAway));
		questionsToVerify.add(answerMap.get(OshaRateType.JobTransfersCases));
		questionsToVerify.add(answerMap.get(OshaRateType.JobTransferDays));
		questionsToVerify.add(answerMap.get(OshaRateType.OtherRecordables));
		
		return questionsToVerify;
	}
}

