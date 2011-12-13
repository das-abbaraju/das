package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.List;


public class OshaStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR = 8978;
	public static final int QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR = 8977;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 8812;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 8810;
	
	public OshaStatistics(int year, List<AuditData> data) {
		super(year, OshaType.OSHA, data);
		answerMap = new HashMap<OshaRateType, String>();
		for (AuditData answer: data) {
			if (answer.getQuestion().getId() == QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.TrirAbsolute, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.LWCR, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Fatalities, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR) {
				answerMap.put(OshaRateType.Hours, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == 8813) {
				answerMap.put(OshaRateType.DaysAwayCases, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == 8814) {
				answerMap.put(OshaRateType.DaysAway, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == 8815) {
				answerMap.put(OshaRateType.JobTransfersCases, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == 8816) {
				answerMap.put(OshaRateType.JobTransferDays, answer.getAnswer());
			}
			else if (answer.getQuestion().getId() == 8817) {
				answerMap.put(OshaRateType.OtherRecordables, answer.getAnswer());
			}
		} 
	}

	@Override
	public String toString() {
		/*StringBuilder string = new StringBuilder();
		string.append("TRIR: ");
		string.append(getStats(OshaRateType.TrirAbsolute));
		string.append(", LWCR: ");
		string.append(getStats(OshaRateType.LWCR));
		string.append(", Fatalities: ");
		string.append(getStats(OshaRateType.Fatalities));
		string.append(", Hours Worked: ");
		string.append(getStats(OshaRateType.Hours));
		return string.toString();*/
		return "|Osha Stats|";
	}
}

