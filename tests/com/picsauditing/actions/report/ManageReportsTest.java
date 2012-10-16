package com.picsauditing.actions.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.model.report.ReportModel;
import com.picsauditing.search.Database;
import com.picsauditing.strutsutil.AjaxUtils;

public class ManageReportsTest {

	ManageReports manageReports;

	@Mock private ReportModel reportModel;
	@Mock private ReportDAO reportDao;
	@Mock private ReportUserDAO reportUserDao;
	@Mock private Permissions permissions;
	@Mock private I18nCache i18nCache;
	@Mock private Database databaseForTesting;
	@Mock private HttpServletRequest httpRequest;

	private static final int USER_ID = 37;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(AjaxUtils.class);
		PowerMockito.mockStatic(ManageReports.class);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		manageReports = new ManageReports();

		setUpI18nCacheText();

		Whitebox.setInternalState(manageReports, "reportModel", reportModel);
		Whitebox.setInternalState(manageReports, "reportDao", reportDao);
		Whitebox.setInternalState(manageReports, "reportUserDao", reportUserDao);
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
		when(httpRequest.getHeader(anyString())).thenReturn("XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.favoritesList();

		assertEquals("favoritesList", result);
	}

	@Test
	public void testFavoritesList_NotAjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn("NOT_XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.favoritesList();

		assertEquals("favorites", result);
	}

	@Test
	public void testFavoritesList_DoesntLeaveReportUsersNull() {
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(null);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.favoritesList();

		assertNotNull(Whitebox.getInternalState(manageReports, "reportUserFavorites"));
	}

	@Test
	public void testMyReportsList_AjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn("XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.myReportsList();

		assertEquals("myReportsList", result);
	}

	@Test
	public void testMyReportsList_NotAjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn("NOT_XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.myReportsList();

		assertEquals("myReports", result);
	}

	@Test
	public void testMyReportsList_DoesntLeaveReportUsersNull() {
		when(reportUserDao.findAll(USER_ID)).thenReturn(null);
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.myReportsList();

		assertNotNull(Whitebox.getInternalState(manageReports, "reportPermissionUsers"));
	}

	@Test
	public void testSearchList_AjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn("XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.searchList();

		assertEquals("searchList", result);
	}

	@Test
	public void testSearchList_NotAjaxReturnsExpectedResult() {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();
		reportUsers.add(new ReportUser());
		when(reportUserDao.findAll(USER_ID)).thenReturn(reportUsers);
		when(httpRequest.getHeader(anyString())).thenReturn("NOT_XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		String result = manageReports.searchList();

		assertEquals("search", result);
	}

	@Test
	public void testSearchList_AjaxDoesntLeaveReportUsersNull() {
		when(reportUserDao.findAll(USER_ID)).thenReturn(null);
		when(httpRequest.getHeader(anyString())).thenReturn("XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.searchList();

		assertNotNull(Whitebox.getInternalState(manageReports, "reports"));
	}

	@Test
	public void testSearchList_NotAjaxDoesntLeaveReportUsersNull() {
		when(reportUserDao.findAll(USER_ID)).thenReturn(null);
		when(httpRequest.getHeader(anyString())).thenReturn("NOT_XmlHttpRequest");
		Whitebox.setInternalState(manageReports, "requestForTesting", httpRequest);

		manageReports.searchList();

		assertNotNull(Whitebox.getInternalState(manageReports, "reports"));
	}

	private void setUpI18nCacheText() {
		when(i18nCache.hasKey(anyString(), eq(Locale.ENGLISH)))
				.thenReturn(true);

		when(i18nCache.getText(eq("ManageReports.message.NoFavorites"), eq(Locale.ENGLISH), any()))
				.thenReturn("No Favorites.");
	}
}
