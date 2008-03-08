package com.picsauditing.rules;

import java.util.HashMap;

import com.picsauditing.rules.RulesOperator;
import com.picsauditing.rules.RulesQuestion;
import com.picsauditing.rules.RulesRow;

import junit.framework.TestCase;

public class RulesRowTest extends TestCase {
	private RulesRow row = new RulesRow(); 

	public RulesRowTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		row.addQuestion("name", new RulesQuestion(RulesOperator.Equals, "FOO"));
		row.addQuestion("age", new RulesQuestion(RulesOperator.Equals, 1));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		row = null;
	}
	
	public final void testEqualsBoth() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", "FOO");
		parameters.put("age", 1);
		assertTrue(row.equals(parameters));
	}
	public final void testNotName() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", "BAR");
		parameters.put("age", 1);
		assertFalse(row.equals(parameters));
	}
	public final void testNotAge() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", "FOO");
		parameters.put("age", 99);
		assertFalse(row.equals(parameters));
	}
	public final void testNull() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		assertFalse(row.equals(parameters));
	}
}
