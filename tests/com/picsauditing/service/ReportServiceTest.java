package com.picsauditing.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.sql.SQLException;
import java.util.*;

import javax.persistence.NoResultException;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.report.ReportContext;
import com.picsauditing.report.ReportJson;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.data.ReportDataConverter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.JSONUtilities;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.report.converter.LegacyReportConverter;
import com.picsauditing.report.models.ModelType;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

public class ReportServiceTest {

	private ReportService reportService;

	@Mock
	private ReportDAO reportDao;
	@Mock
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Mock
	private ReportPermissionAccountDAO reportPermissionAccountDao;
	@Mock
	private User user;
	@Mock
	private Account account;
	@Mock
	private Report report;
	@Mock
	private ReportPermissionUser reportPermissionUser;
	@Mock
	private Permissions permissions;
	@Mock
	private ReportContext reportContext;
	@Mock
	private PermissionService permissionService;
	@Mock
	private ReportPreferencesService reportPreferencesService;
	@Mock
	private SqlBuilder sqlBuilder;
	@Mock
	protected I18nCache i18nCache;

	private LegacyReportConverter legacyReportConverter;
	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mock(Database.class));
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportService = new ReportService();
		legacyReportConverter = new LegacyReportConverter();

		setInternalState(reportService, "reportDao", reportDao);
		setInternalState(reportService, "reportPermissionUserDao", reportPermissionUserDao);
		setInternalState(reportService, "reportPermissionAccountDao", reportPermissionAccountDao);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);
		setInternalState(reportService, "permissionService", permissionService);
		setInternalState(reportService, "reportPreferencesService", reportPreferencesService);
		setInternalState(reportService, "sqlBuilder", sqlBuilder);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);
		when(account.getId()).thenReturn(ACCOUNT_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getAccountIdString()).thenReturn(String.valueOf(ACCOUNT_ID));
		when(permissions.getUserIdString()).thenReturn(String.valueOf(USER_ID));

		when(i18nCache.getText(anyString(), any(Locale.class))).then(translate());
		Whitebox.setInternalState(ReportUtil.class, "i18nCache", i18nCache);
		Whitebox.setInternalState(ReportDataConverter.class, "i18nCache", i18nCache);
	}

	@AfterClass
	public static void tearDownClass() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
		Whitebox.setInternalState(ReportUtil.class, "i18nCache", (I18nCache) null);
		Whitebox.setInternalState(ReportDataConverter.class, "i18nCache", (I18nCache) null);
	}

	@Test
	public void testBuildJsonResponse_whenIncludeData_resultsElementShouldBePopulated() throws ReportValidationException, SQLException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, user, permissions, false, true, false, false, 0, 0);
		when(reportDao.findById(REPORT_ID)).thenReturn(report);
		when(report.getModelType()).thenReturn(ModelType.Contractors);
		when(report.getColumns()).thenReturn(getReportColumns());
		SelectSQL selectSql = getTestSelectSql();
		when(sqlBuilder.initializeReportAndBuildSql(report, permissions)).thenReturn(selectSql);
		int resultsToBuild = 3;
		when(reportDao.runQuery(eq(selectSql.toString()), any(JSONObject.class))).thenAnswer(createQueryResults(resultsToBuild));
		when(permissions.isAdmin()).thenReturn(true);

		JSONObject responseJson = reportService.buildJsonResponse(reportContext);

		assertEquals(2, responseJson.size());

		JSONObject resultsJson = (JSONObject) responseJson.get(ReportJson.LEVEL_RESULTS);
		assertEquals(3, resultsJson.size());

		boolean successFlag = (Boolean)responseJson.get(ReportJson.EXT_JS_SUCCESS);
		assertTrue(successFlag);

		int resultsTotal = (Integer) resultsJson.get(ReportJson.RESULTS_TOTAL);
		assertEquals(resultsToBuild, resultsTotal);

		String resultsSql = (String) resultsJson.get(ReportJson.RESULTS_SQL);
		assertEquals(getExpectedResultsSql(), resultsSql);

		JSONArray resultsDataJsonArray = (JSONArray) resultsJson.get(ReportJson.RESULTS_DATA);
		assertEquals(3, resultsDataJsonArray.size());

		JSONArray expectedResultsData = getExpectedResultData();

		JSONObject expectedResultsDataRow = (JSONObject) expectedResultsData.get(0);
		JSONObject actualResultsDataRow = (JSONObject) resultsDataJsonArray.get(0);
		assertEquals(expectedResultsDataRow.size(), actualResultsDataRow.size());
		validateDataRow(expectedResultsDataRow, actualResultsDataRow);

		expectedResultsDataRow = (JSONObject) expectedResultsData.get(1);
		actualResultsDataRow = (JSONObject) resultsDataJsonArray.get(1);
		assertEquals(expectedResultsDataRow.size(), actualResultsDataRow.size());
		validateDataRow(expectedResultsDataRow, actualResultsDataRow);

		expectedResultsDataRow = (JSONObject) expectedResultsData.get(2);
		actualResultsDataRow = (JSONObject) resultsDataJsonArray.get(2);
		assertEquals(expectedResultsDataRow.size(), actualResultsDataRow.size());
		validateDataRow(expectedResultsDataRow, actualResultsDataRow);
	}

	private void validateDataRow(JSONObject expectedResultsDataRow, JSONObject actualResultsDataRow) {
		assertEquals(expectedResultsDataRow.get("AccountCountry"), actualResultsDataRow.get("AccountCountry"));
		assertEquals(expectedResultsDataRow.get("AccountStatus"), actualResultsDataRow.get("AccountStatus"));
		assertEquals(expectedResultsDataRow.get("AccountID"), actualResultsDataRow.get("AccountID"));
		assertEquals(expectedResultsDataRow.get("AccountCountrySubdivision"), actualResultsDataRow.get("AccountCountrySubdivision"));
		assertEquals(expectedResultsDataRow.get("AccountCountryName"), actualResultsDataRow.get("AccountCountryName"));
		assertEquals(expectedResultsDataRow.get("AccountCity"), actualResultsDataRow.get("AccountCity"));
		assertEquals(expectedResultsDataRow.get("ContractorMembershipDate"), actualResultsDataRow.get("ContractorMembershipDate"));
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_WhenReportIsNull_ThrowException() throws ReportValidationException {
		Report report = null;

		reportService.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_WhenModelTypeIsMissing_ThrowException() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(null);

		reportService.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_WhenColumnsAreMissing_ThrowException() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		report.setColumns(new ArrayList<Column>());

		reportService.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_WhenParametersAreInvalid_ThrowException() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		ArrayList<Column> notEmptyColumnList = new ArrayList<Column>(){{add(new Column());}};
		report.setColumns(notEmptyColumnList);
		report.setParameters("this is not valid json");

		reportService.validate(report);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithUser_WhenUserCantViewOrEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		reportService.shareReportWithUser(USER_ID, REPORT_ID, permissions, false);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithUser_WhenUserCanViewButNotEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		reportService.shareReportWithUser(USER_ID, REPORT_ID, permissions, false);
	}

	@Test
	public void testShareReportWithUser_WhenUserCanViewAndEdit_AndEditableIsTrue_ThenReportIsSharedWithEditPermission() throws ReportPermissionException {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.findById(REPORT_ID)).thenReturn(report);
		when(reportDao.find(User.class, USER_ID)).thenReturn(user);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);
		boolean editable = true;

		ReportPermissionUser reportPermissionUser = reportService.shareReportWithUser(USER_ID, REPORT_ID, permissions, editable);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertEquals(editable, reportPermissionUser.isEditable());
	}

	@Test
	public void testShareReportWithUser_WhenUserCanViewAndEdit_AndEditableIsFalse_ThenReportIsSharedWithoutEditPermission() throws ReportPermissionException {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.findById(REPORT_ID)).thenReturn(report);
		when(reportDao.find(User.class, USER_ID)).thenReturn(user);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);
		boolean editable = false;

		ReportPermissionUser reportPermissionUser = reportService.shareReportWithUser(USER_ID, REPORT_ID, permissions, editable);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertEquals(editable, reportPermissionUser.isEditable());
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithAccount_WhenUserCantViewOrEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		reportService.shareReportWithAccount(ACCOUNT_ID, REPORT_ID, permissions);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithAccount_WhenUserCanViewButNotEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		reportService.shareReportWithAccount(ACCOUNT_ID, REPORT_ID, permissions);
	}

	@Test
	public void testShareReportWithAccount_WhenUserCanViewAndEdit_ThenReportIsShared() throws ReportPermissionException {
		when(reportPermissionAccountDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.findById(REPORT_ID)).thenReturn(report);
		when(reportDao.find(Account.class, ACCOUNT_ID)).thenReturn(account);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);

		ReportPermissionAccount reportPermissionAccount = reportService.shareReportWithAccount(ACCOUNT_ID, REPORT_ID, permissions);

		verify(reportPermissionAccountDao).save(reportPermissionAccount);
		assertEquals(REPORT_ID, reportPermissionAccount.getReport().getId());
		assertEquals(ACCOUNT_ID, reportPermissionAccount.getAccount().getId());
	}

	@Test
	public void testCreateOrLoadReport_WhenJsonIsPassedInAndIncludeDataIsTrue_LoadFromJson() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, true, false, false, 0, 0);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);


		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(REPORT_ID, resultReport.getId());
	}

	@Test
	public void testCreateOrLoadReport_WhenIncludeDataIsFalse_HitTheDb() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, false, false, false, 0, 0);
		when(reportDao.findById(anyInt())).thenReturn(report);
		legacyReportConverter = mock(LegacyReportConverter.class);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissions.getUserId()).thenReturn(USER_ID);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(report, resultReport);
	}

	@Test
	public void testCreateOrLoadReport_WhenReportJsonIsEmpty_HitTheDb() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, true, false, false, 0, 0);
		when(reportDao.findById(anyInt())).thenReturn(report);
		legacyReportConverter = mock(LegacyReportConverter.class);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissions.getUserId()).thenReturn(USER_ID);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(report, resultReport);
	}

	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_ReportPropertiesAreNotMutated() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, true, false, false, 0, 0);
		setupMockBasicLegacyReport();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(report.getId(), resultReport.getId());
		assertEquals(report.getModelType(), resultReport.getModelType());
		assertEquals(report.getName(), resultReport.getName());
		assertEquals(report.getNumTimesFavorited(), resultReport.getNumTimesFavorited());
		assertEquals(report.getParameters(), resultReport.getParameters());
		assertEquals(report.getDescription(), resultReport.getDescription());
		assertEquals(report.getSql(), resultReport.getSql());
		assertEquals(report.getFilterExpression(), resultReport.getFilterExpression());
	}

	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_andReportIsEmpty_setReportPropertiesFromJsonParameters() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		ReportContext reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, true, false, false, 0, 0);
		Report report = setupRealLegacyReportWithParameterJson();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		List<Column> columns = resultReport.getColumns();
		assertEquals(7, columns.size());
		Map<String, Column> resultReportElementMap = createReportElementMap(columns);

		verifyColumn("AccountName", resultReportElementMap);
		verifyColumn("ContractorMembershipDate", resultReportElementMap);
		verifyColumn("AccountCity", resultReportElementMap);
		verifyColumn("ContractorPayingFacilities", resultReportElementMap);
		verifyColumn("AccountCountrySubdivision", resultReportElementMap);
		verifyColumn("AccountCountry", resultReportElementMap);
	}

	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_FiltersShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		ReportContext reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, true, false, false, 0, 0);
		Report report = setupRealLegacyReportWithParameterJson();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		List<Filter> filters = resultReport.getFilters();
		assertEquals(2, filters.size());
		Map<String, Filter> resultFilterMap = createReportElementMap(filters);

		verifyFilter("AccountName", QueryFilterOperator.Contains, JSONUtilities.EMPTY_JSON_ARRAY, resultFilterMap);
		verifyFilter("AccountStatus", QueryFilterOperator.In, "[Active, Pending]", resultFilterMap);
	}

	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_SortsShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		ReportContext reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, true, false, false, 0, 0);
		Report report = setupRealLegacyReportWithParameterJson();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		List<Sort> sorts = resultReport.getSorts();
		assertEquals(1, sorts.size());
		Map<String, Sort> resultSortMap = createReportElementMap(sorts);

		verifySort("AccountName", true, resultSortMap);
	}

	@Test(expected = Exception.class)
	public void testCopy_WhenUserDoesntHavePermissionToCopy_ThenExceptionIsThrown() throws Exception {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, false, false, false, 0, 0);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(false);

		reportService.copy(reportContext);
	}

	@Test
	public void testCopy_WhenReportIsCopied_ThenReportIsValidatedAndSaved() throws Exception {
		JSONObject payloadJson = buildMinimalPayloadJson();
		ReportContext reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, false, false, false, 0, 0);
		when(permissionService.canUserViewReport(eq(permissions), anyInt())).thenReturn(true);
		when(reportPermissionUserDao.findOne(eq(USER_ID), anyInt())).thenReturn(reportPermissionUser);
		ReportService reportServiceSpy = spy(reportService);

		Report newReport = reportServiceSpy.copy(reportContext);

		assertTrue(REPORT_ID != newReport.getId());
		verify(reportServiceSpy).validate(newReport);
		verify(reportDao).save(newReport);
	}

	@Test(expected = Exception.class)
	public void testSave_WhenUserDoesntHavePermissionToEdit_ThenExceptionIsThrown() throws Exception {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, false, false, false, 0, 0);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		reportService.save(reportContext);
	}

	@Test
	public void testSave_WhenReportIsSaved_ThenReportIsValidatedAndSaved() throws Exception {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, permissions, false, false, false, false, 0, 0);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);
		ReportService reportServiceSpy = spy(reportService);

		Report report = reportServiceSpy.save(reportContext);

		assertTrue(REPORT_ID == report.getId());
		verify(reportServiceSpy).validate(report);
		verify(reportDao).save(report);
	}

	private void verifyColumn(String columnName, Map<String, Column> columnMap) {
		Column column = columnMap.get(columnName);
		assertNotNull(column);
		assertEquals(columnName, column.getName());
	}

	private void verifyFilter(String filterName, QueryFilterOperator operator, String value, Map<String, Filter> filterMap) {
		Filter filter = filterMap.get(filterName);
		assertNotNull(filter);
		assertEquals(filterName, filter.getName());
		assertEquals(operator, filter.getOperator());
		assertEquals(value, filter.getValues().toString());
	}

	private void verifySort(String sortName, boolean ascending, Map<String, Sort> sortMap) {
		Sort sort = sortMap.get(sortName);
		assertNotNull(sort);
		assertEquals(sortName, sort.getName());
		assertEquals(ascending, sort.isAscending());
	}

	private <T extends ReportElement> Map<String, T> createReportElementMap(List<T> reportElements) {
		Map<String, T> elementMap = new HashMap<String, T>();
		for (T reportElement : reportElements) {
			elementMap.put(reportElement.getName(), reportElement);
		}
		return elementMap;
	}

	// A legacy report contains parameters(json), which are used to load reportElements, filters, and sorts.
	private void setupMockBasicLegacyReport() {
		when(report.getId()).thenReturn(REPORT_ID);
		when(report.getModelType()).thenReturn(ModelType.Contractors);
		when(report.getName()).thenReturn("fooReport");
		when(report.getNumTimesFavorited()).thenReturn(5);
		when(report.getParameters()).thenReturn(getParameterJson());
		when(report.getDescription()).thenReturn("A basic report for testing");
		when(report.getSql()).thenReturn("select * from dual");
		when(report.getFilterExpression()).thenReturn("where somecolumn is 'foo'");
	}

	private Report setupRealLegacyReportWithParameterJson() {
		Report report = new Report();
		report.setParameters(getParameterJson());
		return report;
	}

	private String getParameterJson() {
		return "{\"id\":1,\"modelType\":\"Contractors\",\"name\":\"Contractor List\",\"description\":\"Default Contractor List for PICS Employees\",\"filterExpression\":\"\",\"rowsPerPage\":50,\"columns\":[{\"name\":\"AccountName\"},{\"name\":\"AccountStatus\"},{\"name\":\"ContractorMembershipDate\"},{\"name\":\"AccountCity\"},{\"name\":\"ContractorPayingFacilities\"},{\"name\":\"AccountCountrySubdivision\"},{\"name\":\"AccountCountry\"}],\"filters\":[{\"name\":\"AccountName\",\"operator\":\"Contains\"},{\"name\":\"AccountStatus\",\"operator\":\"In\",\"value\":\"Active, Pending\"}],\"sorts\":[{\"name\":\"AccountName\",\"direction\":\"ASC\"}]}";
	}

	private Report setupMockBasicReport() {
		when(report.getId()).thenReturn(REPORT_ID);
		when(report.getModelType()).thenReturn(ModelType.Contractors);
		when(report.getName()).thenReturn("fooReport");
		when(report.getNumTimesFavorited()).thenReturn(5);
		when(report.getParameters()).thenReturn("{\"randomjson\":\"this is some json\"}");
		when(report.getDescription()).thenReturn("A basic report for testing");
		when(report.getSql()).thenReturn("select * from dual");
		when(report.getFilterExpression()).thenReturn("where somecolumn is 'foo'");

		Column column = new Column();
		List<Column> columns = new ArrayList<Column>();
		columns.add(column);
		when(report.getColumns()).thenReturn(columns);

		return report;
	}

	private JSONObject buildMinimalPayloadJson() {
		JSONObject reportJson = new JSONObject();
		reportJson.put(ReportJson.REPORT_ID, REPORT_ID);
		reportJson.put(ReportJson.REPORT_MODEL_TYPE, ModelType.Contractors.toString());

		JSONObject column = new JSONObject();
		column.put(ReportJson.REPORT_ELEMENT_NAME, "foo");

		JSONArray columns = new JSONArray();
		columns.add(column);
		reportJson.put(ReportJson.REPORT_COLUMNS, columns);

		JSONObject payloadJson = new JSONObject();
		payloadJson.put(ReportJson.LEVEL_REPORT, reportJson);

		return payloadJson;
	}

	private Answer<String> translate() {
		return new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String translation = translate((String) args[0]);
				if (translation == null) {
					translation =  "translation:" + Arrays.toString(args);
				}
				return translation;
			}
		};
	}

	private Answer<?> createQueryResults(final int rowsToReturn) {
		return new Answer<List<BasicDynaBean>>() {
			@Override
			public List<BasicDynaBean> answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				JSONObject passedInJson = (JSONObject) args[1];
				List<BasicDynaBean> queryResults = getTestDatabaseReportData();
				passedInJson.put(ReportJson.RESULTS_TOTAL, rowsToReturn);
				return queryResults;
			}
		};
	}

	private List<Map<String, String>> buildDatabaseData() {
		List<Map<String, String>> rows = new ArrayList<Map<String, String>>();

		Map<String, String> row = new HashMap<String, String>();
		row.put("AccountCountry", "US");
		row.put("AccountStatus", "Active");
		row.put("AccountID", "15160");
		row.put("AccountCountrySubdivision", "US-TX");
		row.put("AccountName", "007 Pest Control Inc.");
		row.put("AccountCity", "Pasadena");
		row.put("ContractorMembershipDate", "2012-11-28");
		rows.add(row);

		row = new HashMap<String, String>();
		row.put("AccountCountry", "CA");
		row.put("AccountStatus", "Active");
		row.put("AccountID", "32054");
		row.put("AccountCountrySubdivision", "CA-BC");
		row.put("AccountName", "0798909 BC Ltd");
		row.put("AccountCity", "Kamloops");
		row.put("ContractorMembershipDate", "2012-06-18");
		rows.add(row);

		row = new HashMap<String, String>();
		row.put("AccountCountry", "CA");
		row.put("AccountStatus", "Active");
		row.put("AccountID", "29298");
		row.put("AccountCountrySubdivision", "CA-BC");
		row.put("AccountName", "0841332 B.C. LTD.");
		row.put("AccountCity", "ABBOTSFORD");
		row.put("ContractorMembershipDate", "2012-03-29");
		rows.add(row);

		return rows;
	}

	private String translate(String key) {
		Map<String, String> translation = new HashMap<String, String>() {{
			put("CountrySubdivision.US-TX", "Texas");
			put("CountrySubdivision.CA-BC", "British Columbia");
			put("Country.US", "United States");
			put("Country.CA", "Canada");
			put("AccountStatus.Active", "Active");
		}};
		return translation.get(key);
	}

	private ArrayList<BasicDynaBean> getTestDatabaseReportData() {
		ArrayList<BasicDynaBean> basicDynaBeans = new ArrayList<BasicDynaBean>();

		RowSetDynaClass rowSetDynaClass = mock(RowSetDynaClass.class);
		when(rowSetDynaClass.getDynaProperty(anyString())).thenReturn(new DynaProperty("foo"));

		for (Map<String, String> row : buildDatabaseData()) {
			BasicDynaBean basicDynaBean = new BasicDynaBean(rowSetDynaClass);
			for (String columnName : row.keySet()) {
				String columnValue = row.get(columnName);
				basicDynaBean.set(columnName, columnValue);
			}
			basicDynaBeans.add(basicDynaBean);
		}

		return basicDynaBeans;
	}

	private JSONArray getExpectedResultData() {
		JSONArray resultDataArray = new JSONArray();

		for (Map<String, String> row : buildDatabaseData()) {
			JSONObject jsonObject = new JSONObject();
			for (String columnName : row.keySet()) {
				String columnValue = row.get(columnName);
				columnValue = translateColumnValueIfNecessary(columnName, columnValue);
				jsonObject.put(columnName, columnValue);
			}
			resultDataArray.add(jsonObject);
		}

		return resultDataArray;
	}

	private String translateColumnValueIfNecessary(String columnName, String columnValue) {
		if (columnName.equals("AccountCountry")) {
			columnValue = translate("Country." + columnValue);
		}
		if (columnName.equals("AccountStatus")) {
			columnValue = translate("AccountStatus." + columnValue);
		}
		if (columnName.equals("AccountCountrySubdivision")) {
			columnValue = translate("CountrySubdivision." + columnValue);
		}
		return columnValue;
	}

	private List<Column> getReportColumns() {
		List<Column> columns = new ArrayList<Column>();
		Column column = new Column();
		column.setName("AccountName");
		Field field = new Field("AccountName");
		field.setType(FieldType.String);
		field.setPreTranslation("");
		field.setUrl("ContractorView.action?id={AccountID}");
		column.setField(field);
		columns.add(column);

		column = new Column();
		column.setName("AccountStatus");
		field = new Field("AccountStatus");
		field.setType(FieldType.AccountStatus);
		field.setPreTranslation("AccountStatus");
		column.setField(field);
		columns.add(column);

		column = new Column();
		column.setName("ContractorMembershipDate");
		field = new Field("ContractorMembershipDate");
		field.setType(FieldType.Date);
		field.setPreTranslation("");
		column.setField(field);
		columns.add(column);

		column = new Column();
		column.setName("AccountCity");
		field = new Field("AccountCity");
		field.setType(FieldType.String);
		field.setPreTranslation("");
		column.setField(field);
		columns.add(column);

		column = new Column();
		column.setName("ContractorPayingFacilities");
		field = new Field("ContractorPayingFacilities");
		field.setType(FieldType.String);
		field.setPreTranslation("");
		column.setField(field);
		columns.add(column);

		column = new Column();
		column.setName("AccountCountrySubdivision");
		field = new Field("AccountCountrySubdivision");
		field.setType(FieldType.CountrySubdivision);
		field.setPreTranslation("CountrySubdivision");
		column.setField(field);
		columns.add(column);

		column = new Column();
		column.setName("AccountCountry");
		field = new Field("AccountCountry");
		field.setType(FieldType.Country);
		field.setPreTranslation("Country");
		column.setField(field);
		columns.add(column);

		return columns;
	}

	private SelectSQL getTestSelectSql() {
		String fromTable = "contractor_info AS Contractor";
		String whereClause = "(true) AND (true)";
		SelectSQL selectSQL = new SelectSQL(fromTable, whereClause);
		selectSQL.addField("TRIM(Account.name) AS `AccountName`");
		selectSQL.addField("Account.id AS `AccountID`");
		selectSQL.addField("Account.status AS `AccountStatus`");
		selectSQL.addField("Contractor.membershipdate AS `ContractorMembershipDate`");
		selectSQL.addField("Account.city AS `AccountCity`");
		selectSQL.addField("Account.countrysubdivision AS `AccountCountrySubdivision`");
		selectSQL.addField("Account.country AS `AccountCountry`");
		selectSQL.setSQL_CALC_FOUND_ROWS(true);
		selectSQL.setLimit(0);
		selectSQL.addJoin("JOIN accounts AS Account ON Contractor.id = Account.id AND Account.type = 'Contractor'");
		selectSQL.addOrderBy("AccountName");
		return selectSQL;
	}

	private String getExpectedResultsSql() {
		return "SELECT SQL_CALC_FOUND_ROWS TRIM(Account.name) AS `AccountName`, Account.id AS `AccountID`, Account.status AS `AccountStatus`, Contractor.membershipdate AS `ContractorMembershipDate`, Account.city AS `AccountCity`, Account.countrysubdivision AS `AccountCountrySubdivision`, Account.country AS `AccountCountry` FROM contractor_info AS Contractor JOIN accounts AS Account ON Contractor.id = Account.id AND Account.type = 'Contractor' WHERE ((true) AND (true)) ORDER BY AccountName LIMIT 0";
	}


}
