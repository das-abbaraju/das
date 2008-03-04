package com.picsauditing.search;

import junit.framework.TestCase;

public class SelectSQLTest extends TestCase {
	private SelectSQL builder = new SelectSQL();

	public void testFrom() {
		builder.setFromTable("test_from");
		String sql = builder.toString();
		assertTrue(sql.contains("FROM test_from"));
		assertEquals(23, sql.length());
    }
	
	public void testWhere() {
		builder.setFromTable("test_from");
		builder.addWhere("field1 = 'foo' OR field2 = 'bar'");
		builder.addWhere("field3 LIKE '%foobar%'");
		String sql = builder.toString();
		assertEquals(103, sql.length());
    }

	public void testOrderBy() {
		builder.setFromTable("test_from");
		builder.addOrderBy("field1");
		builder.addOrderBy("field2 DESC");
		String sql = builder.toString();
		assertEquals(52, sql.length());
    }

	public void testJoin() {
		builder.setFromTable("test_from");
		builder.addJoin("JOIN test_join USING (field1)");
		String sql = builder.toString();
		assertEquals(53, sql.length());
    }

	public void testGroupBy() {
		builder.setFromTable("test_from");
		builder.addField("field1");
		builder.addGroupBy("field1");
		String sql = builder.toString();
		assertEquals(44, sql.length());
    }

	public void testHaving() {
		builder.setFromTable("test_from");
		builder.addField("field1");
		builder.addField("count(*) as total");
		builder.setHavingClause("count(*) > 1");
		String sql = builder.toString();
		assert(!builder.toString().contains("HAVING"));
		
		builder.addGroupBy("field1");
		assert(builder.toString().contains("HAVING"));
    }
}
