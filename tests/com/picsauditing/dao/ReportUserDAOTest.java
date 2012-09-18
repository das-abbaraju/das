package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
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
	@Mock private ReportUser reportUser;

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
		when(reportUser.getId()).thenReturn(0);

		reportUserDao.save(reportUser);

		verify(entityManager).persist(reportUser);
	}

	@Test
	public void testSave_MergeIfExists() {
		when(reportUser.getId()).thenReturn(REPORT_USER_ID);

		reportUserDao.save(reportUser);

		verify(entityManager).merge(reportUser);
	}

	@Test
	public void testRemove_CallsEntityManagerRemove() {
		reportUserDao.remove(reportUser);

		verify(entityManager).remove(reportUser);
	}

	@Test
	public void testRemove_DoesntCallEntityManagerRemoveIfNull() {
		reportUserDao.remove(null);

		verify(entityManager, never()).remove(reportUser);
	}
}
