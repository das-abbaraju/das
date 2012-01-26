package com.picsauditing.report;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.QueryFunction;
import com.picsauditing.report.fields.SimpleReportColumn;
import com.picsauditing.report.fields.SimpleReportFilter;
import com.picsauditing.report.fields.SimpleReportSort;
import com.picsauditing.report.models.QueryAccount;
import com.picsauditing.report.models.QueryAccountContractor;
import com.picsauditing.search.SelectSQL;

public class SqlBuilderTest extends TestCase {
	private SqlBuilder builder;
	private SimpleReportDefinition definition = new SimpleReportDefinition();

	protected void setUp() throws Exception {
		builder = new SqlBuilder();
		builder.setDefinition(definition);
	}

	public void testAccounts() {
		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(0, sql.getFields().size());
		assertContains("FROM accounts AS a", sql);
		assertContains("ORDER BY a.nameIndex", sql);
	}

	public void testAccountColumns() {
		definition.getColumns().add(new SimpleReportColumn("accountID"));
		definition.getColumns().add(new SimpleReportColumn("accountName"));
		definition.getColumns().add(new SimpleReportColumn("accountStatus"));

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());

		assertContains("a.id AS `accountID`", sql);
	}

	public void testContractors() {
		builder.setBase(new QueryAccountContractor());
		SelectSQL sql = builder.getSql();

		assertEquals(0, sql.getFields().size());
		String expected = "JOIN contractor_info AS c ON a.id = c.id AND a.type = 'Contractor'";
		assertContains(expected, sql);
	}

	public void testContractorColumns() {
		definition.getColumns().add(new SimpleReportColumn("accountID"));
		definition.getColumns().add(new SimpleReportColumn("contractorName"));
		definition.getColumns().add(new SimpleReportColumn("contractorScore"));

		builder.setBase(new QueryAccountContractor());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
	}

	public void testLeftJoinUser() throws Exception {
		definition.getColumns().add(new SimpleReportColumn("accountID"));
		definition.getColumns().add(new SimpleReportColumn("accountName"));
		definition.getColumns().add(new SimpleReportColumn("accountContactName"));

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS accountContact ON accountContact.id = a.contactID", sql);
	}

	public void testFilters() {
		definition.getColumns().add(new SimpleReportColumn("accountName"));
		SimpleReportFilter filter = new SimpleReportFilter();
		filter.setColumn("accountName");
		filter.setOperator(QueryFilterOperator.BeginsWith);
		filter.setValue("Trevor's");
		definition.getFilters().add(filter);

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("WHERE ((a.nameIndex LIKE 'Trevor\'s%'))", sql);
	}

	public void testFiltersWithComplexColumn() {
		SimpleReportColumn column = new SimpleReportColumn("AccountCreationDateYear");
		column.setFunction(QueryFunction.Year);
		definition.getColumns().add(column);

		SimpleReportFilter filter = new SimpleReportFilter();
		filter.setColumn("AccountCreationDateYear");
		filter.setOperator(QueryFilterOperator.GreaterThan);
		filter.setValue("2010");

		definition.getFilters().add(filter);

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("(YEAR(a.creationDate) > '2010')", sql);
	}

	public void testGroupBy() {
		definition.getColumns().add(new SimpleReportColumn("accountStatus"));
		SimpleReportColumn column = new SimpleReportColumn("accountStatusCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("COUNT(a.status)", sql);
		assertContains("GROUP BY a.status", sql);
	}

	public void testSorts() {
		builder.setBase(new QueryAccount());
		
		SimpleReportSort sort = new SimpleReportSort("accountStatus");
		definition.getOrderBy().add(sort);
		SelectSQL sql = builder.getSql();
		assertContains("ORDER BY a.status", sql);
		
		definition.getColumns().add(new SimpleReportColumn("accountStatus"));
		sql = builder.getSql();
		assertContains("ORDER BY accountStatus", sql);
		
		sort.setAscending(false);
		sql = builder.getSql();
		assertContains("ORDER BY accountStatus DESC", sql);
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
