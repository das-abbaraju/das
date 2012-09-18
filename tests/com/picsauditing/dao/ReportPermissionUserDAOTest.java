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
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.User;

public class ReportPermissionUserDAOTest {

	private ReportPermissionUserDAO reportPermissionUserDao;

	@Mock private EntityManager entityManager;
	@Mock private Report report;
	@Mock private User user;
	@Mock private ReportPermissionUser reportPermissionUser;

	private final int REPORT_ID = 37;
	private final int USER_ID = 5;
	private final int REPORT_USER_ID = 101;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportPermissionUserDao = new ReportPermissionUserDAO();
		reportPermissionUserDao.setEntityManager(entityManager);

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
		when(reportPermissionUser.getId()).thenReturn(0);

		reportPermissionUserDao.save(reportPermissionUser);

		verify(entityManager).persist(reportPermissionUser);
	}

	@Test
	public void testSave_MergeIfExists() {
		when(reportPermissionUser.getId()).thenReturn(REPORT_USER_ID);

		reportPermissionUserDao.save(reportPermissionUser);

		verify(entityManager).merge(reportPermissionUser);
	}

	@Test
	public void testRemove_CallsEntityManagerRemove() {
		reportPermissionUserDao.remove(reportPermissionUser);

		verify(entityManager).remove(reportPermissionUser);
	}

	@Test
	public void testRemove_DoesntCallEntityManagerRemoveIfNull() {
		reportPermissionUserDao.remove(null);

		verify(entityManager, never()).remove(reportPermissionUser);
	}
}
