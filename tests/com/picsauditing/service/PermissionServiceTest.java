package com.picsauditing.service;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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

		boolean result = permissionService.canUserViewReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanUserViewAndCopy_AssociationWithAccountReturnsTrue() {
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionAccountDao.findOne(ACCOUNT_ID, REPORT_ID)).thenReturn(null);

		boolean result = permissionService.canUserViewReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanUserViewAndCopy_UserInDevGroupReturnsTrue() {
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionAccountDao.findOne(ACCOUNT_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenReturn(null);

		boolean result = permissionService.canUserViewReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanUserViewAndCopy_ReturnsFalse() {
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
		when(reportPermissionAccountDao.findOne(ACCOUNT_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenThrow(new NoResultException());

		boolean result = permissionService.canUserViewReport(permissions, REPORT_ID);

		assertFalse(result);
	}

	@Test
	public void testCanUserEdit_FalseIfNoResultException() {
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenThrow(new NoResultException());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(reportPermissionUserDao.findOne(UserGroup.class, "group.id = 77375 AND user.id = 23")).thenThrow(new NoResultException());

		boolean result = permissionService.canUserEditReport(permissions, REPORT_ID);

		assertFalse(result);
	}

	@Test
	public void testCanUserEdit_FalseIfNoEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(false);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(reportPermissionUser);
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(reportPermissionUserDao.findOne(UserGroup.class, "group.id = 77375 AND user.id = " + USER_ID)).thenThrow(new NoResultException());

		boolean result = permissionService.canUserEditReport(permissions, REPORT_ID);

		assertFalse(result);
	}

	@Test
	public void testCanUserEdit_WhenDaoReturnsNull_ThenReturnFalse() {
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(null);

		boolean result = permissionService.canUserEditReport(permissions, REPORT_ID);

		assertFalse(result);
	}

	@Test
	public void testCanUserEdit_TrueIfEditPermission() {
		when(reportPermissionUser.isEditable()).thenReturn(true);
		when(reportPermissionUserDao.findOneByPermissions(permissions, REPORT_ID)).thenReturn(reportPermissionUser);

		boolean result = permissionService.canUserEditReport(permissions, REPORT_ID);

		assertTrue(result);
	}

	@Test
	public void testCanTransferOwnership_ownerCanTransfer() throws Exception {
		Report report = new Report();
		User fromOwner = new User("From Owner");
		report.setOwner(fromOwner);

		boolean result = permissionService.canTransferOwnership(fromOwner, report, permissions);

		assertTrue(result);
	}

	@Test
	public void testCanTransferOwnership_devGroupCanTransfer() throws Exception {
		Report report = new Report();
		User owner = new User("Joe Owner");
		report.setOwner(owner);
		User fromOwner = new User("Not The Owner");
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenReturn(new UserGroup());

		boolean result = permissionService.canTransferOwnership(fromOwner, report, permissions);

		assertTrue(result);
	}

	@Test
	public void testCanTransferOwnership_otherThanOwnerOrDevGroupCannotTransfer() throws Exception {
		Report report = new Report();
		User owner = new User("Joe Owner");
		report.setOwner(owner);
		User otherUser = new User("Not The Owner");
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenThrow(new NoResultException());

		boolean result = permissionService.canTransferOwnership(otherUser, report, permissions);

		assertFalse(result);
	}

	@Test
	public void testCanUserDeleteReport_ownerCanDelete() throws Exception {
		Report report = new Report();
		User owner = new User("Joe Owner");
		report.setOwner(owner);

		boolean result = permissionService.canUserDeleteReport(owner, report, permissions);

		assertTrue(result);
	}

	@Test
	public void testCanUserDeleteReport_devGroupCanDelete() throws Exception {
		Report report = new Report();
		User owner = new User("Joe Owner");
		report.setOwner(owner);
		User otherUser = new User("Not The Owner");
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenReturn(new UserGroup());

		boolean result = permissionService.canUserDeleteReport(otherUser, report, permissions);

		assertTrue(result);
	}

	@Test
	public void testCanUserDeleteReport_otherThanOwnerOrDevGroupCannotTransfer() throws Exception {
		Report report = new Report();
		User owner = new User("Joe Owner");
		report.setOwner(owner);
		User otherUser = new User("Not the Owner");
		when(reportPermissionUserDao.findOne(eq(UserGroup.class), anyString())).thenThrow(new NoResultException());

		boolean result = permissionService.canUserDeleteReport(otherUser, report, permissions);

		assertFalse(result);
	}

	@Test
	public void testGrantEdit_WhenEditPermissionIsGranted_ThenEditableShouldBeTrue() throws Exception {
		ReportPermissionUser reportPermissionUser = new ReportPermissionUser();
		reportPermissionUser.setEditable(false);
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportPermissionUser);

		reportPermissionUser = permissionService.grantEdit(USER_ID, REPORT_ID);

		assertTrue(reportPermissionUser.isEditable());
	}

	@Test
	public void testGrantView_WhenViewPermissionIsGranted_ThenEditableShouldBeFalse() throws Exception {
		ReportPermissionUser reportPermissionUser = new ReportPermissionUser();
		reportPermissionUser.setEditable(true);
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenReturn(reportPermissionUser);

		reportPermissionUser = permissionService.grantView(USER_ID, REPORT_ID);

		assertFalse(reportPermissionUser.isEditable());
	}

}
