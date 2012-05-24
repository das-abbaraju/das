package com.picsauditing.search;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class SelectSQLTest {
	private SelectSQL builder;
	
	@Before
	public void setUp() {
		builder = new SelectSQL();
		builder.setFromTable("my_table");
	}

	@Test
	public void testEmptyTableName() {
		builder.setFromTable(null);
		builder.addField("now() as current_time");
		assertEquals("SELECT now() as current_time", builder.toString());
	}
	
	@Test
	public void testFrom() {
		assertEquals("SELECT *\nFROM my_table", builder.toString());
    }
	
	@Test
	public void testWhere() {
		builder.addWhere("field1 = 'foo' OR field2 = 'bar'");
		builder.addWhere("field3 LIKE '%foobar%'");
		assertEquals("SELECT *\nFROM my_table\nWHERE (field1 = 'foo' OR field2 = 'bar') \n AND (field3 LIKE '%foobar%') ", builder.toString());
    }

	@Test
	public void testOrderBy() {
		builder.addOrderBy("field1");
		builder.addOrderBy("field2 DESC");
		assertEquals("SELECT *\nFROM my_table\nORDER BY field1, field2 DESC", builder.toString());
    }

	@Test
	public void testJoin() {
		builder.addJoin("JOIN another_table USING (field1)");
		assertEquals("SELECT *\nFROM my_table\nJOIN another_table USING (field1)", builder.toString());
    }

	@Test
	public void testGroupBy() {
		builder.addField("field1");
		builder.addGroupBy("field1");
		assertEquals("SELECT field1\nFROM my_table\nGROUP BY field1", builder.toString());
    }

	@Test
	public void testHaving() {
		builder.addField("field1");
		builder.addField("count(*) as total");
		builder.addHaving("count(*) > 1");
		builder.addGroupBy("field1");
		assertEquals("SELECT field1, count(*) as total\nFROM my_table\nGROUP BY field1\nHAVING (count(*) > 1) ", builder.toString());
    }
	
	@Test
	public void testHaving_WithoutGroupBy() {
		builder.addField("field1");
		builder.addField("count(*) as total");
		builder.addHaving("count(*) > 1");
		assertEquals("SELECT field1, count(*) as total\nFROM my_table", builder.toString());
    }
}
