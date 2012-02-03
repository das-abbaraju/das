package com.picsauditing.search;

import junit.framework.TestCase;

public class SelectSQLTest extends TestCase {
	private SelectSQL builder;
	
	public void setUp() {
		builder = new SelectSQL();
		builder.setFromTable("my_table");
	}

	public void testFrom() {
		assertEquals("SELECT *\nFROM my_table", builder.toString());
    }
	
	public void testWhere() {
		builder.addWhere("field1 = 'foo' OR field2 = 'bar'");
		builder.addWhere("field3 LIKE '%foobar%'");
		assertEquals("SELECT *\nFROM my_table\nWHERE (field1 = 'foo' OR field2 = 'bar') \n AND (field3 LIKE '%foobar%') ", builder.toString());
    }

	public void testOrderBy() {
		builder.addOrderBy("field1");
		builder.addOrderBy("field2 DESC");
		assertEquals("SELECT *\nFROM my_table\nORDER BY field1, field2 DESC", builder.toString());
    }

	public void testJoin() {
		builder.addJoin("JOIN another_table USING (field1)");
		assertEquals("SELECT *\nFROM my_table\nJOIN another_table USING (field1)", builder.toString());
    }

	public void testGroupBy() {
		builder.addField("field1");
		builder.addGroupBy("field1");
		assertEquals("SELECT field1\nFROM my_table\nGROUP BY field1", builder.toString());
    }

	public void testHaving() {
		builder.addField("field1");
		builder.addField("count(*) as total");
		builder.addHaving("count(*) > 1");
		
		// No HAVING without a GROUP BY
		assertEquals("SELECT field1, count(*) as total\nFROM my_table", builder.toString());
		
		builder.addGroupBy("field1");
		assertEquals("SELECT field1, count(*) as total\nFROM my_table\nGROUP BY field1\nHAVING (count(*) > 1) ", builder.toString());
    }
}
