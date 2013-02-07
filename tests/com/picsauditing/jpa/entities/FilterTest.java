package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.Strings;

public class FilterTest {

	private Filter filter;

	@Before
	public void setUp() {
		filter = new Filter();
		filter.setField(new Field("Gets Clobbered"));
	}

	@Test
	public void setValue_WhenEmptyString_ThenLeavesArrayEmpty() {
		filter.setValue("");

		List<String> values = filter.getValues();

		assertEquals(0, values.size());
	}

	@Test
	public void setValue_WhenWhitespaceOnly_ThenLeavesArrayEmpty() {
		filter.setValue(" \t\n");

		List<String> values = filter.getValues();

		assertEquals(0, values.size());
	}

	@Test
	public void testInvalidFilter() throws ReportValidationException {
		assertEquals("true", filter.getSqlForFilter());
	}

	@Test
	public void testFilterEmpty() throws ReportValidationException {
		filter.setName("FieldName");
		filter.setField(new Field(filter.getName(), "fieldName", FieldType.String));
		filter.setOperator(QueryFilterOperator.Empty);

		assertEquals("fieldName IS NULL OR fieldName = ''", filter.getSqlForFilter());
	}

	@Test
	public void testFilterWithValue() throws ReportValidationException {
		filter.setName("FieldName");
		filter.setField(new Field(filter.getName(), "fieldName", FieldType.String));
		filter.setValue("Bob's");

		String sqlString = filter.getSqlForFilter();

		assertEquals("fieldName = 'Bob''s'", sqlString);
	}

	@Test
	public void testFilterFromJson_CommaSeparatedValues() throws ReportValidationException {
		filter.setName("AccountStatus");
		List<String> values = new ArrayList<String>();
		values.add("Active");
		values.add("Pending");
		filter.setValues(values);

		assertEquals("[Active, Pending]", filter.getValues().toString());
		assertEquals(2, filter.getValues().size());
	}

	@Test
	public void testGetSqlForFilter_WhenFilterFieldNameShouldntBeConverted_ThenItsNotConverted() throws ReportValidationException {
		filter.setName("Foo");
		String originalFilterValue = "something";
		filter.setValue(originalFilterValue);
		filter.setField(new Field("Get Clobbered"));

		String filterSql = filter.getSqlForFilter();

		assertTrue(filterSql.contains(originalFilterValue));
	}

	@Test
	public void testGetSqlForFilter_WhenFilterFieldNameIsAccountName_ThenConvertToIndexFormat() throws ReportValidationException {
		filter.setName("AccountName");
		String originalFilterValue = "two words";
		filter.setValue(originalFilterValue);
		filter.setField(new Field("Gets Clobbered"));

		String filterSql = filter.getSqlForFilter();

		assertFalse(filterSql.contains(originalFilterValue));
		String modifiedFilterValue = Strings.indexName(originalFilterValue);
		assertTrue(filterSql.contains(modifiedFilterValue));
	}

	@Test
	public void testIsValid_WhenValuesIsEmpty_ThenReturnFalse() {
		filter.setValues(new ArrayList<String>());

		boolean result = filter.isValid();

		assertFalse(result);
	}

	@Test
	public void testIsValid_WhenValuesContainsSingleEmptyString_ThenReturnFalse() {
		List<String> values = new ArrayList<String>();
		values.add("");
		filter.setValues(values);

		boolean result = filter.isValid();

		assertFalse(result);
	}
}