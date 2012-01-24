package com.picsauditing.report;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import com.picsauditing.report.models.QueryAccount;
import com.picsauditing.report.models.QueryAccountContractor;
import com.picsauditing.search.SelectSQL;

public class SqlBuilderTest extends TestCase {
	private SqlBuilder builder;

	protected void setUp() throws Exception {
		builder = new SqlBuilder();
	}

	public void testAccounts() {
		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertTrue(sql.getFields().size() > 10);
		assertContains("FROM accounts AS a", sql);
	}

	public void testAccountColumns() {
		String json = "{\"columns\" : [ \"accountID\", \"accountName\", \"accountStatus\", \"oldColumnName\" ]}";
		builder.setDefinition(new SimpleReportDefinition(json));

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
		
		assertContains("a.id AS `accountID`", sql);
	}

	public void testContractors() {
		builder.setBase(new QueryAccountContractor());
		SelectSQL sql = builder.getSql();

		assertTrue(sql.getFields().size() > 25);
		String expected = "JOIN contractor_info AS c ON a.id = c.id AND a.type = 'Contractor'";
		assertContains(expected, sql);
	}

	public void testContractorColumns() {
		String json = "{\"columns\" : [ \"accountID\", \"contractorName\", \"contractorScore\" ]}";
		builder.setDefinition(new SimpleReportDefinition(json));

		builder.setBase(new QueryAccountContractor());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
	}

	public void testLeftJoinUser() throws Exception {
		String json = "{\"columns\" : [ \"accountID\", \"accountName\", \"accountContactName\" ]}";
		builder.setDefinition(new SimpleReportDefinition(json));

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS accountContact ON accountContact.id = a.contactID", sql);
	}

	public void testFilters() {
		String json = "{\"columns\" : [ \"accountName\" ], \"filters\" : [ {\"field\": \"accountName\", \"operator\": \"BeginsWith\", \"value\": \"Trevor\"} ]}";
		builder.setDefinition(new SimpleReportDefinition(json));
		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("WHERE ((a.nameIndex LIKE 'Trevor%'))", sql);
	}

	static private void assertContains(String pattern, SelectSQL sql) {
		// sql.getFields().clear();
		String sqlWithoutWhitespace = simplifyWhitespace(sql);
		// sqlWithoutWhitespace = sqlWithoutWhitespace.replace("SELECT * ", "");
		if (sqlWithoutWhitespace.contains(pattern))
			return;
		throw new ComparisonFailure("Missing sql", pattern, sql.toString());
	}

	static private String simplifyWhitespace(SelectSQL sql) {
		// Consider moving this to SelectSQL
		String string = sql.toString();
		string = string.replace("\n", " ");
		string = string.replace("  ", " ");
		return string;
	}
}
