package com.picsauditing.rules;

import com.picsauditing.rules.RulesOperator;
import com.picsauditing.rules.RulesQuestion;

import junit.framework.TestCase;

public class RulesQuestionTest extends TestCase {

	public RulesQuestionTest(String name) {
		super(name);
	}

	public final void testEqualsAny() {
		RulesQuestion question;
		question = new RulesQuestion(RulesOperator.Any, "FOO");
		assertTrue(question.equals("BAR"));
	}
	public final void testEqualsEquals() {
		RulesQuestion question;
		question = new RulesQuestion(RulesOperator.Equals, "FOO");
		assertTrue(question.equals("FOO"));

		question = new RulesQuestion(RulesOperator.Equals, "FOO");
		assertFalse(question.equals("BAR"));

		question = new RulesQuestion(RulesOperator.Equals, 1);
		assertTrue(question.equals(1));
		
	}
	public final void testEqualsNotEquals() {
		RulesQuestion question;
		question = new RulesQuestion(RulesOperator.NotEquals, "FOO");
		assertTrue(question.equals("BAR"));
	}
}
