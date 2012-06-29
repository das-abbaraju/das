package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UkStatistics extends SafetyStatistics{

	private List<AuditData> questionsToVerify = new ArrayList<AuditData>();
	private int[] questionIdsToVerify = {7691, 8867, 8868, 8869, 8870, 8871, 8872, 9967, 9968, 12033};

	public UkStatistics(int year, List<AuditData> data) {
		super(year, OshaType.UK_HSE, data);
		
		Arrays.sort(questionIdsToVerify);
		
		answerMap = new HashMap<OshaRateType, AuditData>();
		for (AuditData answer: data) {
			if (answer.getQuestion().getId() == 9060) {
				answerMap.put(OshaRateType.IFR, answer);
			}
			if (answer.getQuestion().getId() == 9099) {
				answerMap.put(OshaRateType.Hours, answer);
			}
			if (answer.getQuestion().getId() == 8867) {
				answerMap.put(OshaRateType.Fatalities, answer);
			}
			if (answer.getQuestion().getId() == 9062) {
				answerMap.put(OshaRateType.DOFR, answer);
			}
			if (answer.getQuestion().getId() == 11689) {
				answerMap.put(OshaRateType.LTIFR, answer);
			}
			if (answer.getQuestion().getId() == 8873) {
				answerMap.put(OshaRateType.FileUpload, answer);
				fileUpload = answer;
			}
			if (Arrays.binarySearch(questionIdsToVerify, answer.getQuestion().getId()) > 0) {
				questionsToVerify.add(answer);
			}
			
		}	
	}

	@Override
	public String toString() {
		return "|UK Stats|";
	}

	@Override
	public List<AuditData> getQuestionsToVerify() {
		return questionsToVerify;
	}
}
