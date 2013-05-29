package com.picsauditing.jpa.entities;

import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KChase
 * Date: 4/9/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SouthAfricaStatistics extends SafetyStatistics {

	public static final int QUESTION_ID_DIIR_FOR_THE_GIVEN_YEAR = 16290;
	public static final int QUESTION_ID_SR_FOR_THE_GIVEN_YEAR = 16291;
	public static final int QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR = 16284;
	public static final int QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR = 16283 ;

	public SouthAfricaStatistics(int year, List<AuditData> data, boolean categoryApplies) {
		super(year, OshaType.SOUTH_AFRICA, data);
		answerMap = new HashMap<OshaRateType, AuditData>();

		answerMap.put(OshaRateType.DIIR, makeZeroAnswerData(QUESTION_ID_DIIR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.SR, makeZeroAnswerData(QUESTION_ID_SR_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.Fatalities, makeZeroAnswerData(QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR));
		answerMap.put(OshaRateType.Hours, makeZeroAnswerData(QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR));

		for (AuditData answer : data) {
			if (Strings.isEmpty(answer.getAnswer()))
				continue;
			if (answer.getAnswer().equals(QuestionFunction.MISSING_PARAMETER))
				continue;
			if (answer.getQuestion().getId() == QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.Fatalities, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_HOURS_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.Hours, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_DIIR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.DIIR, answer);
			} else if (answer.getQuestion().getId() == QUESTION_ID_SR_FOR_THE_GIVEN_YEAR && categoryApplies) {
				answerMap.put(OshaRateType.SR, answer);
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
