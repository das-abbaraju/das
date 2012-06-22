package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.QueryFunction;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.AccountContractorModel;
import com.picsauditing.report.models.AccountModel;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.business.DynamicReportUtil;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.picsauditing.util.business.DynamicReportUtil")
@PrepareForTest({SqlBuilder.class, DynamicReportUtil.class})
public class SqlBuilderTest {

	private SqlBuilder builder;

	private static final String DATABASE_COLUMN_NAME = "databaseColumnName";
	private static final String FIELD_NAME = "fieldName";

	@Mock private AccountTable table, joinTable, secondJoinTable;
	@Mock private AccountModel model;
	@Mock private SelectSQL sql;
	@Mock private Definition definition;
	@Mock private Field field;
	@Mock private Column column;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(DynamicReportUtil.class);

		builder = new SqlBuilder();
	}

	@Test
	public void testSetFrom_NoAlias() throws Exception {
		String tableName = "tableName";
		when(table.getTableName()).thenReturn(tableName);
		when(table.getAlias()).thenReturn("");
		when(model.getPrimaryTable()).thenReturn(table);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "setFrom", model);

		verify(sql).setFromTable(tableName);
	}

	@Test
	public void testSetFrom_WithAlias() throws Exception {
		String tableName = "tableName";
		when(table.getTableName()).thenReturn(tableName);
		String alias = "tn";
		when(table.getAlias()).thenReturn(alias);
		when(model.getPrimaryTable()).thenReturn(table);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "setFrom", model);

		verify(sql).setFromTable(tableName + " AS " + alias);
	}

	@Test
	public void testAddJoins_NullDoesntThrowException() throws Exception {
		AbstractTable nullTable = null;
		Whitebox.invokeMethod(builder, "addJoins", nullTable);
	}

	@Test
	public void testAddJoins_NoJoins() throws Exception {
		when(table.getJoins()).thenReturn(new ArrayList<AbstractTable>());
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, never()).addJoin(anyString());
	}

	@Test
	public void testAddJoins_JoinNotNeeded() throws Exception {
		when(joinTable.isJoinNeeded(any(Definition.class))).thenReturn(false);
		List<AbstractTable> joinTables = new ArrayList<AbstractTable>();
		joinTables.add(joinTable);
		when(table.getJoins()).thenReturn(joinTables);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, never()).addJoin(anyString());
	}

	@Test
	public void testAddJoins_InnerJoinNoAlias() throws Exception {
		String tableName = "tableName";
		String whereClause = "whereClause";
		when(joinTable.isJoinNeeded(any(Definition.class))).thenReturn(true);
		when(joinTable.isInnerJoin()).thenReturn(true);
		when(joinTable.getTableName()).thenReturn(tableName);
		when(joinTable.getAlias()).thenReturn("");
		when(joinTable.getWhereClause()).thenReturn(whereClause);

		List<AbstractTable> joinTables = new ArrayList<AbstractTable>();
		joinTables.add(joinTable);
		when(table.getJoins()).thenReturn(joinTables);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, times(1)).addJoin("JOIN " + tableName + " ON " + whereClause);
	}

	@Test
	public void testAddJoins_InnerJoinWithAlias() throws Exception {
		String tableName = "tableName";
		String whereClause = "whereClause";
		String alias = "alias";
		when(joinTable.isJoinNeeded(any(Definition.class))).thenReturn(true);
		when(joinTable.isInnerJoin()).thenReturn(true);
		when(joinTable.getTableName()).thenReturn(tableName);
		when(joinTable.getAlias()).thenReturn(alias);
		when(joinTable.getWhereClause()).thenReturn(whereClause);

		List<AbstractTable> joinTables = new ArrayList<AbstractTable>();
		joinTables.add(joinTable);
		when(table.getJoins()).thenReturn(joinTables);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, times(1)).addJoin("JOIN " + tableName + " AS " + alias + " ON " + whereClause);
	}

	@Test
	public void testAddJoins_OuterJoinNoAlias() throws Exception {
		String tableName = "tableName";
		String whereClause = "whereClause";
		when(joinTable.isJoinNeeded(any(Definition.class))).thenReturn(true);
		when(joinTable.isInnerJoin()).thenReturn(false);
		when(joinTable.getTableName()).thenReturn(tableName);
		when(joinTable.getAlias()).thenReturn("");
		when(joinTable.getWhereClause()).thenReturn(whereClause);

		List<AbstractTable> joinTables = new ArrayList<AbstractTable>();
		joinTables.add(joinTable);
		when(table.getJoins()).thenReturn(joinTables);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, times(1)).addJoin("LEFT JOIN " + tableName + " ON " + whereClause);
	}

	@Test
	public void testAddJoins_OuterJoinWithAlias() throws Exception {
		String tableName = "tableName";
		String whereClause = "whereClause";
		String alias = "alias";
		when(joinTable.isJoinNeeded(any(Definition.class))).thenReturn(true);
		when(joinTable.isInnerJoin()).thenReturn(false);
		when(joinTable.getTableName()).thenReturn(tableName);
		when(joinTable.getAlias()).thenReturn(alias);
		when(joinTable.getWhereClause()).thenReturn(whereClause);

		List<AbstractTable> joinTables = new ArrayList<AbstractTable>();
		joinTables.add(joinTable);
		when(table.getJoins()).thenReturn(joinTables);
		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, times(1)).addJoin("LEFT JOIN " + tableName + " AS " + alias + " ON " + whereClause);
	}

	@Test
	public void testAddJoins_CascadingJoin() throws Exception {
		List<AbstractTable> secondJoinTables = new ArrayList<AbstractTable>();
		when(secondJoinTable.isJoinNeeded(any(Definition.class))).thenReturn(true);
		secondJoinTables.add(secondJoinTable);
		when(joinTable.getJoins()).thenReturn(secondJoinTables);

		List<AbstractTable> joinTables = new ArrayList<AbstractTable>();
		when(joinTable.isJoinNeeded(any(Definition.class))).thenReturn(true);
		joinTables.add(joinTable);
		when(table.getJoins()).thenReturn(joinTables);

		Whitebox.setInternalState(builder, "sql", sql);

		Whitebox.invokeMethod(builder, "addJoins", table);

		verify(sql, times(2)).addJoin(anyString());
	}

	@Test
	public void testAddFieldsAndGroupBy_Simple() throws Exception {
		SqlBuilder builderSpy = PowerMockito.spy(builder);
		PowerMockito.doReturn(Boolean.FALSE).when(builderSpy, "usesGroupBy", any(Map.class));
		List<Column> columns = new ArrayList<Column>();
		when(column.getFieldName()).thenReturn(FIELD_NAME);
		when(column.getFunction()).thenReturn(QueryFunction.Count);
		columns.add(column);
		Map<String, Field> availableFields = new HashMap<String, Field>();

		Whitebox.invokeMethod(builderSpy, "addFieldsAndGroupBy", availableFields, columns);

		verify(sql, never()).addField(anyString());
	}

	@Test
	public void testIsFieldIncluded_True() throws Exception {
		String fieldName = "fieldName";
		List<Column> columns = new ArrayList<Column>();
		columns.add(new Column(fieldName));
		when(definition.getColumns()).thenReturn(columns);
		Whitebox.setInternalState(builder, "definition", definition);

		Boolean result = Whitebox.invokeMethod(builder, "isFieldIncluded", fieldName);

		assertTrue(result.booleanValue());
	}

	@Test
	public void testIsFieldIncluded_False() throws Exception {
		String fieldName = "fieldName";
		String wrongFieldName = "wrongFieldName";
		List<Column> columns = new ArrayList<Column>();
		columns.add(new Column(wrongFieldName));
		when(definition.getColumns()).thenReturn(columns);
		Whitebox.setInternalState(builder, "definition", definition);

		Boolean result = Whitebox.invokeMethod(builder, "isFieldIncluded", fieldName);

		assertFalse(result.booleanValue());
	}

	@Test
	public void testUsesGroupBy_FalseIfNoColumns() throws Exception {
		List<Column> columns = new ArrayList<Column>();
		when(definition.getColumns()).thenReturn(columns);
		Whitebox.setInternalState(builder, "definition", definition);
		Map<String, Field> emptyAvailableFields = new HashMap<String, Field>();

		Boolean result = Whitebox.invokeMethod(builder, "usesGroupBy", emptyAvailableFields);

		assertFalse(result.booleanValue());
	}

	@Test
	public void testUsesGroupBy_FalseIfFieldIsNull() throws Exception {
		List<Column> columns = new ArrayList<Column>();
		String fieldName = "fieldName";
		columns.add(new Column(fieldName));
		when(definition.getColumns()).thenReturn(columns);
		Whitebox.setInternalState(builder, "definition", definition);
		Map<String, Field> availableFields = new HashMap<String, Field>();
		availableFields.put(fieldName.toUpperCase(), null);

		Boolean result = Whitebox.invokeMethod(builder, "usesGroupBy", availableFields);

		assertFalse(result.booleanValue());
	}

	@Test
	public void testUsesGroupBy_FalseIfFieldIsNotAggregrate() throws Exception {
		String fieldName = "fieldName";
		when(column.getFieldName()).thenReturn(fieldName);
		when(column.getFunction()).thenReturn(null);
		List<Column> columns = new ArrayList<Column>();
		columns.add(column);

		when(definition.getColumns()).thenReturn(columns);
		Whitebox.setInternalState(builder, "definition", definition);

		Map<String, Field> availableFields = new HashMap<String, Field>();
		availableFields.put(fieldName.toUpperCase(), new Field(fieldName, fieldName, FilterType.AccountName));

		Boolean result = Whitebox.invokeMethod(builder, "usesGroupBy", availableFields);

		assertFalse(result.booleanValue());
	}

	@Ignore("Too low level")
	@Test
	public void testUsesGroupBy_TrueIfFieldIsAggregrate() throws Exception {
		String fieldName = "fieldName";
		when(column.getFieldName()).thenReturn(fieldName);
//		when(queryFunction.isAggregate()).thenReturn(true);
//		when(column.getFunction()).thenReturn(queryFunction);

		List<Column> columns = new ArrayList<Column>();
		columns.add(column);

		when(definition.getColumns()).thenReturn(columns);
		Whitebox.setInternalState(builder, "definition", definition);

		Map<String, Field> availableFields = new HashMap<String, Field>();
		availableFields.put(fieldName.toUpperCase(), new Field(fieldName, fieldName, FilterType.AccountName));

		Boolean result = Whitebox.invokeMethod(builder, "usesGroupBy", availableFields);

		assertTrue(result.booleanValue());
	}

	@Test
	public void testIsAggregate_FalseIfColumnIsNull() throws Exception {
		Column column = null;

		Boolean result = Whitebox.invokeMethod(builder, "isAggregate", column);

		assertFalse(result.booleanValue());
	}

	@Test
	public void testIsAggregate_FalseIfColumnFunctionIsNull() throws Exception {
		Column column = new Column("columnName");
		column.setFunction(null);

		Boolean result = Whitebox.invokeMethod(builder, "isAggregate", column);

		assertFalse(result.booleanValue());
	}

	@Test
	public void testColumnToSql_BlankIfNoField() throws Exception {
		String columnName = "columnName";
		Map<String, Field> availableFields = new HashMap<String, Field>();
		Column column = new Column(columnName);

		String result = Whitebox.invokeMethod(builder, "columnToSql", column, availableFields);

		assertEquals("", result);
	}

	@Test
	public void testColumnToSql_UndecoratedIfNoFunction() throws Exception {
		String result = setUpAndRunColumnToSqlTest(null, DATABASE_COLUMN_NAME);
		assertEquals(DATABASE_COLUMN_NAME, result);
	}

	@Test
	public void testColumnToSql_UndecoratedIfUnrecognizedQueryFunction() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.None, DATABASE_COLUMN_NAME);
		assertEquals(DATABASE_COLUMN_NAME, result);
	}

	@Test
	public void testColumnToSql_Average() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Average, DATABASE_COLUMN_NAME);
		assertEquals("AVG(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Count() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Count, DATABASE_COLUMN_NAME);
		assertEquals("COUNT(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_CountDistinct() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.CountDistinct, DATABASE_COLUMN_NAME);
		assertEquals("COUNT(DISTINCT " + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Date() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Date, DATABASE_COLUMN_NAME);
		assertEquals("DATE(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_LowerCase() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.LowerCase, DATABASE_COLUMN_NAME);
		assertEquals("LOWER(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_UpperCase() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.UpperCase, DATABASE_COLUMN_NAME);
		assertEquals("UPPER(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Max() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Max, DATABASE_COLUMN_NAME);
		assertEquals("MAX(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Min() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Min, DATABASE_COLUMN_NAME);
		assertEquals("MIN(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Month() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Month, DATABASE_COLUMN_NAME);
		assertEquals("MONTH(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Round() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Round, DATABASE_COLUMN_NAME);
		assertEquals("ROUND(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Sum() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Sum, DATABASE_COLUMN_NAME);
		assertEquals("SUM(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testColumnToSql_Year() throws Exception {
		String result = setUpAndRunColumnToSqlTest(QueryFunction.Year, DATABASE_COLUMN_NAME);
		assertEquals("YEAR(" + DATABASE_COLUMN_NAME + ")", result);
	}

	@Test
	public void testAddQuotesToValues_Blank() throws Exception {
		String unquotedValues = "";

		String quotedValues = Whitebox.invokeMethod(builder, "addQuotesToValues", unquotedValues);

		assertEquals("''", quotedValues);
	}

	@Test
	public void testAddQuotesToValues_Simple() throws Exception {
		String unquotedValues = "1,2,3";

		String quotedValues = Whitebox.invokeMethod(builder, "addQuotesToValues", unquotedValues);

		assertEquals("'1','2','3'", quotedValues);
	}

	@Test
	public void testAddQuotesToValues_WithSpaces() throws Exception {
		String unquotedValues = " 1 , 2 , 3 ";

		String quotedValues = Whitebox.invokeMethod(builder, "addQuotesToValues", unquotedValues);

		assertEquals("'1','2','3'", quotedValues);
	}

	public String setUpAndRunColumnToSqlTest(QueryFunction queryFunction, String databaseColumnName) throws Exception {
		when(field.getDatabaseColumnName()).thenReturn(databaseColumnName);

		String columnName = "columnName";
		Map<String, Field> availableFields = new HashMap<String, Field>();
		availableFields.put(columnName.toUpperCase(), field);

		when(column.getFieldName()).thenReturn(columnName);
		when(column.getFunction()).thenReturn(queryFunction);

		return Whitebox.invokeMethod(builder, "columnToSql", column, availableFields);
	}

//	@Ignore("This is in the wrong class. It should be in AccountModelTest")
	@Test
	public void testFromTable() throws Exception {
		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertEquals(0, sql.getFields().size());
		assertContains("FROM accounts AS a", sql.toString());
		assertContains("ORDER BY a.name", sql.toString());
	}

	@Test
	public void testMultipleColumns() {
		Definition definition = new Definition();
		definition.getColumns().add(new Column("accountID"));
		definition.getColumns().add(new Column("accountName"));
		definition.getColumns().add(new Column("accountStatus"));
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertEquals(3, sql.getFields().size());

		assertContains("a.id AS `accountID`", sql.toString());
		assertContains("a.name AS `accountName`", sql.toString());
		assertContains("a.status AS `accountStatus`", sql.toString());
	}

	@Test
	public void testJoins() {
		SelectSQL sql = builder.initializeSql(new AccountContractorModel());

		String expected = "JOIN contractor_info AS c ON a.id = c.id AND a.type = 'Contractor'";
		assertContains(expected, sql.toString());
	}

	@Test
	public void testContractorColumns() {
		Definition definition = new Definition();
		definition.getColumns().add(new Column("contractorName"));
		definition.getColumns().add(new Column("contractorScore"));
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(new AccountContractorModel());

		assertEquals(3, sql.getFields().size());
	}

	@Test
	public void testLeftJoinUser() throws Exception {
		Definition definition = new Definition();
		definition.getColumns().add(new Column("accountID"));
		definition.getColumns().add(new Column("accountName"));
		definition.getColumns().add(new Column("accountContactName"));
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertEquals(3, sql.getFields().size());
		assertContains("LEFT JOIN users AS accountContact ON accountContact.id = a.contactID", sql.toString());
	}

	@Test
	public void testFilters() {
		Definition definition = new Definition();
		definition.getColumns().add(new Column("accountName"));
		Filter filter = new Filter();
		filter.setFieldName("accountName");
		filter.setOperator(QueryFilterOperator.BeginsWith);
		filter.setValue("Trevor's");
		definition.getFilters().add(filter);
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertContains("WHERE ((a.nameIndex LIKE 'Trevor\\'s%'))", sql.toString());
	}

	@Ignore("NullPointerException I don't have time to fix now - Mike N.")
	@Test
	public void testFiltersWithComplexColumn() {
		String columnName = "accountCreationDateYear";
		Definition definition = new Definition();
		Column column = new Column(columnName);
		column.setFunction(QueryFunction.Year);
		definition.getColumns().add(column);
		builder.setDefinition(definition);

		Filter filter = new Filter();
		filter.setFieldName(columnName);
		filter.setOperator(QueryFilterOperator.GreaterThan);
		filter.setValue("2010");

		definition.getFilters().add(filter);

		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertContains("(YEAR(a.creationDate) > '2010')", sql.toString());
	}

	@Ignore("accountStatusCount needs to be added to availableFields")
	@Test
	public void testGroupBy() {
		Definition definition = new Definition();
		definition.getColumns().add(new Column("accountStatus"));
		Column column = new Column("accountStatusCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertContains("COUNT(a.status)", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Ignore("NullPointerException I don't have time to fix now - Mike N.")
	@Test
	public void testHaving() {
		// {"filters":[{"column":"contractorName","operator":"BeginsWith","value":"Da"}]}
		Definition definition = new Definition();
		definition.getColumns().add(new Column("accountStatus"));
		Column column = new Column("accountNameCount");
		column.setFunction(QueryFunction.Count);
		definition.getColumns().add(column);
		builder.setDefinition(definition);

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

		SelectSQL sql = builder.initializeSql(new AccountModel());

		assertContains("HAVING (COUNT(a.name) > '5')", sql.toString());
		assertContains("WHERE ((a.nameIndex LIKE 'A%'))", sql.toString());
		assertContains("GROUP BY a.status", sql.toString());
	}

	@Test
	public void testGroupByContractorName() {
		Column contractorNameCount = new Column("contractorNameCount");
		contractorNameCount.setFunction(QueryFunction.Count);
		Definition definition = new Definition();
		definition.getColumns().add(contractorNameCount);
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(new AccountContractorModel());
		assertEquals(0, sql.getFields().size());
//		assertEquals("", sql.getOrderBy());
	}

	@Test
	public void testSorts() {
		AbstractModel accountModel = new AccountModel();

		Sort sort = new Sort("accountStatus");
		Definition definition = new Definition();
		definition.getSorts().add(sort);
		builder.setDefinition(definition);

		SelectSQL sql = builder.initializeSql(accountModel);
		assertContains("ORDER BY a.status", sql.toString());

//		definition.getColumns().add(new Column("accountStatus"));
//		sql = builder.initializeSql(accountModel);
//		assertContains("ORDER BY accountStatus", sql.toString());

		sort.setAscending(false);
		sql = builder.initializeSql(accountModel);
		assertContains("ORDER BY a.status DESC", sql.toString());
	}
}
