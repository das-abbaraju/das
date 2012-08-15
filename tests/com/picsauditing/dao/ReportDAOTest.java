package com.picsauditing.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.models.ModelType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class ReportDAOTest {

	private ReportDAO reportDao;

	@Mock
	private EntityManager mockEntityManager;
	@Mock
	private Report report;
	@Mock
	private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportDao = new ReportDAO();
		reportDao.setEntityManager(mockEntityManager);

		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(23);
	}

	@Ignore
	@Test
	public void testConnectReportToUser() {
		reportDao.connectReportToUser(report, user);

		verify(reportDao).save(any(ReportUser.class));
	}

	@Ignore
	@Test(expected = NoResultException.class)
	public void grantPermissionToEdit_listTooSmall() {
		when(reportDao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(null);

		reportDao.grantEditPermission(report, user);

		verify(reportDao, never()).save(any(ReportUser.class));
	}

	@Ignore
	@Test(expected = NonUniqueResultException.class)
	public void grantPermissionToEdit_listTooBig() {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		testList.add(new ReportUser());
		testList.add(new ReportUser());
		when(reportDao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportDao.grantEditPermission(report, user);

		verify(reportDao, never()).save(any(ReportUser.class));
	}

	@Ignore
	@Test
	public void grantPermissionToEdit_listJustRight() {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(reportDao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportDao.grantEditPermission(report, user);

		verify(repUser).setEditable(true);
		verify(reportDao).save(repUser);
	}

	@Ignore
	@Test
	public void revokePermissionToEdit_listJustRight() {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(reportDao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportDao.revokeEditPermission(report, user);

		verify(repUser).setEditable(false);
		verify(reportDao).save(repUser);
	}

	@Ignore
	@Test
	public void saveReport() throws ReportValidationException {
		when(report.getModelType()).thenReturn(ModelType.Accounts);
		when(report.getParameters()).thenReturn("{}");

		reportDao.saveReport(report, user);

		verify(report).setAuditColumns(user);
		verify(reportDao).save(report);
	}

	@Ignore
	@Test
	public void deleteReport() throws NoResultException {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser mockRU1 = mock(ReportUser.class), mockRU2 = mock(ReportUser.class);
		testList.add(mockRU1);
		testList.add(mockRU2);
		when(reportDao.findWhere(ReportUser.class, "t.report.id = 555")).thenReturn(testList);

		reportDao.deleteReport(report);

		verify(reportDao).remove(mockRU1);
		verify(reportDao).remove(mockRU2);
		verify(reportDao).remove(report);
	}

	@Ignore
	@Test
	public void findReportByID() {
		reportDao.findOneReport(5);

		verify(reportDao).findOne(Report.class, "t.id = 5");
	}

	@Ignore
	@Test
	public void removeReportAssociation() throws Exception {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(reportDao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportDao.removeUserReport(user, report);

		verify(reportDao).remove(repUser);
	}
}
