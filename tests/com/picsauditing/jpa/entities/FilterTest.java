package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;

public class FilterTest {
	private Filter filter = new Filter();

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
		filter.getValues().add("Trevor's");

		assertEquals("fieldName = 'Trevor''s'", filter.getSqlForFilter());
	}

	@Test
	public void testFilterFromJson__CommaSeparatedValues() throws ReportValidationException {
		filter.setName("AccountStatus");
		filter.getValues().add("Active");
		filter.getValues().add("Pending");

		assertEquals("[Active, Pending]", filter.getValues().toString());
		assertEquals(2, filter.getValues().size());
	}
}