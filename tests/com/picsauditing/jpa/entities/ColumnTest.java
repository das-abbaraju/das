package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;

public class ColumnTest {

	private Column column;

	@Before
	public void setUp() {
		column = new Column();
	}

	@Test
	public void testGetWidth_WhenWidthIsNotSet_ThenColumnHasDefaultWidth() {
		int resultWidth = column.getWidth();

		assertEquals(Column.DEFAULT_WIDTH, resultWidth);
	}

	@Test
	public void testGetWidth_WhenFieldIsNull_ThenUseColumnWidth() {
		column.setField(null);

		int resultWidth = column.getWidth();

		assertEquals(Column.DEFAULT_WIDTH, resultWidth);
	}

	@Test
	public void testGetWidth_WhenColumnHasDefaultWidthAndFieldHasDefaultWidth_ThenUseColumnWidth() {
		Field field = new Field("Field Name");
		column.setField(field);

		int resultWidth = column.getWidth();

		assertEquals(Column.DEFAULT_WIDTH, resultWidth);
	}

	@Test
	public void testGetWidth_WhenColumnHasDefaultWidthAndFieldHasNonDefaultWidth_ThenUseFieldWidth() {
		Field field = new Field("Field Name");
		int fieldWidth = 3 * Column.DEFAULT_WIDTH;
		field.setWidth(fieldWidth);
		column.setField(field);

		int resultWidth = column.getWidth();

		assertEquals(fieldWidth, resultWidth);
	}

	@Test
	public void testGetWidth_WhenColumnHasNonDefaultWidthAndFieldHasDefaultWidth_ThenUseColumnWidth() {
		int columnWidth = 2 * Column.DEFAULT_WIDTH;
		column.setWidth(columnWidth);
		Field field = new Field("Field Name");
		column.setField(field);

		int resultWidth = column.getWidth();

		assertEquals(columnWidth, resultWidth);
	}

	@Test
	public void testGetWidth_WhenColumnHasNonDefaultWidthAndFieldHasNonDefaultWidth_ThenUseColumnWidth() {
		int columnWidth = 2 * Column.DEFAULT_WIDTH;
		column.setWidth(columnWidth);
		Field field = new Field("Field Name");
		int fieldWidth = 3 * Column.DEFAULT_WIDTH;
		field.setWidth(fieldWidth);
		column.setField(field);

		int resultWidth = column.getWidth();

		assertEquals(columnWidth, resultWidth);
	}

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
