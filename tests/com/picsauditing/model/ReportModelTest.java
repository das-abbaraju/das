package com.picsauditing.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.List;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.pagination.Pagination;

public class ReportModelTest {

	private ReportModel reportModel;

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
	private Report report;
	@Mock
	private ReportPermissionUser reportPermissionUser;
	@Mock
	private Permissions permissions;

	private Pagination<Report> pagination;

	private final int REPORT_ID = 37;
	private final int USER_ID = 23;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportModel = new ReportModel();

		setInternalState(reportModel, "reportDao", reportDao);
		setInternalState(reportModel, "reportUserDao", reportUserDao);
		setInternalState(reportModel, "reportPermissionUserDao", reportPermissionUserDao);
		setInternalState(reportModel, "reportPermissionAccountDao", reportPermissionAccountDao);
		setInternalState(reportModel, "Permissions", permissions);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);
	}

	@Test
	public void mockUserIsMockedWithUserId() {
		assertEquals(USER_ID, user.getId());
	}

	@Test
	public void mockReportIsMockedWithReportId() {
		assertEquals(REPORT_ID, report.getId());
	}

	@Test
	public void canUserViewAndCopy_TrueIfPublicReport() {
		when(report.isPublic()).thenReturn(true);
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);

		assertTrue(reportModel.canUserViewAndCopy(permissions, REPORT_ID));
	}

	@Test
	public void canUserViewAndCopy_FalseIfPrivateReportNoAssociationWithUser() {
		when(report.isPublic()).thenReturn(false);
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(null);

		assertFalse(reportModel.canUserViewAndCopy(permissions, REPORT_ID));
	}

	@Test
	public void canUserViewAndCopy_TrueIfAssociationWithUser() {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(new ReportPermissionUser());

		assertTrue(reportModel.canUserViewAndCopy(permissions, REPORT_ID));
	}

	@Test
	public void canUserViewAndCopy_FalseIfNoResultException() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());

		assertFalse(reportModel.canUserViewAndCopy(permissions, REPORT_ID));
	}

	@Test
	public void canUserEdit_FalseIfNoResultException() {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());

		assertFalse(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserEdit_FalseIfNoEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(false);
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportPermissionUser);

		assertFalse(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserEdit_TrueIfEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(true);
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportPermissionUser);

		assertTrue(reportModel.canUserEdit(USER_ID, report));
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_ThrowsExceptionIfNullReport() throws ReportValidationException {
		Report report = null;

		ReportModel.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_ThrowsExceptionIfNullModelType() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(null);

		ReportModel.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_ThrowsExceptionIfInvalidReportParameters() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		report.setParameters("NOT_A_REPORT");

		ReportModel.validate(report);
	}

	@Test
	public void testValidate_ValidReportParameters() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		report.setParameters("{}");

		ReportModel.validate(report);
	}

	@Test
	public void testGetReportAccessesForSearch_NullSearchTermCallsTopTenFavorites() {
		List<Report> reports = reportModel.getReportsForSearch(null, permissions, pagination);

		assertNotNull(reports);
		verify(reportUserDao).findTenMostFavoritedReports(anyInt(), anyInt());
	}

	@Test
	public void testGetReportAccessesForSearch_BlankSearchTermCallsTopTenFavorites() {
		List<Report> reports = reportModel.getReportsForSearch("", permissions, pagination);

		assertNotNull(reports);
		verify(reportUserDao).findTenMostFavoritedReports(anyInt(), anyInt());
	}

	// TODO add new tests for paging
	// @Test
	// public void
	// testGetReportUsersForSearch_ValidSearchTermCallsFindReportsForSearchFilter()
	// {
	// List<ReportUser> reportUsers =
	// reportModel.getReportUsersForSearch("SEARCH_TERM", 0);
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
	// reportModel.getReportUsersForSearch("SEARCH_TERM", 0);
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
	// reportModel.getReportUsersForSearch("SEARCH_TERM", 0);
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
	// reportModel.getReportUsersForSearch("SEARCH_TERM", 0);
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
		ReportUser reportUser = reportModel.connectReportUser(report, USER_ID);

		verify(reportUserDao).save(reportUser);
		assertEquals(REPORT_ID, reportUser.getReport().getId());
		assertEquals(USER_ID, reportUser.getUser().getId());
		assertFalse(reportUser.isFavorite());
	}

	@Test
	public void testConnectReportPermissionUser() {
		ReportPermissionUser reportPermissionUser = reportModel.connectReportPermissionUser(report, USER_ID);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertFalse(reportPermissionUser.isEditable());
	}

	@Test
	public void testConnectReportUserEditable() {
		ReportPermissionUser reportPermissionUser = reportModel.connectReportPermissionUserEditable(report, USER_ID);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertTrue(reportPermissionUser.isEditable());
	}
}
