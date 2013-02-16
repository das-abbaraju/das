package com.picsauditing.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.pagination.Pagination;

public class ManageReportsServiceTest {

	private ManageReportsService manageReportsService;

	@Mock
	private ReportUserDAO reportUserDao;
	@Mock
	private Permissions permissions;
	@Mock
	private Pagination<Report> reportPagination;

	private final int USER_ID = 23;
	private final int REPORT_ID = 29;

	private static final int MAX_SORT_ORDER = 10;
	private static final int MAX_FAVORITE_COUNT = 15;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		manageReportsService = new ManageReportsService();

		setInternalState(manageReportsService, "reportUserDao", reportUserDao);
	}

	@Test
	public void testFavoriteReport_shouldSetFavoriteFlag() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);

		ReportUser result = manageReportsService.favoriteReport(reportUser);

		assertTrue(result.isFavorite());
	}

	@Test
	public void testFavoriteReport_newlyFavoritedReportShouldHaveHighestSortOrder() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);

		ReportUser result = manageReportsService.favoriteReport(reportUser);

		assertEquals(MAX_SORT_ORDER + 1, result.getSortOrder());
	}

	@Test
	public void testUnfavoriteReport_unfavoritedReportShouldBeRemoved() throws Exception {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);

		ReportUser result = manageReportsService.unfavoriteReport(reportUser);

		assertFalse(result.isFavorite());
		assertEquals(0, result.getSortOrder());
	}

	@Test
	public void testMoveFavoriteUp() throws Exception {
		ReportUser reportUser = createTestReportUser();
		int beforeSortOrder = 3;
		reportUser.setSortOrder(beforeSortOrder);
		reportUser.setFavorite(true);
		when(reportUserDao.getFavoriteCount(USER_ID)).thenReturn(MAX_FAVORITE_COUNT);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);

		ReportUser result = manageReportsService.moveFavoriteUp(reportUser);

		assertEquals(beforeSortOrder + 1, result.getSortOrder());
		verify(reportUserDao).offsetSortOrderForRange(USER_ID, -1, 4, 4);
	}

	@Test
	public void testMoveFavoriteDown() throws Exception {
		ReportUser reportUser = createTestReportUser();
		int beforeSortOrder = 3;
		reportUser.setSortOrder(beforeSortOrder);
		reportUser.setFavorite(true);
		when(reportUserDao.getFavoriteCount(USER_ID)).thenReturn(MAX_FAVORITE_COUNT);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);

		ReportUser result = manageReportsService.moveFavoriteDown(reportUser);

		assertEquals(beforeSortOrder - 1, result.getSortOrder());
		verify(reportUserDao).offsetSortOrderForRange(USER_ID, 1, 2, 2);
	}

	@Test
	public void testGetReportAccessesForSearch_NullSearchTermCallsTopTenFavorites() {
		List<Report> reports = manageReportsService.getReportsForSearch(null, permissions, reportPagination);
		Set<Integer> set = new HashSet<Integer>();
		set.add(1294);

		assertNotNull(reports);
		verify(reportUserDao).findTenMostFavoritedReports(permissions);
	}

	@Test
	public void testGetReportAccessesForSearch_BlankSearchTermCallsTopTenFavorites() {
		List<Report> reports = manageReportsService.getReportsForSearch("", permissions, reportPagination);
		Set<Integer> set = new HashSet<Integer>();
		set.add(1294);

		assertNotNull(reports);
		verify(reportUserDao).findTenMostFavoritedReports(permissions);
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

}
