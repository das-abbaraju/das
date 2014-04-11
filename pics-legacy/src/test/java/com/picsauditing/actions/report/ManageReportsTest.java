package com.picsauditing.actions.report;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.service.user.UserService;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserType;
import com.picsauditing.service.*;
import com.picsauditing.strutsutil.AjaxUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ManageReportsTest extends PicsTranslationTest {

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
	private HttpServletRequest httpRequest;
	@Mock
	private UserService userService;

	@Mock
	private User user;
	@Mock
	private Report report;

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
		when(httpRequest.getHeader(anyString())).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER_VALUE);
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
    public void testFavoritesList_UserTypePopulatedList() {
        when(permissions.getUserId()).thenReturn(USER_ID);
        Whitebox.setInternalState(manageReports, "userType", UserType.Engineering);
        Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

        when(reportPreferencesService.buildFavorites(USER_ID)).thenReturn(null);
        List<ReportUser> favorites = new ArrayList<>();
        favorites.add(new ReportUser());
        when(reportPreferencesService.addUserTypeFavorites(USER_ID, UserType.Engineering)).thenReturn(favorites);

        List<ReportFavoriteInfo> favoriteInfos = new ArrayList<>();
        favoriteInfos.add(new ReportFavoriteInfo());
        when(reportFavoriteInfoConverter.convert(favorites)).thenReturn(favoriteInfos);

        manageReports.favorites();

        List<ReportFavoriteInfo> favoritesList = Whitebox.getInternalState(manageReports, "reportFavoriteList");
        assertNotNull(favoritesList);
        assertEquals(1, favoritesList.size());
    }

    @Test
	public void testSearchList_AjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
        ReportUser reportUser = new ReportUser();
        reportUser.setUser(new User(USER_ID));
        reportUser.setReport(new Report());
        reportUsers.add(reportUser);
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER_VALUE);
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
		when(httpRequest.getHeader(anyString())).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER_VALUE);
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

	@SuppressWarnings("deprecation")
	private void setUpI18nCacheText() {
		when(translationService.hasKey(anyString(), eq(Locale.ENGLISH))).thenReturn(true);

		when(translationService.getText(eq("ManageReports.message.NoFavorites"), eq(Locale.ENGLISH), any()))
				.thenReturn("No Favorites.");
	}
}
