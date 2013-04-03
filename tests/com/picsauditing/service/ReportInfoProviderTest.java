package com.picsauditing.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;

public class ReportInfoProviderTest {

	ReportInfoProvider reportInfoProvider;

	private static final int USER_ID = 23;

	@Mock
	private ReportUserDAO reportUserDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportInfoProvider = new ReportInfoProvider();

		Whitebox.setInternalState(reportInfoProvider, "reportUserDAO", reportUserDAO);
	}

	@Test
	public void testBuildFavorites_whenSortOrderIsContiguous_doNotReIndexSortOrder() throws Exception {
		List<ReportUser> reportUsers = buildSortedFavoritesThatAreContiguous(USER_ID);
		when(reportUserDAO.findAllFavorite(USER_ID)).thenReturn(reportUsers);

		assertEquals(4, reportUsers.size());
		assertEquals(4, reportUsers.get(0).getSortOrder());
		assertEquals(3, reportUsers.get(1).getSortOrder());
		assertEquals(2, reportUsers.get(2).getSortOrder());
		assertEquals(1, reportUsers.get(3).getSortOrder());

		List<ReportInfo> favoritesForUser = reportInfoProvider.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportUserDAO, never()).save(any(ReportUser.class));
	}

	@Test
	public void testBuildFavorites_whenMaxSortOrderIsGreaterThanListSize_reIndexSortOrder() throws Exception {
		List<ReportUser> reportUsers = buildSortedFavoritesThatAreNotContiguous(USER_ID);
		when(reportUserDAO.findAllFavorite(USER_ID)).thenReturn(reportUsers);

		assertEquals(4, reportUsers.size());
		assertEquals(7, reportUsers.get(0).getSortOrder());
		assertEquals(5, reportUsers.get(1).getSortOrder());
		assertEquals(2, reportUsers.get(2).getSortOrder());
		assertEquals(1, reportUsers.get(3).getSortOrder());

		List<ReportInfo> favoritesForUser = reportInfoProvider.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportUserDAO).save(reportUsers.get(0));
		verify(reportUserDAO).save(reportUsers.get(1));
		verify(reportUserDAO, never()).save(reportUsers.get(2));
		verify(reportUserDAO, never()).save(reportUsers.get(3));
	}

	@Test
	public void testBuildFavorites_whenSortOrderIsNotContiguous_reIndexSortOrder() throws Exception {

		List<ReportUser> reportUsers = buildSortedFavoritesThatAreNotContiguous(USER_ID);
		when(reportUserDAO.findAllFavorite(USER_ID)).thenReturn(reportUsers);

		assertEquals(4, reportUsers.size());
		assertEquals(7, reportUsers.get(0).getSortOrder());
		assertEquals(5, reportUsers.get(1).getSortOrder());
		assertEquals(2, reportUsers.get(2).getSortOrder());
		assertEquals(1, reportUsers.get(3).getSortOrder());

		List<ReportInfo> favoritesForUser = reportInfoProvider.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportUserDAO).save(reportUsers.get(0));
		verify(reportUserDAO).save(reportUsers.get(1));
		verify(reportUserDAO, never()).save(reportUsers.get(2));
		verify(reportUserDAO, never()).save(reportUsers.get(3));
	}

	@Test
	public void testBuildFavorites_whenSortOrderHasDuplicates_reIndexSortOrder() throws Exception {

		List<ReportUser> reportUsers = buildSortedFavoritesThatHaveDuplicates(USER_ID);
		when(reportUserDAO.findAllFavorite(USER_ID)).thenReturn(reportUsers);

		assertEquals(4, reportUsers.size());
		assertEquals(4, reportUsers.get(0).getSortOrder());
		assertEquals(2, reportUsers.get(1).getSortOrder());
		assertEquals(2, reportUsers.get(2).getSortOrder());
		assertEquals(1, reportUsers.get(3).getSortOrder());

		List<ReportInfo> favoritesForUser = reportInfoProvider.buildFavorites(USER_ID);

		assertEquals(4, favoritesForUser.size());
		assertEquals(4, favoritesForUser.get(0).getSortOrder());
		assertEquals(3, favoritesForUser.get(1).getSortOrder());
		assertEquals(2, favoritesForUser.get(2).getSortOrder());
		assertEquals(1, favoritesForUser.get(3).getSortOrder());

		verify(reportUserDAO, never()).save(reportUsers.get(0));
		verify(reportUserDAO).save(reportUsers.get(1));
		verify(reportUserDAO, never()).save(reportUsers.get(2));
		verify(reportUserDAO, never()).save(reportUsers.get(3));
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
		ReportUser reportUser = new ReportUser(userId, report);
		reportUser.setFavorite(true);
		reportUser.setSortOrder(sortOrder);

		return reportUser;
	}

}
