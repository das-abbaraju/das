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
		definition = new Definition();
	}

	@Test
	public void testFromTable() throws Exception {
		initializeSql();

		assertEquals(0, sql.getFields().size());
		assertContains("FROM accounts AS a", sql.toString());
	}

	@Test
	public void testDefaultSort() throws Exception {
		initializeSql();
		assertContains("ORDER BY a.name", sql.toString());
	}

	@Test
	public void testMultipleColumns() throws Exception {
		addColumn("accountID");
		addColumn("accountName");
		addColumn("accountStatus");

		initializeSql();

		assertEquals(3, sql.getFields().size());

		assertContains("a.id AS `accountID`", sql.toString());
		assertContains("a.name AS `accountName`", sql.toString());
		assertContains("a.status AS `accountStatus`", sql.toString());
	}

	@Test
	public void testJoins() throws Exception {
		sql = builder.initializeSql(new AccountContractorModel(), definition, permissions);

		String expected = "JOIN contractor_info AS c ON a.id = c.id AND a.type = 'Contractor'";
		assertContains(expected, sql.toString());
	}

	@Test
	public void testLeftJoinUser() throws Exception {
		addColumn("accountID");
		addColumn("accountName");
		addColumn("accountContactName");

		initializeSql();

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS accountContact ON accountContact.id = a.contactID", sql.toString());
	}

	@Test
	public void testFilters() throws Exception {
		Column column = addColumn("accountName");
		addFilter(column.getFieldName(), QueryFilterOperator.BeginsWith, "Trevor's");

		initializeSql();
		
		assertContains("WHERE ((a.nameIndex LIKE 'Trevor\\'s%'))", sql.toString());
		assertAllFiltersHaveFields();
	}

	@Test
	public void testFiltersWithComplexColumn() throws Exception {
		Column column = addColumn("accountCreationDate__Year");

		addFilter(column.getFieldName(), QueryFilterOperator.GreaterThan, "2010");

		initializeSql();
		
		assertContains("(YEAR(a.creationDate) > 2010)", sql.toString());
		assertAllFiltersHaveFields();
	}

	@Test
	public void testFilterMyAccount() throws Exception {
		addFilter("accountID", QueryFilterOperator.CurrentAccount, null);
		initializeSql();
		assertContains("(a.id = 1100)", sql.toString());
	}

	@Test
	public void testFilterMyUser() throws Exception {
		addFilter("accountContactID", QueryFilterOperator.CurrentUser, null);
		initializeSql();
		assertContains("(accountContact.id = 123)", sql.toString());
	}

	@Test
	public void testInvalidFilters() throws Exception {
		Column column = addColumn("accountName");
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
		addColumn("accountStatus");
		addColumn("accountStatus__Count");

		initializeSql();

		assertContains("a.status AS `accountStatus`", sql.toString());
		assertContains("COUNT(a.status) AS `accountStatus__Count`", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testHaving() throws Exception {
		addColumn("accountStatus");
		addColumn("accountName__Count");

		addFilter("accountName__Count", QueryFilterOperator.GreaterThan, "5");
		addFilter("accountName", QueryFilterOperator.BeginsWith, "A");

		initializeSql();

		assertContains("HAVING (COUNT(a.name) > 5)", sql.toString());
		assertContains("WHERE ((a.nameIndex LIKE 'A%'))", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testSorts() throws Exception {
		addSort("accountStatus");
		initializeSql();
		assertContains("ORDER BY a.status", sql.toString());
	}

	@Test
	public void testSortsDesc() throws Exception {
		Sort sort = addSort("accountStatus");
		sort.setAscending(false);
		initializeSql();
		assertContains("ORDER BY a.status DESC", sql.toString());
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
		sql = builder.initializeSql(new AccountModel(), definition, permissions);
	}

}
