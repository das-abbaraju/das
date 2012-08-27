package com.picsauditing.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.util.ArrayList;
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

	@Mock private EntityManager mockEntityManager;
	@Mock private Report report;
	@Mock private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		reportUserDao = new ReportUserDAO();
		reportUserDao.setEntityManager(mockEntityManager);

		when(report.getId()).thenReturn(555);
		when(user.getId()).thenReturn(23);
	}

	@Ignore
	@Test
	public void testRemove() throws Exception {
		List<ReportUser> testList = new ArrayList<ReportUser>();
		ReportUser repUser = mock(ReportUser.class);
		testList.add(repUser);
		when(reportUserDao.findWhere(ReportUser.class, "t.user.id = 23 AND t.report.id = 555")).thenReturn(testList);

		reportUserDao.remove(user, report);

		verify(reportUserDao).remove(repUser);
	}
}
