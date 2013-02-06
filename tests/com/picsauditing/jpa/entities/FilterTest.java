package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
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
		filter.addValue("Bob's");

		assertEquals("fieldName = 'Bob''s'", filter.getSqlForFilter());
	}

	@Test
	public void testFilterFromJson_CommaSeparatedValues() throws ReportValidationException {
		filter.setName("AccountStatus");
		filter.addValue("Active");
		filter.addValue("Pending");

		assertEquals("[Active, Pending]", filter.getValues().toString());
		assertEquals(2, filter.getValues().size());
	}

	@Test
	public void testGetSqlForFilter_WhenFilterFieldNameShouldntBeConverted_ThenItsNotConverted() throws ReportValidationException {
		filter.setName("Foo");
		String originalFilterValue = "something";
		filter.addValue(originalFilterValue);
		filter.setField(new Field("Get Clobbered"));

		String filterSql = filter.getSqlForFilter();

		assertTrue(filterSql.contains(originalFilterValue));
	}

	@Test
	public void testGetSqlForFilter_WhenFilterFieldNameIsAccountName_ThenConvertToIndexFormat() throws ReportValidationException {
		filter.setName("AccountName");
		String originalFilterValue = "two words";
		filter.addValue(originalFilterValue);
		filter.setField(new Field("Gets Clobbered"));

		String filterSql = filter.getSqlForFilter();

		assertFalse(filterSql.contains(originalFilterValue));
		String modifiedFilterValue = Strings.indexName(originalFilterValue);
		assertTrue(filterSql.contains(modifiedFilterValue));
	}
}