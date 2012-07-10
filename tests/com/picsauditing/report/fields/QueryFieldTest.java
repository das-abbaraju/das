package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class QueryFieldTest {

	@Test
	public void testUrlSingle() {
		QueryField field = new QueryField("contractorName", "a.name", FilterType.AccountName);
		field.setUrl("Test.action?id={accountID}");

		Set<String> dependentFields = field.getDependentFields();
		assertEquals(1, dependentFields.size());
		assertTrue(dependentFields.contains("accountID"));
	}
	
	@Test
	public void testUrlDouble() {
		QueryField field = new QueryField("contractorName", "a.name", FilterType.AccountName);
		field.setUrl("Test.action?id={accountID}&name={reportName}");

		Set<String> dependentFields = field.getDependentFields();
		assertEquals(2, dependentFields.size());
		assertTrue(dependentFields.contains("accountID"));
		assertTrue(dependentFields.contains("reportName"));
	}
}
