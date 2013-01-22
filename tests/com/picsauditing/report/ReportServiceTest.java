package com.picsauditing.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionAccount;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.report.ReportService;
import com.picsauditing.report.ReportValidationException;
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

	private Pagination<Report> pagination;

	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportService = new ReportService();

		LegacyReportConverter legacyReportConverter = new LegacyReportConverter();
		Whitebox.setInternalState(legacyReportConverter, "reportService", reportServiceMock);

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

	@Test
	public void mockUserIsMockedWithUserId() {
		assertEquals(USER_ID, user.getId());
	}

	@Test
	public void mockReportIsMockedWithReportId() {
		assertEquals(REPORT_ID, report.getId());
	}

	@Test
	public void canUserViewAndCopy_TrueIfAssociationWithUser() {
		// use make user so that it has an account
		User user = EntityFactory.makeUser();
		user.setId(USER_ID);
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(
				new ReportPermissionUser());

		assertTrue(reportService.canUserViewAndCopy(permissions, REPORT_ID));
	}

	@Ignore
	public void canUserViewAndCopy_FalseIfNoResultException() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());

		assertFalse(reportService.canUserViewAndCopy(EntityFactory.makePermission(), REPORT_ID));
	}

	@Test
	public void canUserEdit_FalseIfNoResultException() {
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(reportDao.findOne(UserGroup.class, "group.id = 77375 AND user.id = 23")).thenThrow(new NoResultException());

		assertFalse(reportService.canUserEdit(permissions, report));
	}

	@Test
	public void canUserEdit_FalseIfNoEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(false);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(reportPermissionUser);
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(reportDao.findOne(UserGroup.class, "group.id = 77375 AND user.id = " + USER_ID)).thenThrow(new NoResultException());

		assertFalse(reportService.canUserEdit(permissions, report));
	}

	@Test
	public void canUserEdit_TrueIfEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(true);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(reportPermissionUser);

		assertTrue(reportService.canUserEdit(permissions, report));
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
}
