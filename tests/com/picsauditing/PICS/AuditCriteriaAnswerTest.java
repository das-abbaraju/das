package com.picsauditing.PICS;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.MultiYearScope;

public class AuditCriteriaAnswerTest extends TestCase {

	private AuditCriteriaAnswer newAca() {
		// Initialize answer, question, redFlag, amberFlag
		AuditData answer = EntityFactory.makeAuditData("1.5");

		Map<FlagColor, FlagQuestionCriteria> criteriaMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
		FlagQuestionCriteria redFlag = new FlagQuestionCriteria();
		FlagQuestionCriteria amberFlag = new FlagQuestionCriteria();
		redFlag.setFlagColor(FlagColor.Red);
		redFlag.setAuditQuestion(answer.getQuestion());
		amberFlag.setFlagColor(FlagColor.Amber);
		amberFlag.setAuditQuestion(answer.getQuestion());

		criteriaMap.put(FlagColor.Amber, amberFlag);
		criteriaMap.put(FlagColor.Red, redFlag);

		redFlag.setMultiYearScope(MultiYearScope.AllThreeYears);
		redFlag.setValidationRequired(false);
		redFlag.setMultiYearScope(null);
		redFlag.setComparison(">");
		redFlag.setValue("1");

		return new AuditCriteriaAnswer(answer, criteriaMap);
	}
	
	

	public void testBasicRedFlag() {
		AuditCriteriaAnswer aca = newAca();
		aca.getAnswer().getQuestion().setQuestionType("Decimal Number");
		
		aca.getCriteria().remove(FlagColor.Amber);
		FlagQuestionCriteria criteria = aca.getCriteria().get(FlagColor.Red);
		criteria.setComparison(">");
		criteria.setValue("1");

		aca.getAnswer().setAnswer("1.5");
		assertEquals(FlagColor.Red, aca.getResultColor(true));

		aca.getAnswer().setAnswer("1");
		assertEquals(FlagColor.Green, aca.getResultColor(true));
		
		aca.getAnswer().setAnswer("0.9");
		assertEquals(FlagColor.Green, aca.getResultColor(true));

	}

	public void testBasicRedAmberFlag() {
		AuditCriteriaAnswer aca = newAca();
		aca.getAnswer().getQuestion().setQuestionType("Decimal Number");
		
		FlagQuestionCriteria red = aca.getCriteria().get(FlagColor.Red);
		red.setComparison(">");
		red.setValue("1.5");

		FlagQuestionCriteria amber = aca.getCriteria().get(FlagColor.Amber);
		amber.setComparison(">");
		amber.setValue("1");

		aca.getAnswer().setAnswer("2");
		assertEquals(FlagColor.Red, aca.getResultColor(true));

		aca.getAnswer().setAnswer("1.2");
		assertEquals(FlagColor.Amber, aca.getResultColor(true));

		aca.getAnswer().setAnswer("0.9");
		assertEquals(FlagColor.Green, aca.getResultColor(true));
	}

}
