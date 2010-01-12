package com.picsauditing.util;

import junit.framework.TestCase;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

public class AnswerMapTest extends TestCase {

	public void testGeneral() {
		AnswerMap answers = new AnswerMap();

		AuditData data = new AuditData();
		data.setAnswer("John Doe");
		data.setQuestion(new AuditQuestion());
		data.getQuestion().setId(101);
		data.getQuestion().setDefaultQuestion("What is your name?");

		answers.add(data);

		assertEquals("John Doe", answers.get(101).getAnswer());
	}
}
