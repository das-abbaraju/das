package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.report.models.AccountModel;
import com.picsauditing.search.SelectSQL;

public class SqlBuilderTest {

	private SqlBuilder builder;
	private Permissions permissions;
	private Definition definition;
	private SelectSQL sql;

	@Before
	public void setUp() throws Exception {
		User user = new User(123);
		user.setAccount(new Account());
		user.getAccount().setId(1100);

		permissions = EntityFactory.makePermission(user);
		builder = new SqlBuilder();
		definition = new Definition("");
	}

	@Test
	public void testFromTable() throws Exception {
		initializeSql();

		assertEquals(0, sql.getFields().size());
		assertContains("FROM accounts AS Account", sql.toString());
	}

	@Test
	public void testMultipleColumns() throws Exception {
		addColumn("AccountID");
		addColumn("AccountName");
		addColumn("AccountStatus");

		initializeSql();

		assertEquals(3, sql.getFields().size());

		assertContains("Account.id AS `AccountID`", sql.toString());
		assertContains(" AS `AccountName`", sql.toString());
		assertContains("Account.status AS `AccountStatus`", sql.toString());
	}

	@Test
	public void testJoins() throws Exception {
		sql = builder.initializeSql(new AccountContractorModel(permissions), definition, permissions);

		String expected = "FROM contractor_info AS Contractor "
				+ "JOIN accounts AS Account ON Contractor.id = Account.id AND Account.type = 'Contractor'";
		assertContains(expected, sql.toString());
	}

	@Test
	public void testLeftJoinUser() throws Exception {
		addColumn("AccountID");
		addColumn("AccountName");
		addColumn("AccountContactName");

		initializeSql();

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS AccountContact ON Account.contactID = AccountContact.id", sql.toString());
	}

	@Test
	public void testFilters() throws Exception {
		Column column = addColumn("AccountName");
		addFilter(column.getFieldName(), QueryFilterOperator.BeginsWith, "Trevor's");

		initializeSql();

		assertContains("WHERE ((Account.nameIndex LIKE 'Trevor\\'s%'))", sql.toString());
		assertAllFiltersHaveFields();
	}

	@Test
	public void testFiltersWithComplexColumn() throws Exception {
		Column column = addColumn("AccountCreationDate__Year");

		addFilter(column.getFieldName(), QueryFilterOperator.GreaterThan, "2010");

		initializeSql();

		assertContains("(YEAR(Account.creationDate) > 2010)", sql.toString());
		assertAllFiltersHaveFields();
	}

	@Test
	public void testFilterMyAccount() throws Exception {
		Filter filter = addFilter("AccountID", QueryFilterOperator.CurrentAccount, null);
		initializeSql();
		assertContains("(Account.id = 1100)", sql.toString());
		assertEquals("CurrentAccount shouldn't change to Equals", QueryFilterOperator.CurrentAccount,
				filter.getOperator());
	}

	@Test
	public void testFilterMyUser() throws Exception {
		addFilter("AccountContactID", QueryFilterOperator.CurrentUser, null);
		initializeSql();
		assertContains("(AccountContact.id = 123)", sql.toString());
	}

	@Test
	public void testInvalidFilters() throws Exception {
		Column column = addColumn("AccountName");
		addFilter(column.getFieldName(), QueryFilterOperator.BeginsWith, null);
		EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);

		initializeSql();

		assertAllFiltersHaveFields();
	}

	private void assertAllFiltersHaveFields() {
		for (Filter filter : definition.getFilters()) {
			Assert.assertTrue(filter + " is missing the field", filter.getField() != null);
		}
	}

	@Test
	public void testGroupBy() throws Exception {
		addColumn("AccountStatus");
		addColumn("AccountStatus__Count");

		initializeSql();

		assertContains("Account.status AS `AccountStatus`", sql.toString());
		assertContains("COUNT(Account.status) AS `AccountStatus__Count`", sql.toString());
		assertContains("GROUP BY Account.status", sql.toString());
	}

	@Test
	public void testHaving() throws Exception {
		addColumn("AccountStatus");
		addColumn("AccountLegalName__Count");

		addFilter("AccountLegalName__Count", QueryFilterOperator.GreaterThan, "5");
		addFilter("AccountLegalName", QueryFilterOperator.BeginsWith, "A");

		initializeSql();

		assertContains("HAVING (COUNT(Account.name) > 5)", sql.toString());
		// Michael Test this for nameIndex
		assertContains("WHERE ((Account.name LIKE 'A%'))", sql.toString());
		assertContains("GROUP BY Account.status", sql.toString());
	}

	@Test
	public void testSorts() throws Exception {
		addSort("AccountStatus");
		initializeSql();
		assertContains("ORDER BY Account.status", sql.toString());
	}

	@Test
	public void testSortsDesc() throws Exception {
		Sort sort = addSort("AccountStatus");
		sort.setAscending(false);
		initializeSql();
		assertContains("ORDER BY Account.status DESC", sql.toString());
	}

	private Column addColumn(String fieldName) {
		Column column = new Column(fieldName);
		definition.getColumns().add(column);
		return column;
	}

	private Filter addFilter(String fieldName, QueryFilterOperator operator, String value) {
		Filter filter = new Filter();
		filter.setFieldName(fieldName);
		filter.setOperator(operator);
		filter.values.add(value);
		definition.getFilters().add(filter);
		return filter;
	}

	private Sort addSort(String fieldName) {
		Sort sort = new Sort(fieldName);
		definition.getSorts().add(sort);
		return sort;
	}

	private void initializeSql() throws ReportValidationException {
		sql = builder.initializeSql(new AccountModel(permissions), definition, permissions);
	}

}
