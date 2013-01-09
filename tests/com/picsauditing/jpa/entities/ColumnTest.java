package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.picsauditing.report.fields.SqlFunction;

public class ColumnTest {

	private Column column = new Column();

	@Test
	public void testGetFieldNameWithoutMethod() {
		column.setName("FacilityCount");
		column.setSqlFunction(SqlFunction.Count);
		column.setMethodToFieldName();
		
		assertEquals("FacilityCount__Count", column.getName());
		assertEquals("FacilityCount", column.getFieldNameWithoutMethod());
		assertEquals(SqlFunction.Count, column.getSqlFunction());
	}
}
