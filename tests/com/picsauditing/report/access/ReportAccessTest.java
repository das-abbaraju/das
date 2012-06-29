package com.picsauditing.report.access;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.models.ModelType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

public class ReportAccessTest {
	
	@Mock private BasicDAO dao;
	@Mock private Report report;
	@Mock private User user;
	private ReportAdministration reportAccess;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportAccess = new ReportAccessor();
		
		setInternalState(reportAccess, "basicDao", dao);
		
		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(23);
	}
	
	@Test
	public void canUserViewAndCopy_nullQuery () {
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(null);
		
		assertFalse(reportAccess.canUserViewAndCopy(23, report));
	}
	
	@Ignore
	@Test
	public void canUserViewAndCopy_emptyQuery () {
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(null);

		assertFalse(reportAccess.canUserViewAndCopy(23, report));
	}
	
	@Test
	public void canUserViewAndCopy_successfulQuery () {
		List<ReportUser> fakeList = new ArrayList<ReportUser>();
		fakeList.add(new ReportUser());
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(fakeList);

		assertTrue(reportAccess.canUserViewAndCopy(23, report));
	}
	
	@Test
	public void canUserEdit_nullResponse () {
		when(dao.findOne(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(null);
		
		assertFalse(reportAccess.canUserEdit(23, report));
	}
	
	@Test
	public void canUserEdit_Negative () {
		ReportUser mockRU = mock(ReportUser.class);
		when(mockRU.canEditReport()).thenReturn(false);
		when(dao.findOne(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(mockRU);

		assertFalse(reportAccess.canUserEdit(23, report));
	}
	
	@Test
	public void canUserEdit_Positive () {
		ReportUser mockRU = mock(ReportUser.class);
		when(mockRU.canEditReport()).thenReturn(true);
		when(dao.findOne(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(mockRU);

		assertTrue(reportAccess.canUserEdit(23, report));
	}
	
	@Test
	public void canUserDelete_Negative () {
		User mockCreator = mock(User.class);
		when(mockCreator.getId()).thenReturn(5);
		when(report.getCreatedBy()).thenReturn(mockCreator);
		
		assertFalse(reportAccess.canUserDelete(23, report));
	}
	
	@Test
	public void canUserDelete_Positive () {
		User mockCreator = mock(User.class);
		when(mockCreator.getId()).thenReturn(23);
		when(report.getCreatedBy()).thenReturn(mockCreator);
		
		assertTrue(reportAccess.canUserDelete(23, report));
	}
	
	@Test
	public void connectReportToUser () {
		reportAccess.connectReportToUser(report, user);
		
		verify(dao).save(any(ReportUser.class));
	}
	
	@Test
	public void grantPermissionToEdit_listTooSmall () {
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(null);
		
		reportAccess.grantPermissionToEdit(report, user);
		
		verify(dao, never()).save(any(ReportUser.class));
	}
	
	@Test
	public void grantPermissionToEdit_listTooBig () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		testList.add(new ReportUser());
		testList.add(new ReportUser());
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);
		
		reportAccess.grantPermissionToEdit(report, user);
		
		verify(dao, never()).save(any(ReportUser.class));		
	}
	
	@Test
	public void grantPermissionToEdit_listJustRight () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);
		
		reportAccess.grantPermissionToEdit(report, user);
		
		verify(repUser).setEditable(true);
		verify(dao).save(repUser);
	}
	
	@Test
	public void revokePermissionToEdit_listJustRight () {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(dao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);
		
		reportAccess.revokePermissionToEdit(report, user);
		
		verify(repUser).setEditable(false);
		verify(dao).save(repUser);		
	}
	
	@Test
	public void saveReport () throws ReportValidationException {
		when(report.getModelType()).thenReturn(ModelType.Accounts);
		when(report.getParameters()).thenReturn("{}");
		
		reportAccess.saveReport(report, user);
		
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
		
		reportAccess.deleteReport(report);
		
		verify(dao).remove(mockRU1);
		verify(dao).remove(mockRU2);
		verify(dao).remove(report);
	}
	
	@Test
	public void findReportByID () {
		
		reportAccess.findReportById(5);
		
		verify(dao).findOne(Report.class, "t.id = 5");
	}
}
