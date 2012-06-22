package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;

public class AuditRuleTest extends PicsTest{
	
	AuditRule auditRule;
	AuditData data;
	ContractorAccount contractor;
	ContractorAudit audit;

	@Before
	public void setUp() throws Exception {
		auditRule = new AuditRule();
		
		contractor = EntityFactory.makeContractor();
		audit = EntityFactory.makeContractorAudit(AuditType.PQF, contractor);
		
		data = EntityFactory.makeAuditData("1");
		data.getQuestion().setEffectiveDate(new Date(100, 1, 1));
		data.getQuestion().setExpirationDate(new Date(8000, 1, 1));
		data.getQuestion().setQuestionType("Calculation");
		data.setAudit(audit);
		
		PicsTestUtil.forceSetPrivateField(auditRule, "questionAnswer", "1");
		PicsTestUtil.forceSetPrivateField(auditRule, "question", data.getQuestion());
	}

	@Test
	public void testIsMatchingAnswer_Number_Comparisons() {		
		auditRule.setQuestionComparator(QuestionComparator.LessThan);
		assertFalse(auditRule.isMatchingAnswer(data));
		
		auditRule.setQuestionComparator(QuestionComparator.LessThanEqual);
		assertTrue(auditRule.isMatchingAnswer(data));
		
		auditRule.setQuestionComparator(QuestionComparator.Equals);
		assertTrue(auditRule.isMatchingAnswer(data));
		
		auditRule.setQuestionComparator(QuestionComparator.NotEquals);
		assertFalse(auditRule.isMatchingAnswer(data));
		
		auditRule.setQuestionComparator(QuestionComparator.GreaterThan);
		assertFalse(auditRule.isMatchingAnswer(data));
		
		auditRule.setQuestionComparator(QuestionComparator.GreaterThanEqual);
		assertTrue(auditRule.isMatchingAnswer(data));
	}

}
