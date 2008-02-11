package com.picsauditing.resultset;

import java.util.HashMap;
import junit.framework.TestCase;

public class ResultSetTest extends TestCase {
	private ResultSet rs = new ResultSet();

	public ResultSetTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Calculate Price based on the person's gender and age
		// GENDER AGE PRICE
		// ANY    >50 500
		// =M     >20 200
		// ANY    ANY 100
		
		rs = new ResultSet();
		rs.addColumn("gender", "String");
		rs.addColumn("age", "Integer");
		rs.setReturnType("Integer"); // Price
		
		ResultSetRow row;
		
		row = new ResultSetRow();
		row.addQuestion("gender", new ResultSetQuestion());
		row.addQuestion("age", new ResultSetQuestion(ResultSetOperator.GreaterThan, 50));
		row.setValue(500);
		rs.addRow(row);
		
		row = new ResultSetRow();
		row.addQuestion("gender", new ResultSetQuestion(ResultSetOperator.Equals, "M"));
		row.addQuestion("age", new ResultSetQuestion(ResultSetOperator.GreaterThan, 20));
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
