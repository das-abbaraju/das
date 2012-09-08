package com.picsauditing.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.models.ModelType;

public class ReportModelTest {

	private ReportModel reportModel;

	@Mock private ReportDAO reportDao;
	@Mock private ReportUserDAO reportUserDao;
	@Mock private User user;
	@Mock private Report report;
	@Mock private ReportUser userReport;
	@Mock private BasicDynaBean dynaBean;

	private final int REPORT_ID = 37;
	private final String REPORT_NAME = "My Report";
	private final String REPORT_DESCRIPTION = "This is a report";
	private final int USER_ID = 23;
	private final String USER_NAME = "User Name";
	private final int NUM_TIMES_FAVORITED = 10;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportModel = new ReportModel();

		setInternalState(reportModel, "reportDao", reportDao);
		setInternalState(reportModel, "reportUserDao", reportUserDao);

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

		assertTrue(reportModel.canUserViewAndCopy(USER_ID, REPORT_ID));
	}

	@Test
	public void canUserViewAndCopy_FalseIfPrivateReportNoAssociationWithUser() {
		when(report.isPublic()).thenReturn(false);
		when(reportDao.find(Report.class, REPORT_ID)).thenReturn(report);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(null);

		assertFalse(reportModel.canUserViewAndCopy(USER_ID, REPORT_ID));
	}

	@Test
	public void canUserViewAndCopy_TrueIfAssociationWithUser() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(new ReportUser());

		assertTrue(reportModel.canUserViewAndCopy(USER_ID, REPORT_ID));
	}

	@Test
	public void canUserViewAndCopy_FalseIfNoResultException() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());

		assertFalse(reportModel.canUserViewAndCopy(USER_ID, REPORT_ID));
	}

	@Test
	public void canUserEdit_FalseIfNoResultException() {
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());

		assertFalse(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserEdit_FalseIfNoEditPermission() {
		when(userReport.isEditable()).thenReturn(false);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(userReport);

		assertFalse(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserEdit_TrueIfEditPermission() {
		when(userReport.isEditable()).thenReturn(true);
		when(reportUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(userReport);

		assertTrue(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserDelete_FalseIfNotCreatedByUser() {
		User otherUser = mock(User.class);
		when(report.getCreatedBy()).thenReturn(otherUser);

		assertFalse(ReportModel.canUserDelete(USER_ID, report));
	}

	@Test
	public void canUserDelete_TrueIfCreatedByUser() {
		when(report.getCreatedBy()).thenReturn(user);

		assertTrue(ReportModel.canUserDelete(USER_ID, report));
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
	public void testGetUserReportsForSearch_NullSearchTermCallsTopTenFavorites() {
		List<ReportUser> userReports = reportModel.getUserReportsForSearch(null, 0);

		assertNotNull(userReports);
		verify(reportUserDao).findTenMostFavoritedReports(anyInt());
	}

	@Test
	public void testGetUserReportsForSearch_BlankSearchTermCallsTopTenFavorites() {
		List<ReportUser> userReports = reportModel.getUserReportsForSearch("", 0);

		assertNotNull(userReports);
		verify(reportUserDao).findTenMostFavoritedReports(anyInt());
	}

	@Test
	public void testGetUserReportsForSearch_ValidSearchTermCallsFindReportsForSearchFilter() {
		List<ReportUser> userReports = reportModel.getUserReportsForSearch("SEARCH_TERM", 0);

		assertNotNull(userReports);
		verify(reportUserDao).findAllForSearchFilter(anyInt(), anyString());
	}

	@Test
	public void testPopulateUserReports_NoResultsReturnsEmptyList() {
		List<BasicDynaBean> emptyResults = new ArrayList<BasicDynaBean>();

		List<ReportUser> userReports = ReportModel.populateUserReports(emptyResults);

		assertNotNull(userReports);
		assertEquals(0, userReports.size());
	}

	@Test
	public void testPopulateUserReports_ResultsPopulateCorrectly() {
		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		when(dynaBean.get("id")).thenReturn(REPORT_ID);
		when(dynaBean.get("name")).thenReturn(REPORT_NAME);
		when(dynaBean.get("description")).thenReturn(REPORT_DESCRIPTION);
		when(dynaBean.get("userId")).thenReturn(USER_ID);
		when(dynaBean.get("userName")).thenReturn(USER_NAME);
		when(dynaBean.get("numTimesFavorited")).thenReturn(NUM_TIMES_FAVORITED);
		results.add(dynaBean);

		List<ReportUser> userReports = ReportModel.populateUserReports(results);
		ReportUser userReport = userReports.get(0);
		Report report = userReport.getReport();
		User user = report.getCreatedBy();

		assertNotNull(userReports);
		assertEquals(1, userReports.size());
		assertEquals(REPORT_ID, report.getId());
		assertEquals(REPORT_NAME, report.getName());
		assertEquals(REPORT_DESCRIPTION, report.getDescription());
		assertEquals(USER_ID, user.getId());
		assertEquals(USER_NAME, user.getName());
		assertEquals(NUM_TIMES_FAVORITED, report.getNumTimesFavorited());
	}

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
	public void testConnectReportToUser() {
		ReportUser userReport = reportModel.connectReportToUser(report, USER_ID);

		verify(reportUserDao).save(userReport);
		assertEquals(REPORT_ID, userReport.getReport().getId());
		assertEquals(USER_ID, userReport.getUser().getId());
		assertFalse(userReport.isEditable());
		assertFalse(userReport.isFavorite());
	}

	@Test
	public void testConnectReportToUserEditable() {
		ReportUser userReport = reportModel.connectReportToUserEditable(report, USER_ID);

		verify(reportUserDao).save(userReport);
		assertEquals(REPORT_ID, userReport.getReport().getId());
		assertEquals(USER_ID, userReport.getUser().getId());
		assertTrue(userReport.isEditable());
		assertFalse(userReport.isFavorite());
	}
}
