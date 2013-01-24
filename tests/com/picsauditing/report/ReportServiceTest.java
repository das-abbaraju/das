package com.picsauditing.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.*;

import javax.persistence.NoResultException;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.JSONUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.report.converter.LegacyReportConverter;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.pagination.Pagination;

public class ReportServiceTest {

	private ReportService reportService;

	@Mock
	private ReportDAO reportDao;
	@Mock
	private ReportUserDAO reportUserDao;
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
	private FeatureToggle featureToggle;
	@Mock
	private ReportService reportServiceMock;
	@Mock
	private ReportContext reportContext;

	private Pagination<Report> pagination;
	private LegacyReportConverter legacyReportConverter;

	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportService = new ReportService();
		legacyReportConverter = new LegacyReportConverter();

		setInternalState(reportService, "reportDao", reportDao);
		setInternalState(reportService, "reportUserDao", reportUserDao);
		setInternalState(reportService, "reportPermissionUserDao", reportPermissionUserDao);
		setInternalState(reportService, "reportPermissionAccountDao", reportPermissionAccountDao);
		setInternalState(reportService, "featureToggle", featureToggle);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);
		when(account.getId()).thenReturn(ACCOUNT_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(featureToggle.isFeatureEnabled(anyString())).thenReturn(true);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_ThrowsExceptionIfNullReport() throws ReportValidationException {
		Report report = null;

		reportService.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_ThrowsExceptionIfNullModelType() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(null);

		reportService.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_ThrowsExceptionIfInvalidReportParameters() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		report.setParameters("NOT_A_REPORT");

		reportService.validate(report);
	}

//	@Test(expected = ReportValidationException.class)
//	public void testValidate_MissingColumns() throws ReportValidationException {
//		Report report = new Report();
//		report.setModelType(ModelType.Accounts);
//		report.setParameters("{}");
//		reportService.legacyConvertParametersToReport(report);
//
//		ReportService.validate(report);
//	}
//
//	@Test(expected = ReportValidationException.class)
//	public void testLegacyConvertParametersToReport_NullReportParametersThrowsError() throws ReportValidationException {
//		Report report = new Report();
//		report.setParameters(null);
//		reportService.legacyConvertParametersToReport(report);
//	}

	@Test
	public void testGetReportAccessesForSearch_NullSearchTermCallsTopTenFavorites() {
		List<Report> reports = reportService.getReportsForSearch(null, permissions, pagination);
		Set<Integer> set = new HashSet<Integer>();
		set.add(1294);

		assertNotNull(reports);
		verify(reportUserDao).findTenMostFavoritedReports(permissions);
	}

	@Test
	public void testGetReportAccessesForSearch_BlankSearchTermCallsTopTenFavorites() {
		List<Report> reports = reportService.getReportsForSearch("", permissions, pagination);
		Set<Integer> set = new HashSet<Integer>();
		set.add(1294);

		assertNotNull(reports);
		verify(reportUserDao).findTenMostFavoritedReports(permissions);
	}

	// TODO add new tests for paging
	// @Test
	// public void
	// testGetReportUsersForSearch_ValidSearchTermCallsFindReportsForSearchFilter()
	// {
	// List<ReportUser> reportUsers =
	// reportService.getReportUsersForSearch("SEARCH_TERM", 0);
	//
	// assertNotNull(reportUsers);
	// verify(reportUserDao).findAllForSearchFilter(anyInt(), anyString());
	// }

	// TODO add new tests for paging
	// @Test
	// public void
	// testGetReportUsersForSearch_ValidSearchTermCallsFindReportsForSearchFilter()
	// {
	// List<ReportUser> reportUsers =
	// reportService.getReportUsersForSearch("SEARCH_TERM", 0);
	//
	// assertNotNull(reportUsers);
	// verify(reportUserDao).findAllForSearchFilter(anyInt(), anyString());
	// }

	// TODO add new tests for paging
	// @Test
	// public void
	// testGetReportUsersForSearch_ValidSearchTermCallsFindReportsForSearchFilter()
	// {
	// List<ReportUser> reportUsers =
	// reportService.getReportUsersForSearch("SEARCH_TERM", 0);
	//
	// assertNotNull(reportUsers);
	// verify(reportUserDao).findAllForSearchFilter(anyInt(), anyString());
	// }

	// TODO add new tests for paging
	// @Test
	// public void
	// testGetReportUsersForSearch_ValidSearchTermCallsFindReportsForSearchFilter()
	// {
	// List<ReportUser> reportUsers =
	// reportService.getReportUsersForSearch("SEARCH_TERM", 0);
	//
	// assertNotNull(reportUsers);
	// verify(reportUserDao).findAllForSearchFilter(anyInt(), anyString());
	// }

	@Ignore
	@Test
	public void testUnfavorite_TopReport() {
		// TODO fixed this bug, need a test to verify behavior
	}

	@Ignore
	@Test
	public void testUnfavorite_BottomReport() {
		// TODO another important edge case
	}

	@Test
	public void testConnectReportUser() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);
		ReportUser reportUser = reportService.connectReportUser(USER_ID, REPORT_ID);

		verify(reportUserDao).save(reportUser);
		assertEquals(REPORT_ID, reportUser.getReport().getId());
		assertEquals(USER_ID, reportUser.getUser().getId());
		assertFalse(reportUser.isFavorite());
	}

	@Test
	public void testConnectReportPermissionUser() {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);
		ReportPermissionUser reportPermissionUser = reportService.connectReportPermissionUser(permissions, USER_ID, REPORT_ID, false);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertFalse(reportPermissionUser.isEditable());
	}

	@Test
	public void testConnectReportPermissionUserEditable() {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);
		ReportPermissionUser reportPermissionUser = reportService.connectReportPermissionUser(permissions, USER_ID, REPORT_ID, true);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertTrue(reportPermissionUser.isEditable());
	}

	@Test
	public void testConnectReportPermissionAccount() {
		when(reportPermissionAccountDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);
		ReportPermissionAccount reportPermissionAccount = reportService.connectReportPermissionAccount(ACCOUNT_ID, REPORT_ID, EntityFactory.makePermission());

		verify(reportPermissionAccountDao).save(reportPermissionAccount);
		assertEquals(REPORT_ID, reportPermissionAccount.getReport().getId());
		assertEquals(ACCOUNT_ID, reportPermissionAccount.getAccount().getId());
	}

	@Test
	public void testCreateReport_ReportShouldAlwaysValidate() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		ReportService reportServiceSpy = spy(reportService);
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report report = reportServiceSpy.createReport(reportContext);

		verify(reportServiceSpy).validate(report);
	}

	@Test
	public void testCreateReport_WhenJsonIsPassedInAndIncludeDataIsTrue_LoadFromJson() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);

		Report resultReport = reportService.createReport(reportContext);

		assertEquals(REPORT_ID, resultReport.getId());
	}

	@Test
	public void testCreateReport_WhenIncludeDataIsFalse_HitTheDb() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, false, false, false, 0, 0);
		when(reportDao.findById(anyInt())).thenReturn(report);
		legacyReportConverter = mock(LegacyReportConverter.class);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);

		Report resultReport = reportService.createReport(reportContext);

		assertEquals(report, resultReport);
	}

	@Test
	public void testCreateReport_WhenReportJsonIsEmpty_HitTheDb() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		when(reportDao.findById(anyInt())).thenReturn(report);
		legacyReportConverter = mock(LegacyReportConverter.class);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);

		Report resultReport = reportService.createReport(reportContext);

		assertEquals(report, resultReport);
	}

	@Test
	public void testCreateReport_WhenReportIsLoadedFromDb_ReportPropertiesAreNotMutated() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		Report report = buildBasicLegacyReport();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createReport(reportContext);

		assertEquals(report.getId(), resultReport.getId());
		assertEquals(report.getModelType(), resultReport.getModelType());
		assertEquals(report.getName(), resultReport.getName());
		assertEquals(report.getNumTimesFavorited(), resultReport.getNumTimesFavorited());
		assertEquals(report.getParameters(), resultReport.getParameters());
		assertEquals(report.getDescription(), resultReport.getDescription());
		assertEquals(report.getSql(), resultReport.getSql());
		assertEquals(report.getFilterExpression(), resultReport.getFilterExpression());
		assertEquals(report.isEditable(), resultReport.isEditable());
		assertEquals(report.isFavorite(), resultReport.isFavorite());
	}

	@Test
	public void testCreateReport_WhenReportIsLoadedFromDb_ReportElementsShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		Report report = buildBasicLegacyReport();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createReport(reportContext);

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
	public void testCreateReport_WhenReportIsLoadedFromDb_FiltersShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		Report report = buildBasicLegacyReport();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createReport(reportContext);

		List<Filter> filters = resultReport.getFilters();
		assertEquals(2, filters.size());
		Map<String, Filter> resultFilterMap = createReportElementMap(filters);

		verifyFilter("AccountName", QueryFilterOperator.Contains, JSONUtilities.EMPTY_JSON_ARRAY, resultFilterMap);
		verifyFilter("AccountStatus", QueryFilterOperator.In, "[Active, Pending]", resultFilterMap);
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

	private <T extends ReportElement> Map<String, T> createReportElementMap(List<T> reportElements) {
		Map<String, T> elementMap = new HashMap<String, T>();
		for (T reportElement : reportElements) {
			elementMap.put(reportElement.getName(), reportElement);
		}
		return elementMap;
	}

	// A legacy report contains parameters(json), which are used to load reportElements, filters, and sorts.
	private Report buildBasicLegacyReport() {
		Report report = new Report();
		report.setId(100);
		report.setModelType(ModelType.Contractors);
		report.setName("fooReport");
		report.setNumTimesFavorited(5);
		report.setParameters(getParameterJson());
		report.setDescription("A basic report for testing");
		report.setSql("select * from dual");
		report.setFilterExpression("where somecolumn is 'foo'");
		report.setEditable(false);
		report.setFavorite(false);
		return report;
	}

	private String getParameterJson() {
		return "{\"id\":1,\"modelType\":\"Contractors\",\"name\":\"Contractor List\",\"description\":\"Default Contractor List for PICS Employees\",\"filterExpression\":\"\",\"rowsPerPage\":50,\"columns\":[{\"name\":\"AccountName\"},{\"name\":\"AccountStatus\"},{\"name\":\"ContractorMembershipDate\"},{\"name\":\"AccountCity\"},{\"name\":\"ContractorPayingFacilities\"},{\"name\":\"AccountCountrySubdivision\"},{\"name\":\"AccountCountry\"}],\"filters\":[{\"name\":\"AccountName\",\"operator\":\"Contains\"},{\"name\":\"AccountStatus\",\"operator\":\"In\",\"value\":\"Active, Pending\"}],\"sorts\":[{\"name\":\"AccountName\",\"direction\":\"ASC\"}]}";
	}

	private Report buildBasicReport() {
		Report report = new Report();
		report.setId(100);
		report.setModelType(ModelType.Contractors);
		report.setName("fooReport");
		report.setNumTimesFavorited(5);
		report.setParameters("{\"randomjson\":\"this is some json\"}");
		report.setDescription("A basic report for testing");
		report.setSql("select * from dual");
		report.setFilterExpression("where somecolumn is 'foo'");
		report.setEditable(false);
		report.setFavorite(false);

		Column column = new Column();
		List<Column> columns = new ArrayList<Column>();
		columns.add(column);
		report.setColumns(columns);
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

}
