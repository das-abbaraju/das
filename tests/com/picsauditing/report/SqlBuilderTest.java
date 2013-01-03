package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.report.models.AccountsModel;
import com.picsauditing.search.SelectSQL;

public class SqlBuilderTest {

	private SqlBuilder builder;
	
	@Mock
	private Permissions permissions;
	
	private Definition definition;
	private SelectSQL sql;

	private final int USER_ID = 123;
	private final int ACCOUNT_ID = Account.PicsID;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(permissions.getAccountIdString()).thenReturn("" + ACCOUNT_ID);
		when(permissions.getVisibleAccounts()).thenReturn(new HashSet<Integer>());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);

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
		assertContains("TRIM(Account.name) AS `AccountName`", sql.toString());
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
		addFilter(column.getId(), QueryFilterOperator.BeginsWith, "Trevor's");

		initializeSql();

		assertContains("WHERE ((Account.nameIndex LIKE 'Trevor''s%'))", sql.toString());
		assertAllFiltersHaveFields();
	}

	@Test
	public void testFiltersWithComplexColumn() throws Exception {
		Column column = addColumn("AccountCreationDate__Year");

		addFilter(column.getId(), QueryFilterOperator.GreaterThan, "2010");

		initializeSql();

		assertContains("(YEAR(Account.creationDate) > 2010)", sql.toString());
		assertAllFiltersHaveFields();
	}
	
	@Test
	public void testAdvancedFilter() throws Exception {
		Column column = addColumn("AccountName");
		Column columnCompare = addColumn("AccountContactName");
		
		addFilter(column.getId(), QueryFilterOperator.Equals, columnCompare.getId(), true);
				
		initializeSql();
		
		assertContains("WHERE ((Account.nameIndex = AccountContact.name))", sql.toString());
		assertAllFiltersHaveFields();
	}

	@Test
	public void testFilterMyAccount() throws Exception {
		Filter filter = addFilter("AccountID", QueryFilterOperator.CurrentAccount, null);
		initializeSql();
		assertContains("(Account.id = "+this.ACCOUNT_ID+")", sql.toString());
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
		addFilter(column.getId(), QueryFilterOperator.BeginsWith, null);
		when(permissions.has(OpPerms.AllOperators)).thenReturn(true);
		
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
		addColumn("AccountName__Count");

		addFilter("AccountName__Count", QueryFilterOperator.GreaterThan, "5");
		addFilter("AccountName", QueryFilterOperator.BeginsWith, "A");

		initializeSql();

		assertContains("HAVING (COUNT(TRIM(Account.name)) > 5)", sql.toString());
		assertContains("WHERE ((Account.nameIndex LIKE 'A%'))", sql.toString());
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
		return addFilter(fieldName, operator, value, false);
	}

	private Filter addFilter(String fieldName, QueryFilterOperator operator, String value, boolean advanced) {
		Filter filter = new Filter();
		filter.setId(fieldName);
		filter.setOperator(operator);
		filter.values.add(value);
		if (advanced) {
			filter.setAdvancedFilter(advanced);
			filter.setFieldForComparison(new Field(value));
		}

		definition.getFilters().add(filter);
		return filter;
	}

	private Sort addSort(String fieldName) {
		Sort sort = new Sort(fieldName);
		definition.getSorts().add(sort);
		return sort;
	}

	private void initializeSql() throws ReportValidationException {
		sql = builder.initializeSql(new AccountsModel(permissions), definition, permissions);
	}

}
