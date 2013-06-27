package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static com.picsauditing.util.Assert.assertNotContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.models.ModelType;
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
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.search.SelectSQL;

@UseReporter(DiffReporter.class)
public class SqlBuilderTest {

	@Mock
	private Permissions permissions;

	private SqlBuilder sqlBuilder;

	private final int USER_ID = 123;
	private final int ACCOUNT_ID = Account.PicsID;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(permissions.getAccountIdString()).thenReturn("" + ACCOUNT_ID);
		when(permissions.getVisibleAccounts()).thenReturn(new HashSet<Integer>());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);

		sqlBuilder = new SqlBuilder();
	}

	@Test
	public void testFromTable() throws Exception {
		Report report = new Report();

		SelectSQL selectSQL = initializeReportAndBuildSql(report);

		assertEquals(0, selectSQL.getFields().size());
		assertContains("FROM accounts AS Account", selectSQL.toString());
	}

	@Test
	public void testMultipleColumns() throws Exception {
		Report report = new Report();

		addColumn("AccountID", report);
		addColumn("AccountName", report);
		addColumn("AccountStatus", report);

		SelectSQL sql = initializeReportAndBuildSql(report);

		Approvals.verify(sql.toString());
	}

	@Test
	public void testJoins() throws Exception {
		Report report = new Report();
		report.setModelType(ModelType.Contractors);

		SelectSQL selectSQL = sqlBuilder.initializeReportAndBuildSql(report, permissions);

		String expected = "FROM contractor_info AS Contractor "
				+ "JOIN accounts AS Account ON Contractor.id = Account.id AND Account.type = 'Contractor'";
		assertContains(expected, selectSQL.toString());
	}

	@Test
	public void testLeftJoinUser() throws Exception {
		Report report = new Report();
		addColumn("AccountID", report);
		addColumn("AccountName", report);
		addColumn("AccountContactName", report);

		SelectSQL selectSQL = initializeReportAndBuildSql(report);

		assertEquals(3, selectSQL.getFields().size());
		assertContains("LEFT JOIN users AS AccountContact ON Account.contactID = AccountContact.id", selectSQL.toString());
	}

	@Test
	public void testFilters() throws Exception {
		Report report = new Report();
		Column column = addColumn("AccountName", report);
		Filter filter = createFilter(column.getName(), QueryFilterOperator.BeginsWith, "Trevor's");
		report.addFilter(filter);

		SelectSQL sql = initializeReportAndBuildSql(report);

		Approvals.verify(sql.toString());
	}

	@Test
	public void testFiltersWithComplexColumn() throws Exception {
		Report report = new Report();
		Column column = addColumn("AccountCreationDate__Year", report);
		column.setSqlFunction(SqlFunction.Year);

		Field field = new Field("AccountCreationDate");
		field.setDatabaseColumnName("Account.creationDate");
		column.setField(field);

		Filter filter = createFilter("AccountCreationDate__Year", QueryFilterOperator.GreaterThan, "2010", SqlFunction.Year);
		filter.setField(field);
		report.addFilter(filter);

		SelectSQL sql = initializeReportAndBuildSql(report);

		Approvals.verify(sql.toString());
	}

	@Test
	public void testAdvancedFilter() throws Exception {
		Report report = new Report();
		Column column = addColumn("AccountName", report);
		Column columnCompare = addColumn("AccountContactName", report);
		Filter filter = createFilter(column.getName(), QueryFilterOperator.Equals, columnCompare.getName(), true);
		report.addFilter(filter);

		SelectSQL sql = initializeReportAndBuildSql(report);

		Approvals.verify(sql.toString());
	}

	@Test
	public void testFilterMyAccount() throws Exception {
		Report report = new Report();
		Filter filter = createFilter("AccountID", QueryFilterOperator.CurrentAccount, null);
		report.addFilter(filter);

		SelectSQL selectSQL = initializeReportAndBuildSql(report);

		assertContains("(Account.id = " + this.ACCOUNT_ID + ")", selectSQL.toString());
		assertEquals("CurrentAccount shouldn't change to Equals", QueryFilterOperator.CurrentAccount, filter.getOperator());
	}

	@Test
	public void testFilterMyUser() throws Exception {
		Report report = new Report();
		Filter filter = createFilter("AccountContactID", QueryFilterOperator.CurrentUser, null);
		report.addFilter(filter);

		SelectSQL selectSQL = initializeReportAndBuildSql(report);

		assertContains("(AccountContact.id = 123)", selectSQL.toString());
	}

	@Test
	public void testInvalidFilters() throws Exception {
		Report report = new Report();
		Column column = addColumn("AccountName", report);
		Filter filter = createFilter(column.getName(), QueryFilterOperator.BeginsWith, null);
		report.addFilter(filter);
		when(permissions.has(OpPerms.AllOperators)).thenReturn(true);

		initializeReportAndBuildSql(report);

		assertAllFiltersHaveFields(report);
	}

	private void assertAllFiltersHaveFields(Report report) {
		for (Filter filter : report.getFilters()) {
			Assert.assertTrue(filter + " is missing the field", filter.getField() != null);
		}
	}

	@Test
	public void testGroupBy() throws Exception {
		Report report = new Report();
		Column accountStatus = addColumn("AccountStatus", report);
		Column accountStatusCount = addColumn("AccountStatus__Count", report);
		accountStatusCount.setSqlFunction(SqlFunction.Count);

		Field field = new Field("AccountStatus");
		field.setDatabaseColumnName("Account.status");

		accountStatus.setField(field);
		accountStatusCount.setField(field);

		SelectSQL sql = initializeReportAndBuildSql(report);

		Approvals.verify(sql.toString());
	}

	@Test
	public void testHaving() throws Exception {
		Report report = new Report();
		sqlBuilder = new SqlBuilder();
		addColumn("AccountStatus", report);

		Column accountStatusCount = addColumn("AccountName__Count", report);
		accountStatusCount.setSqlFunction(SqlFunction.Count);

		Filter countFilter = createFilter("AccountName__Count", QueryFilterOperator.GreaterThan, "5", SqlFunction.Count);
		report.addFilter(countFilter);

		Filter filter2 = createFilter("AccountName", QueryFilterOperator.BeginsWith, "A");
		report.addFilter(filter2);

		SelectSQL sql = initializeReportAndBuildSql(report);

		Approvals.verify(sql.toString());
	}

	@Test
	public void testSorts() throws Exception {
		Report report = new Report();
		Sort sort = new Sort("AccountStatus");
		report.getSorts().add(sort);

		SelectSQL selectSQL = initializeReportAndBuildSql(report);

		assertContains("ORDER BY Account.status", selectSQL.toString());
	}

	@Test
	public void testSortsDesc() throws Exception {
		Report report = new Report();
		Sort sort = new Sort("AccountStatus");
		sort.setAscending(false);
		report.getSorts().add(sort);

		SelectSQL selectSQL = initializeReportAndBuildSql(report);

		assertContains("ORDER BY Account.status DESC", selectSQL.toString());
	}

    @Test
    public void testSortsDesc_BadValue() throws Exception {
        Report report = new Report();
        Sort sort = new Sort("asdf");
        sort.setAscending(false);
        report.getSorts().add(sort);

        SelectSQL selectSQL = initializeReportAndBuildSql(report);

        assertNotContains("ORDER BY Account.status DESC", selectSQL.toString());
    }

    @Test
	public void testSql() throws Exception {
		Report report = new Report();
		addColumn("accountCountry", report);
		report.setModelType(ModelType.Accounts);

		SelectSQL sql = new SqlBuilder().initializeReportAndBuildSql(report, permissions);
		String sqlResult = sql.toString();

		assertContains("SELECT Account.country AS `accountCountry` FROM accounts AS Account", sqlResult);
		assertFalse(sqlResult.contains("NAICS"));
	}

	@Test
	public void testSqlForOperator() throws Exception {
		Report report = new Report();
		report.setModelType(ModelType.Contractors);
		addColumn("AccountCountry", report);
		addColumn("ContractorFlagFlagColor", report);

		Permissions permissions = EntityFactory.makePermission(EntityFactory.makeUser(OperatorAccount.class));

		SelectSQL sql = new SqlBuilder().initializeReportAndBuildSql(report, permissions);

		String sqlResult = sql.toString();
		String expected = "JOIN generalcontractors AS ContractorFlag ON Contractor.id = ContractorFlag.subID AND ContractorFlag.genID = "
				+ permissions.getAccountId();
		assertContains(expected, sqlResult);
	}

	private Column addColumn(String fieldName, Report report) {
		Column column = new Column(fieldName);
		report.addColumn(column);
		return column;
	}

	private Filter createFilter(String fieldName, QueryFilterOperator operator, String value) {
		return createFilter(fieldName, operator, value, false, null);
	}

	private Filter createFilter(String fieldName, QueryFilterOperator operator, String value, boolean advanced) {
		return createFilter(fieldName, operator, value, advanced, null);
	}

	private Filter createFilter(String fieldName, QueryFilterOperator operator, String value, SqlFunction sqlFunction) {
		return createFilter(fieldName, operator, value, false, sqlFunction);
	}

	private Filter createFilter(String fieldName, QueryFilterOperator operator, String value, boolean advanced, SqlFunction sqlFunction) {
		Filter filter = new Filter();
		filter.setName(fieldName);
		filter.setOperator(operator);
		filter.setValue(value);
		if (advanced) {
			filter.setFieldForComparison(new Field(value));
		}
		filter.setSqlFunction(sqlFunction);

		return filter;
	}

	private SelectSQL initializeReportAndBuildSql(Report report) throws ReportValidationException {
		report.setModelType(ModelType.Accounts);
		return sqlBuilder.initializeReportAndBuildSql(report, permissions);
	}

}
