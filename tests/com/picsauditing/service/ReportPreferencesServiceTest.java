package com.picsauditing.service;


import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.NoResultException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class ReportPreferencesServiceTest {

	private ReportPreferencesService reportPreferencesService;

	@Mock
	private ReportDAO reportDao;
	@Mock
	private ReportUserDAO reportUserDao;
	@Mock
	private User user;
	@Mock
	private Report report;
	@Mock
	private ReportInfoProvider reportInfoProvider;

	private static final int USER_ID = 23;
	private static final int REPORT_ID = 29;
	private static final int MAX_SORT_ORDER = 10;
	private static final int MAX_FAVORITE_COUNT = 15;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportPreferencesService = new ReportPreferencesService();

		setInternalState(reportPreferencesService, "reportDao", reportDao);
		setInternalState(reportPreferencesService, "reportUserDao", reportUserDao);
		setInternalState(reportPreferencesService, "reportInfoProvider", reportInfoProvider);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);

	}

	@Test
	public void testLoadOrCreateReportUser_createNewReportUserIfNotFound() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.findById(REPORT_ID)).thenReturn(report);

		ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(USER_ID, REPORT_ID);

		verify(reportUserDao).save(reportUser);
		assertEquals(REPORT_ID, reportUser.getReport().getId());
		assertEquals(USER_ID, reportUser.getUser().getId());
		assertFalse(reportUser.isFavorite());
	}


	@Test
	public void testFavoriteReport_shouldSetFavoriteFlag() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);

		ReportUser result = reportPreferencesService.favoriteReport(reportUser);

		assertTrue(result.isFavorite());
	}

	@Test
	public void testFavoriteReport_newlyFavoritedReportShouldHaveHighestSortOrder() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findMaxSortIndex(USER_ID)).thenReturn(MAX_SORT_ORDER);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);

		ReportUser result = reportPreferencesService.favoriteReport(reportUser);

		assertEquals(MAX_SORT_ORDER + 1, result.getSortOrder());
	}

	@Test
	public void testUnfavoriteReport_unfavoritedReportShouldBeRemoved() throws Exception {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportUser);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);

		ReportUser result = reportPreferencesService.unfavoriteReport(reportUser);

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

		ReportUser result = reportPreferencesService.moveFavoriteUp(reportUser);

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

		ReportUser result = reportPreferencesService.moveFavoriteDown(reportUser);

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

	@Test
	public void testBuildFavorites_whenSortOrderIsContiguous_doNotReIndexSortOrder() throws Exception {
		List<ReportInfo> favoritesForUser = buildSortedFavoritesThatAreContiguous(USER_ID);
		when(reportInfoProvider.findAllFavoriteReports(USER_ID)).thenReturn(favoritesForUser);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		favoritesForUser = reportPreferencesService.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportInfoProvider, never()).updateSortOrder(any(ReportInfo.class), anyInt());
	}

	@Test
	public void testBuildFavorites_whenMaxSortOrderIsGreaterThanListSize_reIndexSortOrder() throws Exception {
		List<ReportInfo> favoritesForUser = buildSortedFavoritesThatAreNotContiguous(USER_ID);
		when(reportInfoProvider.findAllFavoriteReports(USER_ID)).thenReturn(favoritesForUser);

		assertEquals(4, favoritesForUser.size());
		assertEquals(7, favoritesForUser.get(0).getSortOrder());
		assertEquals(5, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		favoritesForUser = reportPreferencesService.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportInfoProvider).updateSortOrder(favoritesForUser.get(0), USER_ID);
		verify(reportInfoProvider).updateSortOrder(favoritesForUser.get(1), USER_ID);
		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(2), USER_ID);
		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(3), USER_ID);
	}

	@Test
	public void testBuildFavorites_whenSortOrderIsNotContiguous_reIndexSortOrder() throws Exception {

		List<ReportInfo> favoritesForUser = buildSortedFavoritesThatAreNotContiguous(USER_ID);
		when(reportInfoProvider.findAllFavoriteReports(USER_ID)).thenReturn(favoritesForUser);

		assertEquals(4, favoritesForUser.size());
		assertEquals(7, favoritesForUser.get(0).getSortOrder());
		assertEquals(5, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		favoritesForUser = reportPreferencesService.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportInfoProvider).updateSortOrder(favoritesForUser.get(0), USER_ID);
		verify(reportInfoProvider).updateSortOrder(favoritesForUser.get(1), USER_ID);
		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(2), USER_ID);
		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(3), USER_ID);
	}

	@Test
	public void testBuildFavorites_whenSortOrderHasDuplicates_reIndexSortOrder() throws Exception {

		List<ReportInfo> favoritesForUser = buildSortedFavoritesThatHaveDuplicates(USER_ID);
		when(reportInfoProvider.findAllFavoriteReports(USER_ID)).thenReturn(favoritesForUser);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(2, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		favoritesForUser = reportPreferencesService.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(0), USER_ID);
		verify(reportInfoProvider).updateSortOrder(favoritesForUser.get(1), USER_ID);
		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(2), USER_ID);
		verify(reportInfoProvider, never()).updateSortOrder(favoritesForUser.get(3), USER_ID);
	}

	@SuppressWarnings("serial")
	private List<ReportInfo> buildSortedFavoritesThatAreContiguous(final int userId) {
		final Report report = new Report();
		List<ReportInfo> favorites = new ArrayList<ReportInfo>() {{
			add(createFavoriteWithSortIndex(userId, report, 4));
			add(createFavoriteWithSortIndex(userId, report, 3));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 1));
		}};
		return favorites;
	}

	@SuppressWarnings("serial")
	private List<ReportInfo> buildSortedFavoritesThatAreNotContiguous(final int userId) {
		final Report report = new Report();
		List<ReportInfo> favorites = new ArrayList<ReportInfo>() {{
			add(createFavoriteWithSortIndex(userId, report, 7));
			add(createFavoriteWithSortIndex(userId, report, 5));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 1));
		}};

		return favorites;
	}

	@SuppressWarnings("serial")
	private List<ReportInfo> buildSortedFavoritesThatHaveDuplicates(final int userId) {
		final Report report = new Report();
		List<ReportInfo> favorites = new ArrayList<ReportInfo>() {{
			add(createFavoriteWithSortIndex(userId, report, 4));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 1));
		}};

		return favorites;
	}

	private ReportInfo createFavoriteWithSortIndex(int userId, Report report, int sortOrder) {
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setFavorite(true);
		reportInfo.setSortOrder(sortOrder);

		return reportInfo;
	}

}
