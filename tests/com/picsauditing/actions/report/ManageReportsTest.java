package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.picsauditing.access.UserService;
import com.picsauditing.service.ReportFavoriteInfoConverter;
import com.picsauditing.service.ReportPreferencesService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.service.ManageReportsService;
import com.picsauditing.service.ReportService;
import com.picsauditing.strutsutil.AjaxUtils;

public class ManageReportsTest {

	private ManageReports manageReports;

	private static final String NOT_AJAX_REQUEST_HEADER = "NOT_XmlHttpRequest";
	private static final int USER_ID = 37;

	@Mock
	private ReportService reportService;
	@Mock
	private ManageReportsService manageReportsService;
	@Mock
	private ReportPreferencesService reportPreferencesService;
	@Mock
	private ReportFavoriteInfoConverter reportFavoriteInfoConverter;
	@Mock
	private ReportDAO reportDao;
	@Mock
	private ReportUserDAO reportUserDao;
	@Mock
	private Permissions permissions;
	@Mock
	private I18nCache i18nCache;
	@Mock
	private HttpServletRequest httpRequest;
	@Mock
	private UserService userService;

	@Mock
	private User user;
	@Mock
	private Report report;

	@BeforeClass
	public static void setupClass() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mock(Database.class));
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		manageReports = new ManageReports();

		setUpI18nCacheText();

		Whitebox.setInternalState(manageReports, "reportService", reportService);
		Whitebox.setInternalState(manageReports, "manageReportsService", manageReportsService);
		Whitebox.setInternalState(manageReports, "reportPreferencesService", reportPreferencesService);
		Whitebox.setInternalState(manageReports, "userService", userService);
		Whitebox.setInternalState(manageReports, "reportFavoriteInfoConverter", reportFavoriteInfoConverter);
		Whitebox.setInternalState(manageReports, "reportDao", reportDao);
		when(permissions.getUserId()).thenReturn(USER_ID);
		Whitebox.setInternalState(manageReports, "permissions", permissions);
		Whitebox.setInternalState(manageReports, "i18nCache", i18nCache);
	}

	@Test
	public void testExecute_RedirectsToMyReportsByDefault() {
		List<ReportUser> reportUser = new ArrayList<ReportUser>();
		reportUser.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUser);

		String result = manageReports.execute();

		assertEquals("redirect", result);
	}

	@Test
	public void testFavoritesList_AjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.favorites();

		assertEquals("favoritesList", result);
	}

	@Test
	public void testFavoritesList_NotAjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn(NOT_AJAX_REQUEST_HEADER);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.favorites();

		assertEquals("favorites", result);
	}

	@Test
	public void testFavoritesList_DoesntLeaveReportUsersNull() {
		when(permissions.getUserId()).thenReturn(USER_ID);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);
		when(reportPreferencesService.buildFavorites(USER_ID)).thenReturn(null);

		manageReports.favorites();

		assertNotNull(Whitebox.getInternalState(manageReports, "reportFavoriteList"));
	}

	@Test
	public void testSearchList_AjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.search();

		assertEquals("searchList", result);
	}

	@Test
	public void testSearchList_NotAjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn(NOT_AJAX_REQUEST_HEADER);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.search();

		assertEquals("search", result);
	}

	@Test
	public void testSearchList_AjaxDoesntLeaveReportUsersNull() {
		when(reportUserDao.findAll(USER_ID)).thenReturn(null);
		when(httpRequest.getHeader(anyString())).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.search();

		assertNotNull(Whitebox.getInternalState(manageReports, "reportList"));
	}

	@Test
	public void testSearchList_NotAjaxDoesntLeaveReportUsersNull() {
		when(reportUserDao.findAll(USER_ID)).thenReturn(null);
		when(httpRequest.getHeader(anyString())).thenReturn(NOT_AJAX_REQUEST_HEADER);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.search();

		assertNotNull(Whitebox.getInternalState(manageReports, "reportList"));
	}

	@Test
	public void testTransferOwnership_CallsExpectedMethod() throws Exception {
		Whitebox.setInternalState(manageReports, "shareId", USER_ID);
		when(userService.loadUser(USER_ID)).thenReturn(user);
		int reportId = 123;
		Whitebox.setInternalState(manageReports, "reportId", reportId);
		when(reportService.loadReportFromDatabase(reportId)).thenReturn(report);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.transferOwnership();

		verify(manageReportsService).transferOwnership(null, user, report, permissions);
	}

	@Test
	public void testDeleteReport_CallsExpectedMethod() throws Exception {
		int reportId = 123;
		Whitebox.setInternalState(manageReports, "reportId", reportId);
		when(reportService.loadReportFromDatabase(reportId)).thenReturn(report);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.deleteReport();

		verify(manageReportsService).deleteReport(null, report, permissions);
	}

	private void setUpI18nCacheText() {
		when(i18nCache.hasKey(anyString(), eq(Locale.ENGLISH)))
		.thenReturn(true);

		when(i18nCache.getText(eq("ManageReports.message.NoFavorites"), eq(Locale.ENGLISH), any()))
		.thenReturn("No Favorites.");
	}
}
