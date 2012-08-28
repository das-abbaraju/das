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

public class ReportDAOTest {

	private ReportDAO reportDao;

	@Mock private EntityManager mockEntityManager;
	@Mock private Report report;
	@Mock private User user;

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
	public void testSaveReport() throws ReportValidationException {
		when(report.getModelType()).thenReturn(ModelType.Accounts);
		when(report.getParameters()).thenReturn("{}");

		reportDao.save(report, user);

		verify(report).setAuditColumns(user);
		verify(reportDao).save(report);
	}

	@Ignore
	@Test
	public void testDeleteReport() throws NoResultException {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser mockRU1 = mock(ReportUser.class), mockRU2 = mock(ReportUser.class);
		testList.add(mockRU1);
		testList.add(mockRU2);
		when(reportDao.findWhere(ReportUser.class, "t.report.id = 555")).thenReturn(testList);

		reportDao.removeAndCascade(report);

		verify(reportDao).remove(mockRU1);
		verify(reportDao).remove(mockRU2);
		verify(reportDao).removeAndCascade(report);
	}

	@Ignore
	@Test
	public void testFindReportByID() {
		reportDao.findOne(5);

		verify(reportDao).findOne(Report.class, "t.id = 5");
	}
}
