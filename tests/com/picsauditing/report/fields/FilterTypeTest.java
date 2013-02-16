package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilterTypeTest {

	@Test
	public void testDefaultOperators() {
		assertEquals(QueryFilterOperator.Equals, FilterType.AccountID.defaultOperator);
		assertEquals(QueryFilterOperator.In, FilterType.Autocomplete.defaultOperator);
		assertEquals(QueryFilterOperator.Equals, FilterType.Boolean.defaultOperator);
		assertEquals(QueryFilterOperator.LessThan, FilterType.Date.defaultOperator);
		assertEquals(QueryFilterOperator.In, FilterType.Multiselect.defaultOperator);
		assertEquals(QueryFilterOperator.Equals, FilterType.Number.defaultOperator);
		assertEquals(QueryFilterOperator.Contains, FilterType.String.defaultOperator);
		assertEquals(QueryFilterOperator.Equals, FilterType.UserID.defaultOperator);
	}
}
