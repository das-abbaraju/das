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
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.models.ModelType;

import javax.persistence.EntityManager;

public class ReportDAOTest {

	private ReportDAO reportDao;

	@Mock private EntityManager entityManager;
	@Mock private Report report;
	@Mock private User user;

	private final int REPORT_ID = 37;
	private final int USER_ID = 5;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportDao = new ReportDAO();
		reportDao.setEntityManager(entityManager);

		when(report.getModelType()).thenReturn(ModelType.Accounts);
		when(report.getParameters()).thenReturn("{}");
		when(report.getId()).thenReturn(REPORT_ID);
		when(user.getId()).thenReturn(USER_ID);
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
	public void testSave() throws ReportValidationException {
		reportDao.save(report, user);

		verify(report).setAuditColumns(user);
		verify(entityManager).merge(report);
	}

	@Test
	public void testRemove_CallsEntityManageRemove() {
		reportDao.remove(report);

		verify(entityManager).remove(report);
	}

	@Test
	public void testRemove_DoesntCallEntityManageRemoveIfnull() {
		reportDao.remove(null);

		verify(entityManager, never()).remove(any(Report.class));
	}
}
