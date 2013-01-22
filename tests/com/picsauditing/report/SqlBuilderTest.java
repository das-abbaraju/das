package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.report.models.AccountsModel;
import com.picsauditing.search.SelectSQL;

@UseReporter(DiffReporter.class)
public class SqlBuilderTest {
	
	@Mock
	private Permissions permissions;
	
	private SqlBuilder builder;
	private Report report = new Report();
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
	}

	@Test
	public void testFromTable() throws Exception {
		initializeSql();

		assertEquals(0, sql.getFields().size());
		assertContains("FROM accounts AS Account", sql.toString());
	}

	@Test
	public void testMultipleColumns() throws Exception {	
		builder = new SqlBuilder();
		addColumn("AccountID");
		addColumn("AccountName");
		addColumn("AccountStatus");
		verifySql();
	}

	private void verifySql() throws ReportValidationException, Exception {
		SelectSQL sql = initializeSql();
		Approvals.verify(sql.toString());
	}

	@Test
	public void testJoins() throws Exception {
		sql = builder.initializeSql(new AccountContractorModel(permissions), report, permissions);

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
		addFilter(column.getName(), QueryFilterOperator.BeginsWith, "Trevor's");

		verifySql();
	}

	@Test
	public void testFiltersWithComplexColumn() throws Exception {
		Column column = addColumn("AccountCreationDate__Year");
		column.setSqlFunction(SqlFunction.Year);

		Filter filter = addFilter("AccountCreationDate__Year", QueryFilterOperator.GreaterThan, "2010");
		filter.setSqlFunction(SqlFunction.Year);
		
		Field field = new Field("AccountCreationDate");
		field.setDatabaseColumnName("Account.creationDate");
		column.setField(field);
		filter.setField(field);

		verifySql();
	}
	
	@Test
	public void testAdvancedFilter() throws Exception {
		Column column = addColumn("AccountName");
		Column columnCompare = addColumn("AccountContactName");
		addFilter(column.getName(), QueryFilterOperator.Equals, columnCompare.getName(), true);
		verifySql();
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
		addFilter(column.getName(), QueryFilterOperator.BeginsWith, null);
		when(permissions.has(OpPerms.AllOperators)).thenReturn(true);
		
		initializeSql();

		assertAllFiltersHaveFields();
	}

	private void assertAllFiltersHaveFields() {
		for (Filter filter : report.getFilters()) {
			Assert.assertTrue(filter + " is missing the field", filter.getField() != null);
		}
	}

	@Test
	public void testGroupBy() throws Exception {
		Column accountStatus = addColumn("AccountStatus");
		Column accountStatusCount = addColumn("AccountStatus__Count");
		accountStatusCount.setSqlFunction(SqlFunction.Count);

		Field field = new Field("AccountStatus");
		field.setDatabaseColumnName("Account.status");
		
		accountStatus.setField(field);		
		accountStatusCount.setField(field);
		
		verifySql();
	}

	@Test
	public void testHaving() throws Exception {
		builder = new SqlBuilder();
		addColumn("AccountStatus");
		
		Column accountStatusCount = addColumn("AccountName__Count");
		accountStatusCount.setSqlFunction(SqlFunction.Count);

		Filter countFilter = addFilter("AccountName__Count", QueryFilterOperator.GreaterThan, "5");
		countFilter.setSqlFunction(SqlFunction.Count);
		
		addFilter("AccountName", QueryFilterOperator.BeginsWith, "A");
		verifySql();
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
		report.getColumns().add(column);
		return column;
	}
	
	private Filter addFilter(String fieldName, QueryFilterOperator operator, String value) {
		return addFilter(fieldName, operator, value, false);
	}

	private Filter addFilter(String fieldName, QueryFilterOperator operator, String value, boolean advanced) {
		Filter filter = new Filter();
		filter.setName(fieldName);
		filter.setOperator(operator);
		filter.getValues().add(value); // ???
		if (advanced) {
			filter.setFieldForComparison(new Field(value));
		}

		report.getFilters().add(filter);
		return filter;
	}

	private Sort addSort(String fieldName) {
		Sort sort = new Sort(fieldName);
		report.getSorts().add(sort);
		return sort;
	}

	private SelectSQL initializeSql() throws ReportValidationException {
		sql = builder.initializeSql(new AccountsModel(permissions), report, permissions);
		return sql;
	}

}
