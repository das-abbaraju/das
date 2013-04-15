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
	private ReportInfoConverter reportInfoConverter;

	private static final int USER_ID = 23;
	private static final int REPORT_ID = 29;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportPreferencesService = new ReportPreferencesService();

		setInternalState(reportPreferencesService, "reportDao", reportDao);
		setInternalState(reportPreferencesService, "reportUserDao", reportUserDao);
		setInternalState(reportPreferencesService, "reportInfoConverter", reportInfoConverter);

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
	public void testFavoriteReport_WhenReportIsNewlyFavorited_AndNoPinnedReports_ThenItShouldHaveHighestSortOrder() throws SQLException {
		ReportUser reportUser = createTestReportUser();
		reportUser.setFavorite(false);
		when(reportUserDao.save(reportUser)).thenReturn(reportUser);
		List<ReportUser> unpinnedFavorites = createTestUnpinnedFavorites();
		when(reportUserDao.findUnpinnedFavorites(USER_ID)).thenReturn(unpinnedFavorites);

		ReportUser result = reportPreferencesService.favoriteReport(reportUser);

		assertEquals(unpinnedFavorites.size(), result.getSortOrder());
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
	public void testMoveUnpinnedFavoriteUp_WhenCanBeMoved_ThenGetsSortOrderOfNextHighestSortOrder() throws Exception {
		ReportUser movingReport = createTestReportUser();
		int beforeSortOrder = 3;
		movingReport.setSortOrder(beforeSortOrder);
		movingReport.setFavorite(true);

		List<ReportUser> unpinnedWithNextHighestSortOrder = new ArrayList<>();
		ReportUser replacedReport = new ReportUser();
		int replacedReportSortOrder = 5;
		replacedReport.setSortOrder(replacedReportSortOrder);
		unpinnedWithNextHighestSortOrder.add(replacedReport);
		when(reportUserDao.findUnpinnedWithNextHighestSortOrder(movingReport)).thenReturn(unpinnedWithNextHighestSortOrder);

		ReportUser result = reportPreferencesService.moveUnpinnedFavoriteUp(movingReport);

		assertEquals(replacedReportSortOrder, result.getSortOrder());
	}

	@Test
	public void testMoveUnpinnedFavoriteDown_WhenCanBeMoved_ThenGetsSortOrderOfNextLowestSortOrder() throws Exception {
		ReportUser movingReport = createTestReportUser();
		int beforeSortOrder = 3;
		movingReport.setSortOrder(beforeSortOrder);
		movingReport.setFavorite(true);

		List<ReportUser> unpinnedWithNextHighestSortOrder = new ArrayList<>();
		ReportUser replacedReport = new ReportUser();
		int replacedReportSortOrder = 1;
		replacedReport.setSortOrder(replacedReportSortOrder);
		unpinnedWithNextHighestSortOrder.add(replacedReport);
		when(reportUserDao.findUnpinnedWithNextLowestSortOrder(movingReport)).thenReturn(unpinnedWithNextHighestSortOrder);

		ReportUser result = reportPreferencesService.moveUnpinnedFavoriteDown(movingReport);

		assertEquals(replacedReportSortOrder, result.getSortOrder());
	}

	@Test
	public void testReIndexSortOrder_WhenSortOrderIsContiguous_ThenDontReIndexSortOrder() {
		List<ReportUser> favoritesForUser = buildSortedFavoritesThatAreContiguous(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		List<ReportUser> reIndexedFavorites = reportPreferencesService.reIndexSortOrder(favoritesForUser);

		assertEquals(4, reIndexedFavorites.size());
		assertEquals(4, reIndexedFavorites.get(0).getSortOrder());
		assertEquals(3, reIndexedFavorites.get(1).getSortOrder());
		assertEquals(2, reIndexedFavorites.get(2).getSortOrder());
		assertEquals(1, reIndexedFavorites.get(3).getSortOrder());
	}

	@Test
	public void testReIndexSortOrder_WhenMaxSortOrderIsGreaterThanListSize_ThenReIndexSortOrder() {
		List<ReportUser> favoritesForUser = buildSortedFavoritesThatAreNotContiguous(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(7, favoritesForUser.get(0).getSortOrder());
		assertEquals(5, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		List<ReportUser> reIndexedFavorites = reportPreferencesService.reIndexSortOrder(favoritesForUser);

		assertEquals(4, reIndexedFavorites.size());
		assertEquals(4, reIndexedFavorites.get(0).getSortOrder());
		assertEquals(3, reIndexedFavorites.get(1).getSortOrder());
		assertEquals(2, reIndexedFavorites.get(2).getSortOrder());
		assertEquals(1, reIndexedFavorites.get(3).getSortOrder());
	}

	@Test
	public void testReIndexSortOrder_WhenSortOrderIsNotContiguous_ThenReIndexSortOrder() {
		List<ReportUser> favoritesForUser = buildSortedFavoritesThatAreNotContiguous(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(7, favoritesForUser.get(0).getSortOrder());
		assertEquals(5, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		List<ReportUser> reIndexedFavorites = reportPreferencesService.reIndexSortOrder(favoritesForUser);

		assertEquals(4, reIndexedFavorites.size());
		assertEquals(4, reIndexedFavorites.get(0).getSortOrder());
		assertEquals(3, reIndexedFavorites.get(1).getSortOrder());
		assertEquals(2, reIndexedFavorites.get(2).getSortOrder());
		assertEquals(1, reIndexedFavorites.get(3).getSortOrder());
	}

	@Test
	public void testReIndexSortOrder_WhenSortOrderHasDuplicates_ThenReIndexSortOrder() {
		List<ReportUser> favoritesForUser = buildSortedFavoritesThatHaveDuplicates(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(2, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		List<ReportUser> reIndexedFavorites = reportPreferencesService.reIndexSortOrder(favoritesForUser);

		assertEquals(4, reIndexedFavorites.size());
		assertEquals(4, reIndexedFavorites.get(0).getSortOrder());
		assertEquals(3, reIndexedFavorites.get(1).getSortOrder());
		assertEquals(2, reIndexedFavorites.get(2).getSortOrder());
		assertEquals(1, reIndexedFavorites.get(3).getSortOrder());
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

	private List<ReportUser> createTestUnpinnedFavorites() {
		List<ReportUser> unpinnedFavorites = new ArrayList<>();

		for (int i = 0; i < 3; i += 1) {
			ReportUser reportUser = new ReportUser();
			reportUser.setSortOrder(3 - i);
			reportUser.setFavorite(true);

			unpinnedFavorites.add(reportUser);
		}

		return unpinnedFavorites;
	}

	@SuppressWarnings("serial")
	private List<ReportUser> buildSortedFavoritesThatAreContiguous(final int userId) {
		final Report report = new Report();
		List<ReportUser> favorites = new ArrayList<ReportUser>() {{
			add(createFavoriteWithSortIndex(userId, report, 4));
			add(createFavoriteWithSortIndex(userId, report, 3));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 1));
		}};
		return favorites;
	}

	@SuppressWarnings("serial")
	private List<ReportUser> buildSortedFavoritesThatAreNotContiguous(final int userId) {
		final Report report = new Report();
		List<ReportUser> favorites = new ArrayList<ReportUser>() {{
			add(createFavoriteWithSortIndex(userId, report, 7));
			add(createFavoriteWithSortIndex(userId, report, 5));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 1));
		}};

		return favorites;
	}

	@SuppressWarnings("serial")
	private List<ReportUser> buildSortedFavoritesThatHaveDuplicates(final int userId) {
		final Report report = new Report();
		List<ReportUser> favorites = new ArrayList<ReportUser>() {{
			add(createFavoriteWithSortIndex(userId, report, 4));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 2));
			add(createFavoriteWithSortIndex(userId, report, 1));
		}};

		return favorites;
	}

	private ReportUser createFavoriteWithSortIndex(int userId, Report report, int sortOrder) {
		ReportUser reportUser = new ReportUser();
		reportUser.setFavorite(true);
		reportUser.setSortOrder(sortOrder);

		return reportUser;
	}

}
