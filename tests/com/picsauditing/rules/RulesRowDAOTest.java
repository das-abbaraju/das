package com.picsauditing.rules;

import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.PICS.DefaultDatabase;

public class RulesRowDAOTest extends TestCase {
	public RulesRowDAOTest(String name) {
		super(name);
	}

	public final void testGetPricing() {
		
	}
	
//	public final void testGetPricing() {
//		try {
//			RulesRowDAO dao = new RulesRowDAO();
//			dao.setConn(DefaultDatabase.getConnection());
//			List<RulesRowBean> rows = dao.getRowsByTable("pricing");
//
//			 assertTrue(rows.size() > 5);
//		} catch (Exception e) {
//			fail("Exception thrown: " + e.getMessage());
//		}
//	}
//
//	public final void testGetJUnit() {
//		try {
//			RulesRowDAO dao = new RulesRowDAO();
//			dao.setConn(DefaultDatabase.getConnection());
//			List<RulesRowBean> rows = dao.getRowsByTable("junit");
//
//			 assertEquals(1, rows.size());
//
//			RulesRowBean row = rows.get(0);
//			 assertEquals("Test Case", row.getNotes());
//			 assertEquals("99", row.getResult());
//			 assertEquals(RulesOperator.Any, row.getOperator1());
//			 assertEquals(RulesOperator.IsTrue, row.getOperator2());
//			 assertEquals(RulesOperator.IsFalse, row.getOperator3());
//			 assertEquals(RulesOperator.Equals, row.getOperator4());
//			 assertEquals(RulesOperator.GreaterThan, row.getOperator5());
//			 assertEquals("value", row.getValue4());
//			 assertEquals("0", row.getValue5());
//		} catch (Exception e) {
//			fail("Exception thrown: " + e.getMessage());
//		}
//	}
}
