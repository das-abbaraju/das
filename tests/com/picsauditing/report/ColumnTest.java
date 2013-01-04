package com.picsauditing.report;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.picsauditing.report.fields.SqlFunction;

public class ColumnTest {

	private Column column = new Column();

	@Test
	public void testMethod() {
		column.setId("AccountName__Count");
		column.setMethod(SqlFunction.UpperCase);
		assertEquals(SqlFunction.UpperCase, column.getMethod());
	}

	@Test
	public void testBadMethodName() throws Exception {
		try {
			column.setId("AccountName__BadMethodThatDoesNotExist");
		} catch (Exception weWantToThrowException) {
			return;
		}
		throw new Exception("There is no QueryMethod called BadMethodThatDoesNotExist");
	}

	@Test
	public void testGetFieldNameWithoutMethod() {
		column.setId("FacilityCount");
		column.setMethod(SqlFunction.Count);
		assertEquals("FacilityCount__Count", column.getId());
		assertEquals("FacilityCount", column.getFieldNameWithoutMethod());
		assertEquals(SqlFunction.Count, column.getMethod());
	}
}
