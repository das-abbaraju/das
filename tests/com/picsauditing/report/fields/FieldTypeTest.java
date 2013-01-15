package com.picsauditing.report.fields;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldTypeTest {

	@Test
	public void testGetSqlFunctions_SqlFunctionProfile_Boolean() {

		Set<FieldType> fieldTypes = FieldType.getAllBySqlFunctionProfile(SqlFunctionProfile.Boolean);
		assertEquals(4, fieldTypes.size());

		for (FieldType fieldType : fieldTypes) {
			System.out.println("Testing fieldType: " + fieldType);
			Set<SqlFunction> sqlFunctionsResult = fieldType.getSqlFunctions();

			assertEquals(4, sqlFunctionsResult.size());
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Count));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.CountDistinct));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Max));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Min));
		}
	}

	@Test
	public void testGetSqlFunctions_SqlFunctionProfile_String() {

		Set<FieldType> fieldTypes = FieldType.getAllBySqlFunctionProfile(SqlFunctionProfile.String);
		assertEquals(32, fieldTypes.size());

		for (FieldType fieldType : fieldTypes) {
			System.out.println("Testing fieldType: " + fieldType);
			Set<SqlFunction> sqlFunctionsResult = fieldType.getSqlFunctions();

			assertEquals(8, sqlFunctionsResult.size());
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Count));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.CountDistinct));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Max));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Min));

			assertTrue(sqlFunctionsResult.contains(SqlFunction.GroupConcat));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Length));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.LowerCase));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.UpperCase));
		}
	}

	@Test
	public void testGetSqlFunctions_SqlFunctionProfile_Date() {

		Set<FieldType> fieldTypes = FieldType.getAllBySqlFunctionProfile(SqlFunctionProfile.Date);
		assertEquals(2, fieldTypes.size());

		for (FieldType fieldType : fieldTypes) {
			System.out.println("Testing fieldType: " + fieldType);
			Set<SqlFunction> sqlFunctionsResult = fieldType.getSqlFunctions();

			if (fieldType == FieldType.Date) {
				assertEquals(12, sqlFunctionsResult.size());
			} else if (fieldType == FieldType.DateTime){
				assertEquals(13, sqlFunctionsResult.size());
				assertTrue(sqlFunctionsResult.contains(SqlFunction.Date));
			} else {
				assertEquals(14, sqlFunctionsResult.size());
				assertTrue(sqlFunctionsResult.contains(SqlFunction.Length));
				assertTrue(sqlFunctionsResult.contains(SqlFunction.Date));
			}
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Count));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.CountDistinct));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Max));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Min));

			assertTrue(sqlFunctionsResult.contains(SqlFunction.GroupConcat));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.LowerCase));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.UpperCase));

			assertTrue(sqlFunctionsResult.contains(SqlFunction.Month));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Year));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.YearMonth));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.WeekDay));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Hour));
		}
	}

	@Test
	public void testGetSqlFunctions_SqlFunctionProfile_Number() {

		Set<FieldType> fieldTypes = FieldType.getAllBySqlFunctionProfile(SqlFunctionProfile.Number);
		assertEquals(3, fieldTypes.size());

		for (FieldType fieldType : fieldTypes) {
			System.out.println("Testing fieldType: " + fieldType);
			Set<SqlFunction> sqlFunctionsResult = fieldType.getSqlFunctions();

			if (fieldType == FieldType.Integer) {
				assertEquals(7, sqlFunctionsResult.size());
			} else {
				assertEquals(8, sqlFunctionsResult.size());
				assertTrue(sqlFunctionsResult.contains(SqlFunction.Round));
			}
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Count));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.CountDistinct));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Max));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Min));

			assertTrue(sqlFunctionsResult.contains(SqlFunction.Average));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.Sum));
			assertTrue(sqlFunctionsResult.contains(SqlFunction.StdDev));
		}
	}

}
