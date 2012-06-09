package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.QueryFunction;
import com.picsauditing.report.models.AccountModel;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.search.SelectSQL;

public class SqlBuilderTest {
	private SqlBuilder builder;
	private Definition definition = new Definition();

	@Before
	public void setUp() throws Exception {
		builder = new SqlBuilder();
		builder.setDefinition(definition);
	}

	@Ignore("Not ready to run yet.")
	@Test
	public void testAccounts() {
		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertEquals(2, sql.getFields().size());
		assertContains("FROM accounts AS a", sql.toString());
		assertContains("ORDER BY a.name", sql.toString());
	}

	@Test
	public void testAccountColumns() {
		definition.getColumns().add(new Column("accountID"));
		definition.getColumns().add(new Column("accountName"));
		definition.getColumns().add(new Column("accountStatus"));

		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertEquals(3, sql.getFields().size());

		assertContains("a.id AS `accountID`", sql.toString());
		assertContains("a.name AS `accountName`", sql.toString());
		assertContains("a.status AS `accountStatus`", sql.toString());
	}

	@Ignore("Not ready to run yet.")
	@Test
	public void testContractors() {
		builder.setBase(new AccountContractorModel());
		SelectSQL sql = builder.initializeSql();

		assertEquals(4, sql.getFields().size());
		String expected = "JOIN contractor_info AS c ON a.id = c.id AND a.type = 'Contractor'";
		assertContains(expected, sql.toString());
	}

	@Test
	public void testContractorColumns() {
		definition.getColumns().add(new Column("contractorName"));
		definition.getColumns().add(new Column("contractorScore"));

		builder.setBase(new AccountContractorModel());
		SelectSQL sql = builder.initializeSql();

		assertEquals(3, sql.getFields().size());
	}

	@Test
	public void testLeftJoinUser() throws Exception {
		definition.getColumns().add(new Column("accountID"));
		definition.getColumns().add(new Column("accountName"));
		definition.getColumns().add(new Column("accountContactName"));

		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS accountContact ON accountContact.id = a.contactID", sql.toString());
	}

	@Test
	public void testFilters() {
		definition.getColumns().add(new Column("accountName"));
		Filter filter = new Filter();
		filter.setFieldName("accountName");
		filter.setOperator(QueryFilterOperator.BeginsWith);
		filter.setValue("Trevor's");
		definition.getFilters().add(filter);

		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertContains("WHERE ((a.nameIndex LIKE 'Trevor\'s%'))", sql.toString());
	}

	@Test
	public void testFiltersWithComplexColumn() {
		Column column = new Column("AccountCreationDateYear");
		column.setFunction(QueryFunction.Year);
		definition.getColumns().add(column);

		Filter filter = new Filter();
		filter.setFieldName("AccountCreationDateYear");
		filter.setOperator(QueryFilterOperator.GreaterThan);
		filter.setValue("2010");

		definition.getFilters().add(filter);

		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertContains("(YEAR(a.creationDate) > '2010')", sql.toString());
	}

	@Test
	public void testGroupBy() {
		definition.getColumns().add(new Column("accountStatus"));
		Column column = new Column("accountStatusCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);

		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertContains("COUNT(a.status)", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testHaving() {
		// {"filters":[{"column":"contractorName","operator":"BeginsWith","value":"Da"}]}
		definition.getColumns().add(new Column("accountStatus"));
		Column column = new Column("accountNameCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);

		{
			Filter filter = new Filter();
			filter.setFieldName("accountNameCount");
			filter.setOperator(QueryFilterOperator.GreaterThan);
			filter.setValue("5");
			definition.getFilters().add(filter);
		}
		{
			Filter filter = new Filter();
			filter.setFieldName("accountName");
			filter.setOperator(QueryFilterOperator.BeginsWith);
			filter.setValue("A");
			definition.getFilters().add(filter);
		}

		builder.setBase(new AccountModel());
		SelectSQL sql = builder.initializeSql();

		assertContains("HAVING (COUNT(a.name) > '5')", sql.toString());
		assertContains("WHERE ((a.nameIndex LIKE 'A%'))", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testGroupByContractorName() {
		builder.setBase(new AccountContractorModel());

		Column contractorNameCount = new Column("contractorNameCount");
		contractorNameCount.setFunction(QueryFunction.Count);
		definition.getColumns().add(contractorNameCount);
		
		SelectSQL sql = builder.initializeSql();
		System.out.println(sql.toString());
		assertEquals(1, sql.getFields().size());
		assertEquals("", sql.getOrderBy());
	}
	
	@Test
	public void testSorts() {
		builder.setBase(new AccountModel());

		Sort sort = new Sort("accountStatus");
		definition.getSorts().add(sort);
		SelectSQL sql = builder.initializeSql();
		assertContains("ORDER BY a.status", sql.toString());

		definition.getColumns().add(new Column("accountStatus"));
		sql = builder.initializeSql();
		assertContains("ORDER BY accountStatus", sql.toString());

		sort.setAscending(false);
		sql = builder.initializeSql();
		assertContains("ORDER BY accountStatus DESC", sql.toString());
	}

}
