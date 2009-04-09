package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class FlagQuestionCriteriaTest extends TestCase {
	@Test
	public void testIsFlagged() {
		FlagQuestionCriteria criteria = new FlagQuestionCriteria();
		criteria.setChecked(YesNo.Yes);
		criteria.setAuditQuestion(new AuditQuestion());

		// Test for Money/Decimal QuestionType
		criteria.getAuditQuestion().setQuestionType("Money");
		criteria.setValue("10000");
		criteria.setComparison("<");
		assertTrue("100 is less than 10000", criteria.isFlagged("100"));
		assertFalse("1000000 is not less than 10000", criteria.isFlagged("1,000,000"));
		assertFalse("10000 is not less than 10000", criteria.isFlagged("10000"));
		criteria.setComparison(">");
		assertFalse("100 is less than 10000", criteria.isFlagged("100"));
		assertTrue("1000000 is not less than 10000", criteria.isFlagged("1,000,000"));
		assertFalse("10000 is not less than 10000", criteria.isFlagged("10000"));

		// Test for Check Box Question Type
		criteria.getAuditQuestion().setQuestionType("Check Box");
		criteria.setValue("X");
		assertFalse(criteria.isFlagged("X"));
		criteria.setComparison("=");
		assertTrue(criteria.isFlagged("X"));

		// Test for Date Question Type
		criteria.getAuditQuestion().setQuestionType("Date");
		criteria.setValue("2009-02-02");
		criteria.setComparison(">");
		assertFalse(criteria.isFlagged("2008-02-02"));
		assertTrue(criteria.isFlagged("2010-02-02"));
		criteria.setComparison("<");
		assertFalse(criteria.isFlagged("2010-02-02"));
		assertTrue(criteria.isFlagged("2008-02-02"));

		// Test for Yes/No or Manual Question Type
		criteria.getAuditQuestion().setQuestionType("Yes/No");
		criteria.setValue("Yes");
		criteria.setComparison(">");
		assertFalse(criteria.isFlagged("Yes"));
		criteria.setComparison("=");
		assertFalse(criteria.isFlagged("No"));
		assertTrue(criteria.isFlagged("Yes"));

		// Test for additinal Insured Question Type
		criteria.getAuditQuestion().setQuestionType("Additional Insured");
		criteria.setOperatorAccount(new OperatorAccount());
		criteria.getOperatorAccount().setId(3937);
		List<AccountName> aList = new ArrayList<AccountName>();
		AccountName accountName = new AccountName();
		accountName.setName("Roquette America, Inc.");
		aList.add(accountName);
		criteria.getOperatorAccount().setNames(aList);
		assertFalse(criteria.isFlagged("Square"));
		assertTrue(criteria.isFlagged("Roquette America, Inc."));
	}

}
