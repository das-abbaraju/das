package com.picsauditing.flagcalculator.entities;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UkStatistics extends SafetyStatistics{

	private List<AuditData> questionsToVerify = new ArrayList<>();
	private int[] questionIdsToVerify = {7691, 8867, 8868, 8869, 8870, 8871, 8872, 8991, 9099, 9966, 9967, 9968, 12033};

	public UkStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.UK_HSE, data);

		Arrays.sort(questionIdsToVerify);

		answerMap = new HashMap<OshaRateType, AuditData>();

		answerMap.put(OshaRateType.IFR, makeZeroAnswerData());
		answerMap.put(OshaRateType.Hours, makeZeroAnswerData());
		answerMap.put(OshaRateType.Fatalities, makeZeroAnswerData());
		answerMap.put(OshaRateType.DOFR, makeZeroAnswerData());
		answerMap.put(OshaRateType.LTIFR, makeZeroAnswerData());
        answerMap.put(OshaRateType.AIR, makeZeroAnswerData());

		for (AuditData answer: data) {
			if (StringUtils.isEmpty(answer.getAnswer()))
				continue;
			if (answer.getAnswer().equals(QuestionFunction.MISSING_PARAMETER))
				continue;
			if (answer.getQuestion().getId() == getOshaType().shaKeptQuestionId)
				shaKept = StringUtils.equals(answer.getAnswer(), "Yes");
            if (answer.getQuestion().getId() == 9060 && categoryApplies) {
                answerMap.put(OshaRateType.IFR, answer);
            }
            if (answer.getQuestion().getId() == 20236 && categoryApplies) {
                answerMap.put(OshaRateType.AIR, answer);
            }
			if (answer.getQuestion().getId() == 9099 && categoryApplies) {
				answerMap.put(OshaRateType.Hours, answer);
                hoursWorked = answer;
			}
			if (answer.getQuestion().getId() == 8867 && categoryApplies) {
				answerMap.put(OshaRateType.Fatalities, answer);
			}
			if (answer.getQuestion().getId() == 9062 && categoryApplies) {
				answerMap.put(OshaRateType.DOFR, answer);
			}
			if (answer.getQuestion().getId() == 11689 && categoryApplies) {
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

//	@Override
//	public String toString() {
//		return "|UK Stats|";
//	}
//
//	@Override
//	public List<AuditData> getQuestionsToVerify() {
//		return questionsToVerify;
//	}
}
