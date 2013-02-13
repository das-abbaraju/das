package com.picsauditing.report;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.sql.SQLException;
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

import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.report.converter.LegacyReportConverter;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.service.PermissionService;
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
	private ReportContext reportContext;
	@Mock
	private PermissionService permissionService;

	private Pagination<Report> pagination;

	private LegacyReportConverter legacyReportConverter;
	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;
	private static final int MAX_SORT_ORDER = 10;
	private static final int MAX_FAVORITE_COUNT = 15;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportService = new ReportService();
		legacyReportConverter = new LegacyReportConverter();

		setInternalState(reportService, "reportDao", reportDao);
		setInternalState(reportService, "reportUserDao", reportUserDao);
		setInternalState(reportService, "reportPermissionUserDao", reportPermissionUserDao);
		setInternalState(reportService, "reportPermissionAccountDao", reportPermissionAccountDao);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);
		setInternalState(reportService, "permissionService", permissionService);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);
		when(account.getId()).thenReturn(ACCOUNT_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
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

	@Test
	public void testLoadOrCreateReportUser() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);

		ReportUser reportUser = reportService.loadOrCreateReportUser(USER_ID, REPORT_ID);

		verify(reportUserDao).save(reportUser);
		assertEquals(REPORT_ID, reportUser.getReport().getId());
		assertEquals(USER_ID, reportUser.getUser().getId());
		assertFalse(reportUser.isFavorite());
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

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReportFromPayload_WhenPayloadIsNull_ThenItThrowsIllegalArgumentException() throws IllegalArgumentException, ReportValidationException {
		JSONObject payloadJson = null;
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);

		reportService.createReportFromPayload(reportContext);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateReportFromPayload_WhenPayloadIsEmpty_ThenItThrowsIllegalArgumentException() throws IllegalArgumentException, ReportValidationException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);

		reportService.createReportFromPayload(reportContext);
	}

	@Test
	public void testCreateOrLoadReport_WhenJsonIsPassedInAndIncludeDataIsTrue_LoadFromJson() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(REPORT_ID, resultReport.getId());
	}

	@Test
	public void testCreateOrLoadReport_WhenIncludeDataIsFalse_HitTheDb() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = buildMinimalPayloadJson();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, false, false, false, 0, 0);
		when(reportDao.findById(anyInt())).thenReturn(report);
		legacyReportConverter = mock(LegacyReportConverter.class);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(report, resultReport);
	}

	@Test
	public void testCreateOrLoadReport_WhenReportJsonIsEmpty_HitTheDb() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		when(reportDao.findById(anyInt())).thenReturn(report);
		legacyReportConverter = mock(LegacyReportConverter.class);
		setInternalState(reportService, "legacyReportConverter", legacyReportConverter);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		assertEquals(report, resultReport);
	}

	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_ReportPropertiesAreNotMutated() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		mockBasicLegacyReport();
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
		assertEquals(report.isEditableBy(USER_ID), resultReport.isEditableBy(USER_ID));
		assertEquals(report.isFavoritedBy(USER_ID), resultReport.isFavoritedBy(USER_ID));
	}

	// TODO rework this test to work with a mocked report
	@Ignore
	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_ReportElementsShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		ReportContext reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		mockBasicLegacyReport();
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

	// TODO rework this test to work with a mocked report
	@Ignore
	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_FiltersShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		mockBasicLegacyReport();
		when(reportDao.findById(anyInt())).thenReturn(report);

		Report resultReport = reportService.createOrLoadReport(reportContext);

		List<Filter> filters = resultReport.getFilters();
		assertEquals(2, filters.size());
		Map<String, Filter> resultFilterMap = createReportElementMap(filters);

		verifyFilter("AccountName", QueryFilterOperator.Contains, JSONUtilities.EMPTY_JSON_ARRAY, resultFilterMap);
		verifyFilter("AccountStatus", QueryFilterOperator.In, "[Active, Pending]", resultFilterMap);
	}

	// TODO rework this test to work with a mocked report
	@Ignore
	@Test
	public void testCreateOrLoadReport_WhenReportIsLoadedFromDb_SortsShouldBeSet() throws ReportValidationException, RecordNotFoundException {
		JSONObject payloadJson = new JSONObject();
		reportContext = new ReportContext(payloadJson, REPORT_ID, null, null, false, true, false, false, 0, 0);
		mockBasicLegacyReport();
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

	@Test
	public void testFavoriteReport_shouldSetFavoriteFlag() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);

		ReportUser result = reportService.favoriteReport(USER_ID, REPORT_ID);

		assertTrue(result.isFavorite());
	}

	@Test
	public void testFavoriteReport_newlyFavoritedReportShouldHaveHighestSortOrder() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);

		ReportUser result = reportService.favoriteReport(USER_ID, REPORT_ID);

		assertEquals(MAX_SORT_ORDER + 1, result.getSortOrder());
	}

	@Test
	public void testUnfavoriteReport_unfavoritedReportShouldBeRemoved() throws Exception {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);

		ReportUser result = reportService.unfavoriteReport(USER_ID, REPORT_ID);

		assertFalse(result.isFavorite());
		assertEquals(0, result.getSortOrder());
	}

	@Test
	public void testMoveFavoriteUp() throws Exception {
		ReportUser reportUser = createTestReportUser();
		int beforeSortOrder = 3;
		reportUser.setSortOrder(beforeSortOrder);
		reportUser.setFavorite(true);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);
		when(reportUserDao.getFavoriteCount(USER_ID)).thenReturn(MAX_FAVORITE_COUNT);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);

		ReportUser result = reportService.moveFavoriteUp(USER_ID, REPORT_ID);

		assertEquals(beforeSortOrder + 1, result.getSortOrder());
		verify(reportUserDao).offsetSortOrderForRange(USER_ID, -1, 4, 4);
	}

	@Test
	public void testMoveFavoriteDown() throws Exception {
		ReportUser reportUser = createTestReportUser();
		int beforeSortOrder = 3;
		reportUser.setSortOrder(beforeSortOrder);
		reportUser.setFavorite(true);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);
		when(reportUserDao.getFavoriteCount(USER_ID)).thenReturn(MAX_FAVORITE_COUNT);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);

		ReportUser result = reportService.moveFavoriteDown(USER_ID, REPORT_ID);

		assertEquals(beforeSortOrder - 1, result.getSortOrder());
		verify(reportUserDao).offsetSortOrderForRange(USER_ID, 1, 2, 2);
	}

	private ReportUser createTestReportUser() {
		ReportUser reportUser = new ReportUser();

		Report report = new Report();
		report.setName("myReport");
		reportUser.setReport(report);

		User user = new User("Joe User");
		user.setId(USER_ID);
		reportUser.setUser(user);

		return reportUser;
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
	private Report mockBasicLegacyReport() {
		when(report.getId()).thenReturn(REPORT_ID);
		when(report.getModelType()).thenReturn(ModelType.Contractors);
		when(report.getName()).thenReturn("fooReport");
		when(report.getNumTimesFavorited()).thenReturn(5);
		when(report.getParameters()).thenReturn(getParameterJson());
		when(report.getDescription()).thenReturn("A basic report for testing");
		when(report.getSql()).thenReturn("select * from dual");
		when(report.getFilterExpression()).thenReturn("where somecolumn is 'foo'");
		when(report.isEditableBy(USER_ID)).thenReturn(false);
		when(report.isFavoritedBy(USER_ID)).thenReturn(false);

		return report;
	}

	private String getParameterJson() {
		return "{\"id\":1,\"modelType\":\"Contractors\",\"name\":\"Contractor List\",\"description\":\"Default Contractor List for PICS Employees\",\"filterExpression\":\"\",\"rowsPerPage\":50,\"columns\":[{\"name\":\"AccountName\"},{\"name\":\"AccountStatus\"},{\"name\":\"ContractorMembershipDate\"},{\"name\":\"AccountCity\"},{\"name\":\"ContractorPayingFacilities\"},{\"name\":\"AccountCountrySubdivision\"},{\"name\":\"AccountCountry\"}],\"filters\":[{\"name\":\"AccountName\",\"operator\":\"Contains\"},{\"name\":\"AccountStatus\",\"operator\":\"In\",\"value\":\"Active, Pending\"}],\"sorts\":[{\"name\":\"AccountName\",\"direction\":\"ASC\"}]}";
	}

	private Report mockBasicReport() {
		when(report.getId()).thenReturn(REPORT_ID);
		when(report.getModelType()).thenReturn(ModelType.Contractors);
		when(report.getName()).thenReturn("fooReport");
		when(report.getNumTimesFavorited()).thenReturn(5);
		when(report.getParameters()).thenReturn("{\"randomjson\":\"this is some json\"}");
		when(report.getDescription()).thenReturn("A basic report for testing");
		when(report.getSql()).thenReturn("select * from dual");
		when(report.getFilterExpression()).thenReturn("where somecolumn is 'foo'");
		when(report.isEditableBy(USER_ID)).thenReturn(false);
		when(report.isFavoritedBy(USER_ID)).thenReturn(false);

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

}
