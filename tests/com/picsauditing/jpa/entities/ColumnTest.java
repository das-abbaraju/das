package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.SqlFunction;

public class ColumnTest {

	private Column column = new Column();

	@Test
	public void testMethod() {
		column.setName("AccountName__Count");
		column.setSqlFunction(SqlFunction.UpperCase);
		assertEquals(SqlFunction.UpperCase, column.getSqlFunction());
	}

	@Test
	public void testBadMethodName() throws Exception {
		try {
			column.setName("AccountName__BadMethodThatDoesNotExist");
		} catch (Exception weWantToThrowException) {
			return;
		}
		throw new Exception("There is no QueryMethod called BadMethodThatDoesNotExist");
	}

	@Test
	public void testGetFieldNameWithoutMethod() {
		column.setName("FacilityCount");
		column.setSqlFunction(SqlFunction.Count);
		assertEquals("FacilityCount__Count", column.getName());
		assertEquals("FacilityCount", column.getFieldNameWithoutMethod());
		assertEquals(SqlFunction.Count, column.getSqlFunction());
	}
}
