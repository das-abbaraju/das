package com.picsauditing.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.ResultSetDynaClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.models.ModelType;

public class ReportModelTest {

	private ReportModel reportModel;

	@Mock private ReportDAO reportDao;
	@Mock private Report report;
	@Mock private User user;
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

		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(USER_ID);
	}

	@Test
	public void canUserViewAndCopy_nullQuery() {
		when(reportDao.findOneUserReport(anyInt(), anyInt())).thenReturn(null);

		assertFalse(reportModel.canUserViewAndCopy(USER_ID, report));
	}

	@Test
	public void canUserViewAndCopy_emptyQuery() {

		when(reportDao.findOneUserReport(anyInt(), anyInt())).thenThrow(new NoResultException());

		assertFalse(reportModel.canUserViewAndCopy(USER_ID, report));
	}

	@Test
	public void canUserViewAndCopy_baseReport() {
		assertTrue(reportModel.canUserViewAndCopy(USER_ID, 2));
	}

	@Test
	public void canUserViewAndCopy_successfulQuery () {

		when(reportDao.findOneUserReport(anyInt(), anyInt())).thenReturn(new ReportUser());

		assertTrue(reportModel.canUserViewAndCopy(USER_ID, report));
	}

	@Test
	public void canUserEdit_Negative() {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(false);
		when(reportDao.findOneUserReport(anyInt(), anyInt())).thenReturn(mockReportUser);

		assertFalse(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserEdit_Positive() {
		ReportUser mockReportUser = mock(ReportUser.class);
		when(mockReportUser.isEditable()).thenReturn(true);
		when(reportDao.findOneUserReport(anyInt(), anyInt())).thenReturn(mockReportUser);

		assertTrue(reportModel.canUserEdit(USER_ID, report));
	}

	@Test
	public void canUserDelete_Negative() {
		User mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(5);
		when(report.getCreatedBy()).thenReturn(mockUser);

		assertFalse(ReportModel.canUserDelete(USER_ID, report));
	}

	@Test
	public void canUserDelete_Positive() {
		User mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(USER_ID);
		when(report.getCreatedBy()).thenReturn(mockUser);

		assertTrue(ReportModel.canUserDelete(USER_ID, report));
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_NullReport() throws ReportValidationException {
		Report report = null;

		ReportModel.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_NullModelType() throws ReportValidationException {
		Report report = new Report();
		report.setModelType(null);

		ReportModel.validate(report);
	}

	@Test(expected = ReportValidationException.class)
	public void testValidate_InvalidReportParameters() throws ReportValidationException {
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
	public void testGetReportsForSearch_NullSearchTermCallsTopTenFavorites() {
		List<BasicDynaBean> userReports = reportModel.getReportsForSearch(null, 0);

		assertNotNull(userReports);
		verify(reportDao).findTopTenFavoriteReports(anyInt());
	}

	@Test
	public void testGetReportsForSearch_BlankSearchTermCallsTopTenFavorites() {
		List<BasicDynaBean> userReports = reportModel.getReportsForSearch("", 0);

		assertNotNull(userReports);
		verify(reportDao).findTopTenFavoriteReports(anyInt());
	}

	@Test
	public void testGetReportsForSearch_ValidSearchTermCallsFindReportsForSearchFilter() {
		List<BasicDynaBean> userReports = reportModel.getReportsForSearch("SEARCH_TERM", 0);

		assertNotNull(userReports);
		verify(reportDao).findReportsForSearchFilter(anyInt(), anyString());
	}

	@Test
	public void testPopulateUserReports_NoResultsReturnsEmptyList() {
		List<BasicDynaBean> emptyResults = new ArrayList<BasicDynaBean>();

		List<ReportUser> userReports = reportModel.populateUserReports(emptyResults);

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

		List<ReportUser> userReports = reportModel.populateUserReports(results);
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
}