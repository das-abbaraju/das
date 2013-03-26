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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

	private static final int USER_ID = 23;
	private static final int REPORT_ID = 29;
	private static final int MAX_SORT_ORDER = 10;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportPreferencesService = new ReportPreferencesService();

		setInternalState(reportPreferencesService, "reportDao", reportDao);
		setInternalState(reportPreferencesService, "reportUserDao", reportUserDao);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);

	}

	@Test
	public void testLoadOrCreateReportUser() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);

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
