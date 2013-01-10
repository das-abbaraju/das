package com.picsauditing.report.fields;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypeFactoryTest {

	@Test
	public void testLookupColumnType_AccountID() throws Exception {
		ColumnType result = TypeFactory.lookupColumnType(FieldType.AccountID);
		assertEquals(ColumnType.RightAlign, result);
	}

	@Test
	public void testLookupColumnType_String() throws Exception {
		ColumnType result = TypeFactory.lookupColumnType(FieldType.String);
		assertEquals(ColumnType.LeftAlign, result);
	}

	@Test
	public void testLookupColumnType_StringCount() throws Exception {
		ColumnType result = TypeFactory.lookupColumnType(FieldType.String, SqlFunction.Count);
		assertEquals(ColumnType.RightAlign, result);
	}

	@Test
	public void testLookupColumnType_LowMedHigh() throws Exception {
		ColumnType result = TypeFactory.lookupColumnType(FieldType.LowMedHigh);
//		assertEquals(ColumnType.LeftAlign, result);
	}

}
