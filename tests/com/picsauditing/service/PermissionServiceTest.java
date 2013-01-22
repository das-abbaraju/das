package com.picsauditing.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;

public class PermissionServiceTest {

	PermissionService permissionService;

	@Mock
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Mock
	private ReportPermissionAccountDAO reportPermissionAccountDao;
	@Mock
	private User user;
	@Mock
	private Report report;
	@Mock
	private ReportPermissionUser reportPermissionUser;
	@Mock
	private Permissions permissions;

	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		permissionService = new PermissionService();

		setInternalState(permissionService, "reportPermissionUserDao", reportPermissionUserDao);
		setInternalState(permissionService, "reportPermissionAccountDao", reportPermissionAccountDao);

		when(report.getId()).thenReturn(REPORT_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
	}

	@Test
	public void testCanUserViewAndCopy_AssociationWithUserReturnsTrue() {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(null);

		boolean result = permissionService.canUserViewAndCopyReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanUserViewAndCopy_AssociationWithAccountReturnsTrue() {
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionAccountDao.findOne(ACCOUNT_ID, REPORT_ID)).thenReturn(null);

		boolean result = permissionService.canUserViewAndCopyReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanUserViewAndCopy_UserInDevGroupReturnsTrue() {
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionAccountDao.findOne(ACCOUNT_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenReturn(null);

		boolean result = permissionService.canUserViewAndCopyReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanUserViewAndCopy_ReturnsFalse() {
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(reportPermissionAccountDao.findOne(ACCOUNT_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenThrow(new NoResultException());

		boolean result = permissionService.canUserViewAndCopyReport(permissions, REPORT_ID);

		assertFalse(result);
	}

	@Test
	public void canUserEdit_FalseIfNoResultException() {
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(reportPermissionUserDao.findOne(UserGroup.class, "group.id = 77375 AND user.id = 23")).thenThrow(new NoResultException());

		boolean result = permissionService.canUserEditReport(permissions, report);

		assertFalse(result);
	}

	@Test
	public void canUserEdit_FalseIfNoEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(false);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(reportPermissionUser);
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(reportPermissionUserDao.findOne(UserGroup.class, "group.id = 77375 AND user.id = " + USER_ID)).thenThrow(new NoResultException());

		boolean result = permissionService.canUserEditReport(permissions, report);

		assertFalse(result);
	}

	@Test
	public void canUserEdit_TrueIfEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(true);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(reportPermissionUser);

		boolean result = permissionService.canUserEditReport(permissions, report);

		assertTrue(result);
	}

}
