package com.picsauditing.rules;

import java.util.ArrayList;
import java.util.HashSet;

import com.picsauditing.rules.RulesOperator;

import junit.framework.TestCase;

public class BillingEngineTest extends TestCase {
	public final void testEngine() {
		try {
			ArrayList<RulesRowBean> rows = new ArrayList<RulesRowBean>();
			RulesRowBean row;
			
			row = new RulesRowBean();
			row.setOperator1(RulesOperator.IsTrue);
			row.setResult("1");
			rows.add(row);
			
			row = new RulesRowBean();
			row.setResult("2");
			rows.add(row);
			
			BillingEngine engine = new BillingEngine();
			engine.setUp(rows);
			HashSet<Integer> facilities = new HashSet<Integer>();
			facilities.add(123);
			
			int price;
			price = engine.calculate(true, true, facilities, facilities.size());
			assertEquals(1, price);

			price = engine.calculate(false, true, facilities, facilities.size());
			assertEquals(2, price);
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}
}
