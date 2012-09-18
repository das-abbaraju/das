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
import com.picsauditing.jpa.entities.ReportPermissionAccount;
import com.picsauditing.jpa.entities.User;

public class ReportPermissionAccountDAOTest {

	private ReportPermissionAccountDAO reportPermissionAccountDao;

	@Mock private EntityManager entityManager;
	@Mock private Report report;
	@Mock private User user;
	@Mock private ReportPermissionAccount reportPermissionAccount;

	private final int REPORT_ID = 37;
	private final int USER_ID = 5;
	private final int REPORT_USER_ID = 101;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportPermissionAccountDao = new ReportPermissionAccountDAO();
		reportPermissionAccountDao.setEntityManager(entityManager);

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
		when(reportPermissionAccount.getId()).thenReturn(0);

		reportPermissionAccountDao.save(reportPermissionAccount);

		verify(entityManager).persist(reportPermissionAccount);
	}

	@Test
	public void testSave_MergeIfExists() {
		when(reportPermissionAccount.getId()).thenReturn(REPORT_USER_ID);

		reportPermissionAccountDao.save(reportPermissionAccount);

		verify(entityManager).merge(reportPermissionAccount);
	}

	@Test
	public void testRemove_CallsEntityManagerRemove() {
		reportPermissionAccountDao.remove(reportPermissionAccount);

		verify(entityManager).remove(reportPermissionAccount);
	}

	@Test
	public void testRemove_DoesntCallEntityManagerRemoveIfNull() {
		reportPermissionAccountDao.remove(null);

		verify(entityManager, never()).remove(reportPermissionAccount);
	}
}
