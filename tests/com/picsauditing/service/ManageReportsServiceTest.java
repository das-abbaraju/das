package com.picsauditing.service;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ReportDAO;
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
	private ReportDAO reportDao;
	@Mock
	private ReportUserDAO reportUserDao;
	@Mock
	private Permissions permissions;
	@Mock
	private Pagination<Report> reportPagination;
	@Mock
	private ReportPreferencesService reportPreferencesService;
	@Mock
	private PermissionService permissionService;

	private final int USER_ID = 23;

	private static final int MAX_SORT_ORDER = 10;
	private static final int MAX_FAVORITE_COUNT = 15;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		manageReportsService = new ManageReportsService();

		setInternalState(manageReportsService, "reportUserDao", reportUserDao);
		setInternalState(manageReportsService, "reportDao", reportDao);
		setInternalState(manageReportsService, "reportPreferencesService", reportPreferencesService);
		setInternalState(manageReportsService, "permissionService", permissionService);
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

	@Test
	public void testTransferOwnership_previousOwnerShouldRetainEditPermission() throws Exception {
		Report report = new Report();
		User fromOwner = new User("From Owner");
		report.setOwner(fromOwner);
		User toOwner = new User("To Owner");
		when(permissionService.canTransferOwnership(fromOwner, report, permissions)).thenReturn(true);

		manageReportsService.transferOwnership(fromOwner, toOwner, report, permissions);

		verify(reportPreferencesService).loadOrCreateReportUser(fromOwner.getId(), report.getId());
		verify(permissionService).grantEdit(fromOwner.getId(), report.getId());
	}

	@Test
	public void testTransferOwnership_newOwnerShouldBeSetOnReport() throws Exception {
		Report report = new Report();
		User fromOwner = new User("From Owner");
		report.setOwner(fromOwner);
		User toOwner = new User("To Owner");
		when(permissionService.canTransferOwnership(fromOwner, report, permissions)).thenReturn(true);

		Report resultReport = manageReportsService.transferOwnership(fromOwner, toOwner, report, permissions);

		assertNotSame(fromOwner, resultReport.getOwner());
		assertEquals(toOwner, resultReport.getOwner());
	}

	@Test
	public void testDeleteReport_anyoneWithPermissionCanDelete() throws Exception {
		Report report = new Report();
		User deleterUser = new User("Joe Owner");
		report.setOwner(deleterUser);
		when(permissionService.canUserDeleteReport(deleterUser, report, permissions)).thenReturn(true);

		Report resultReport = manageReportsService.deleteReport(deleterUser, report, permissions);

		assertTrue(DateBean.isToday(resultReport.getUpdateDate()));
		assertEquals(deleterUser, resultReport.getUpdatedBy());
		verify(reportDao).remove(resultReport);
	}

	@Test
	public void testShareWithViewPermission_reportUserIsCreatedAndViewIsGrantedToTargetUser() throws Exception {
		Report report = new Report();
		report.setId(10);
		User sharerUser = new User("Joe Owner");
		sharerUser.setId(1);
		report.setOwner(sharerUser);
		User toUser = new User("To User");
		toUser.setId(2);
		when(permissionService.canUserShareReport(sharerUser, toUser, report, permissions)).thenReturn(true);

		manageReportsService.shareWithViewPermission(sharerUser, toUser, report, permissions);

		verify(reportPreferencesService).loadOrCreateReportUser(toUser.getId(), report.getId());
		verify(permissionService).grantView(toUser.getId(), report.getId());
	}

	@Test
	public void testShareWithEditPermission_reportUserIsCreatedAndEditIsGrantedToTargetUser() throws Exception {
		Report report = new Report();
		report.setId(10);
		User sharerUser = new User("Joe Owner");
		sharerUser.setId(1);
		report.setOwner(sharerUser);
		User toUser = new User("To User");
		toUser.setId(2);
		when(permissionService.canUserShareReport(sharerUser, toUser, report, permissions)).thenReturn(true);

		manageReportsService.shareWithEditPermission(sharerUser, toUser, report, permissions);

		verify(reportPreferencesService).loadOrCreateReportUser(toUser.getId(), report.getId());
		verify(permissionService).grantEdit(toUser.getId(), report.getId());
	}

	@Test
	public void testRemoveReport_anyoneWithPermissionShouldBeAbleToRemove() throws Exception {
		Report report = new Report();
		report.setId(10);
		User removerUser = new User("Joe Remover");
		removerUser.setId(1);
		report.setOwner(removerUser);
		ReportUser reportUser = new ReportUser(removerUser.getId(), report);
		when(permissionService.canUserRemoveReport(removerUser, report, permissions)).thenReturn(true);
		when(reportPreferencesService.loadReportUser(removerUser.getId(), report.getId())).thenReturn(reportUser);
		assertFalse(reportUser.isHidden());

		reportUser = manageReportsService.removeReportUser(removerUser, report, permissions);

		verify(reportUserDao).remove(reportUser);
	}

	@Test
	public void testUnshare_anyoneWithPermissionShouldBeAbleToUnshare() throws Exception {
		Report report = new Report();
		report.setId(10);

		User sharerUser = new User("Joe Sharer");
		sharerUser.setId(1);
		report.setOwner(sharerUser);
		User toUser = new User("To User");
		toUser.setId(2);
		ReportUser reportUser = new ReportUser(toUser.getId(), report);
		when(permissionService.canUserShareReport(sharerUser, toUser, report, permissions)).thenReturn(true);
		when(reportPreferencesService.loadReportUser(toUser.getId(), report.getId())).thenReturn(reportUser);

		manageReportsService.unshare(sharerUser, toUser, report, permissions);

		verify(permissionService).unshare(toUser, report);
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

		List<ReportUser> favorites = buildSortedFavoritesThatAreContiguous(USER_ID);
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(favorites);

		assertEquals(4, favorites.size());
		assertEquals(4, favorites.get(0).getSortOrder());
		assertEquals(3, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		favorites = manageReportsService.buildFavorites(USER_ID);

		assertEquals(4, favorites.size());
		assertEquals(4, favorites.get(0).getSortOrder());
		assertEquals(3, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		verify(reportUserDao, never()).save(any(ReportUser.class));
	}

	@Test
	public void testBuildFavorites_whenMaxSortOrderIsGreaterThanListSize_reIndexSortOrder() throws Exception {

		List<ReportUser> favorites = buildSortedFavoritesThatAreNotContiguous(USER_ID);
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(favorites);

		assertEquals(4, favorites.size());
		assertEquals(7, favorites.get(0).getSortOrder());
		assertEquals(5, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		favorites = manageReportsService.buildFavorites(USER_ID);

		assertEquals(4, favorites.size());
		assertEquals(4, favorites.get(0).getSortOrder());
		assertEquals(3, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		verify(reportUserDao).save(favorites.get(0));
		verify(reportUserDao).save(favorites.get(1));
		verify(reportUserDao, never()).save(favorites.get(2));
		verify(reportUserDao, never()).save(favorites.get(3));
	}

	@Test
	public void testBuildFavorites_whenSortOrderIsNotContiguous_reIndexSortOrder() throws Exception {

		List<ReportUser> favorites = buildSortedFavoritesThatAreNotContiguous(USER_ID);
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(favorites);

		assertEquals(4, favorites.size());
		assertEquals(7, favorites.get(0).getSortOrder());
		assertEquals(5, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		favorites = manageReportsService.buildFavorites(USER_ID);

		assertEquals(4, favorites.size());
		assertEquals(4, favorites.get(0).getSortOrder());
		assertEquals(3, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		verify(reportUserDao).save(favorites.get(0));
		verify(reportUserDao).save(favorites.get(1));
		verify(reportUserDao, never()).save(favorites.get(2));
		verify(reportUserDao, never()).save(favorites.get(3));
	}

	@Test
	public void testBuildFavorites_whenSortOrderHasDuplicates_reIndexSortOrder() throws Exception {

		List<ReportUser> favorites = buildSortedFavoritesThatHaveDuplicates(USER_ID);
		when(reportUserDao.findAllFavorite(USER_ID)).thenReturn(favorites);

		assertEquals(4, favorites.size());
		assertEquals(4, favorites.get(0).getSortOrder());
		assertEquals(2, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		favorites = manageReportsService.buildFavorites(USER_ID);

		assertEquals(4, favorites.size());
		assertEquals(4, favorites.get(0).getSortOrder());
		assertEquals(3, favorites.get(1).getSortOrder());
		assertEquals(2, favorites.get(2).getSortOrder());
		assertEquals(1, favorites.get(3).getSortOrder());

		verify(reportUserDao, never()).save(favorites.get(0));
		verify(reportUserDao).save(favorites.get(1));
		verify(reportUserDao, never()).save(favorites.get(2));
		verify(reportUserDao, never()).save(favorites.get(3));
	}

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
