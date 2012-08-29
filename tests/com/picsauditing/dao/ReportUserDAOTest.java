package com.picsauditing.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

public class ReportUserDAOTest {

	private ReportUserDAO reportUserDao;

	@Mock private EntityManager entityManager;
	@Mock private Report report;
	@Mock private User user;
	@Mock private ReportUser userReport;

	private final int REPORT_ID = 37;
	private final int USER_ID = 5;
	private final int REPORT_USER_ID = 101;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportUserDao = new ReportUserDAO();
		reportUserDao.setEntityManager(entityManager);

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
	public void testSave_PersistIfNew() {
		when(userReport.getId()).thenReturn(0);

		reportUserDao.save(userReport);

		verify(entityManager).persist(userReport);
	}

	@Test
	public void testSave_MergeIfExists() {
		when(userReport.getId()).thenReturn(REPORT_USER_ID);

		reportUserDao.save(userReport);

		verify(entityManager).merge(userReport);
	}

	@Test
	public void testRemove_CallsEntityManagerRemove() {
		reportUserDao.remove(userReport);

		verify(entityManager).remove(userReport);
	}

	@Test
	public void testRemove_DoesntCallEntityManagerRemoveIfNull() {
		reportUserDao.remove(null);

		verify(entityManager, never()).remove(userReport);
	}
}
