package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TypeFactoryTest {

	@Test
	public void testLookupColumnType_AccountID() throws Exception {
		DisplayType result = TypeFactory.lookupColumnType(FieldType.AccountID);
		assertEquals(DisplayType.RightAlign, result);
	}

	@Test
	public void testLookupColumnType_String() throws Exception {
		DisplayType result = TypeFactory.lookupColumnType(FieldType.String);
		assertEquals(DisplayType.LeftAlign, result);
	}

	@Test
	public void testLookupColumnType_StringCount() throws Exception {
		DisplayType result = TypeFactory.lookupColumnType(FieldType.String, SqlFunction.Count);
		assertEquals(DisplayType.RightAlign, result);
	}

	@Test
	public void testLookupColumnType_LowMedHigh() throws Exception {
		DisplayType result = TypeFactory.lookupColumnType(FieldType.LowMedHigh);
//		assertEquals(ColumnType.LeftAlign, result);
	}

}
