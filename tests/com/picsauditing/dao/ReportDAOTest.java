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
	public void testFindReportByID() {
		reportDao.findOne(5);

		verify(reportDao).findOne(Report.class, "t.id = 5");
	}
}
