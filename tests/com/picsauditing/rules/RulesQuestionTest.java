package com.picsauditing.rules;

import com.picsauditing.rules.ResultSetOperator;
import com.picsauditing.rules.ResultSetQuestion;

import junit.framework.TestCase;

public class ResultSetQuestionTest extends TestCase {

	public ResultSetQuestionTest(String name) {
		super(name);
	}

	public final void testEqualsAny() {
		ResultSetQuestion question;
		question = new ResultSetQuestion(ResultSetOperator.Any, "FOO");
		assertTrue(question.equals("BAR"));
	}
	public final void testEqualsEquals() {
		ResultSetQuestion question;
		question = new ResultSetQuestion(ResultSetOperator.Equals, "FOO");
		assertTrue(question.equals("FOO"));

		question = new ResultSetQuestion(ResultSetOperator.Equals, "FOO");
		assertFalse(question.equals("BAR"));

		question = new ResultSetQuestion(ResultSetOperator.Equals, 1);
		assertTrue(question.equals(1));
		
	}
	public final void testEqualsNotEquals() {
		ResultSetQuestion question;
		question = new ResultSetQuestion(ResultSetOperator.NotEquals, "FOO");
		assertTrue(question.equals("BAR"));
	}
}
