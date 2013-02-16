package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.fields.SqlFunction;

public class ReportElementTest {

	private ReportElementForTest reportElement;

	@Before
	public void setUp() throws Exception {
		reportElement = new ReportElementForTest();
	}

	@Test
	public void testSetMethodToFieldName_WhenSqlFunctionIsNull_ThenDoNothing() {
		String name = "name";
		reportElement.setName(name);
		reportElement.setSqlFunction(null);

		reportElement.setMethodToFieldName();

		assertEquals(name, reportElement.getName());
	}

	@Test
	public void testSetMethodToFieldName_WhenNameHasMethodSeparator_Then() {
		String name = "name";
		reportElement.setName(name);
		SqlFunction sqlFunction = SqlFunction.Average;
		reportElement.setSqlFunction(sqlFunction);

		reportElement.setMethodToFieldName();

		String expectedName = name + ReportElement.METHOD_SEPARATOR + sqlFunction.toString();
		assertEquals(expectedName, reportElement.getName());
	}

	private class ReportElementForTest extends ReportElement {
		// Empty on purpose
	}
}
