package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.QueryFunction;
import com.picsauditing.report.fields.ReportColumn;
import com.picsauditing.report.fields.ReportFilter;
import com.picsauditing.report.fields.ReportSort;
import com.picsauditing.report.models.QueryAccount;
import com.picsauditing.report.models.QueryAccountContractor;
import com.picsauditing.search.SelectSQL;

public class SqlBuilderTest {
	private SqlBuilder builder;
	private ReportDefinition definition = new ReportDefinition();

	@Before
	public void setUp() throws Exception {
		builder = new SqlBuilder();
		builder.setDefinition(definition);
	}

	@Test
	public void testAccounts() {
		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(2, sql.getFields().size());
		assertContains("FROM accounts AS a", sql.toString());
		assertContains("ORDER BY a.name", sql.toString());
	}

	@Test
	public void testAccountColumns() {
		definition.getColumns().add(new ReportColumn("accountID"));
		definition.getColumns().add(new ReportColumn("accountName"));
		definition.getColumns().add(new ReportColumn("accountStatus"));

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());

		assertContains("a.id AS `accountID`", sql.toString());
	}

	@Test
	public void testContractors() {
		builder.setBase(new QueryAccountContractor());
		SelectSQL sql = builder.getSql();

		assertEquals(4, sql.getFields().size());
		String expected = "JOIN contractor_info AS c ON a.id = c.id AND a.type = 'Contractor'";
		assertContains(expected, sql.toString());
	}

	@Test
	public void testContractorColumns() {
		definition.getColumns().add(new ReportColumn("contractorName"));
		definition.getColumns().add(new ReportColumn("contractorScore"));

		builder.setBase(new QueryAccountContractor());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
	}

	@Test
	public void testLeftJoinUser() throws Exception {
		definition.getColumns().add(new ReportColumn("accountID"));
		definition.getColumns().add(new ReportColumn("accountName"));
		definition.getColumns().add(new ReportColumn("accountContactName"));

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS accountContact ON accountContact.id = a.contactID", sql.toString());
	}

	@Test
	public void testFilters() {
		definition.getColumns().add(new ReportColumn("accountName"));
		ReportFilter filter = new ReportFilter();
		filter.setColumn("accountName");
		filter.setOperator(QueryFilterOperator.BeginsWith);
		filter.setValue("Trevor's");
		definition.getFilters().add(filter);

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("WHERE ((a.nameIndex LIKE 'Trevor\'s%'))", sql.toString());
	}

	@Test
	public void testFiltersWithComplexColumn() {
		ReportColumn column = new ReportColumn("AccountCreationDateYear");
		column.setFunction(QueryFunction.Year);
		definition.getColumns().add(column);

		ReportFilter filter = new ReportFilter();
		filter.setColumn("AccountCreationDateYear");
		filter.setOperator(QueryFilterOperator.GreaterThan);
		filter.setValue("2010");

		definition.getFilters().add(filter);

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("(YEAR(a.creationDate) > '2010')", sql.toString());
	}

	@Test
	public void testGroupBy() {
		definition.getColumns().add(new ReportColumn("accountStatus"));
		ReportColumn column = new ReportColumn("accountStatusCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("COUNT(a.status)", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testHaving() {
		// {"filters":[{"column":"contractorName","operator":"BeginsWith","value":"Da"}]}
		definition.getColumns().add(new ReportColumn("accountStatus"));
		ReportColumn column = new ReportColumn("accountNameCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);

		{
			ReportFilter filter = new ReportFilter();
			filter.setColumn("accountNameCount");
			filter.setOperator(QueryFilterOperator.GreaterThan);
			filter.setValue("5");
			definition.getFilters().add(filter);
		}
		{
			ReportFilter filter = new ReportFilter();
			filter.setColumn("accountName");
			filter.setOperator(QueryFilterOperator.BeginsWith);
			filter.setValue("A");
			definition.getFilters().add(filter);
		}

		builder.setBase(new QueryAccount());
		SelectSQL sql = builder.getSql();

		assertContains("HAVING (COUNT(a.name) > '5')", sql.toString());
		assertContains("WHERE ((a.nameIndex LIKE 'A%'))", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testGroupByContractorName() {
		builder.setBase(new QueryAccountContractor());

		ReportColumn contractorNameCount = new ReportColumn("contractorNameCount");
		contractorNameCount.setFunction(QueryFunction.Count);
		definition.getColumns().add(contractorNameCount);
		
		SelectSQL sql = builder.getSql();
		System.out.println(sql.toString());
		assertEquals(1, sql.getFields().size());
		assertEquals("", sql.getOrderBy());
	}
	
	@Test
	public void testSorts() {
		builder.setBase(new QueryAccount());

		ReportSort sort = new ReportSort("accountStatus");
		definition.getOrderBy().add(sort);
		SelectSQL sql = builder.getSql();
		assertContains("ORDER BY a.status", sql.toString());

		definition.getColumns().add(new ReportColumn("accountStatus"));
		sql = builder.getSql();
		assertContains("ORDER BY accountStatus", sql.toString());

		sort.setAscending(false);
		sql = builder.getSql();
		assertContains("ORDER BY accountStatus DESC", sql.toString());
	}

}
