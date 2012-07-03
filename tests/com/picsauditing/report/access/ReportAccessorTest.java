package com.picsauditing.report.access;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.models.ModelType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class ReportAccessorTest {

	private ReportAccessor reportAccessor;

	@Mock private BasicDAO dao;
	@Mock private Report report;
	@Mock private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportAccessor = new ReportAccessor();

		setInternalState(reportAccessor, "basicDao", dao);

		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(23);
	}

	@Test
	public void connectReportToUser () {
		reportAccessor.connectReportToUser(report, user);

		verify(dao).save(any(ReportUser.class));
	}

	@Test(expected = NoResultException.class)
	public void grantPermissionToEdit_listTooSmall () {
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(null);

		reportAccessor.grantEditPermission(report, user);

		verify(dao, never()).save(any(ReportUser.class));
	}

	@Test(expected = NonUniqueResultException.class)
	public void grantPermissionToEdit_listTooBig () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		testList.add(new ReportUser());
		testList.add(new ReportUser());
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportAccessor.grantEditPermission(report, user);

		verify(dao, never()).save(any(ReportUser.class));
	}

	@Test
	public void grantPermissionToEdit_listJustRight () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportAccessor.grantEditPermission(report, user);

		verify(repUser).setEditable(true);
		verify(dao).save(repUser);
	}

	@Test
	public void revokePermissionToEdit_listJustRight () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportAccessor.revokeEditPermission(report, user);

		verify(repUser).setEditable(false);
		verify(dao).save(repUser);
	}

	@Test
	public void saveReport () throws ReportValidationException {
		when(report.getModelType()).thenReturn(ModelType.Accounts);
		when(report.getParameters()).thenReturn("{}");

		reportAccessor.saveReport(report, user);

		verify(report).setAuditColumns(user);
		verify(dao).save(report);
	}

	@Test
	public void deleteReport() throws NoResultException {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser mockRU1 = mock(ReportUser.class), mockRU2 = mock(ReportUser.class);
		testList.add(mockRU1);
		testList.add(mockRU2);
		when(dao.findWhere(ReportUser.class, "t.report.id = 555")).thenReturn(testList);

		reportAccessor.deleteReport(report);

		verify(dao).remove(mockRU1);
		verify(dao).remove(mockRU2);
		verify(dao).remove(report);
	}

	@Test
	public void findReportByID () {
		reportAccessor.findReportById(5);

		verify(dao).findOne(Report.class, "t.id = 5");
	}

	@Test
	public void removeReportAssociation () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportAccessor.removeReportAssociation(user, report);

		verify(dao).remove(repUser);
	}
}
