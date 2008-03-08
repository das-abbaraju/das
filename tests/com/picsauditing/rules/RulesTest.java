package com.picsauditing.rules;

import java.util.HashMap;

import com.picsauditing.rules.RulesSet;
import com.picsauditing.rules.RulesOperator;
import com.picsauditing.rules.RulesQuestion;
import com.picsauditing.rules.RulesRow;

import junit.framework.TestCase;

public class RulesTest extends TestCase {
	private RulesSet rs = new RulesSet();

	public RulesTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Calculate Price based on the person's gender and age
		// GENDER AGE PRICE
		// ANY    >50 500
		// =M     >20 200
		// ANY    ANY 100
		
		rs = new RulesSet();
		rs.addColumn("gender", "String");
		rs.addColumn("age", "Integer");
		rs.setReturnType("Integer"); // Price
		
		RulesRow row;
		
		row = new RulesRow();
		row.addQuestion("gender", new RulesQuestion());
		row.addQuestion("age", new RulesQuestion(RulesOperator.GreaterThan, 50));
		row.setValue(500);
		rs.addRow(row);
		
		row = new RulesRow();
		row.addQuestion("gender", new RulesQuestion(RulesOperator.Equals, "M"));
		row.addQuestion("age", new RulesQuestion(RulesOperator.GreaterThan, 20));
		row.setValue(200);
		rs.addRow(row);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		rs = null;
	}

	public final void testEvaluate() {
		try {
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("gender", "M");
			params.put("age", 25);
			int value = (Integer)rs.evaluate(params);
			assertEquals(value, 200);
			
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}
}
