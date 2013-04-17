package com.picsauditing.service;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

import javax.persistence.NoResultException;

public class ManageReportsServiceTest {

    private ManageReportsService manageReportsService;

    @Mock
    private ReportDAO reportDAO;
    @Mock
    private ReportUserDAO reportUserDAO;
	@Mock
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Mock
	private ReportPermissionAccountDAO reportPermissionAccountDao;
    @Mock
    private Permissions permissions;
    @Mock
    private Pagination<ReportInfo> reportPagination;
    @Mock
    private ReportPreferencesService reportPreferencesService;
    @Mock
    private PermissionService permissionService;
    @Mock
    private ReportInfoProvider reportInfoProvider;

	@Mock
	private User user;
	@Mock
	private Account account;
	@Mock
	private Report report;

	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manageReportsService = new ManageReportsService();

        setInternalState(manageReportsService, "reportUserDAO", reportUserDAO);
        setInternalState(manageReportsService, "reportDAO", reportDAO);
        setInternalState(manageReportsService, "reportPreferencesService", reportPreferencesService);
		setInternalState(manageReportsService, "reportPermissionUserDao", reportPermissionUserDao);
		setInternalState(manageReportsService, "reportPermissionAccountDao", reportPermissionAccountDao);
        setInternalState(manageReportsService, "permissionService", permissionService);
        setInternalState(manageReportsService, "reportInfoProvider", reportInfoProvider);

		when(user.getId()).thenReturn(USER_ID);
		when(report.getId()).thenReturn(REPORT_ID);
		when(account.getId()).thenReturn(ACCOUNT_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getAccountIdString()).thenReturn(String.valueOf(ACCOUNT_ID));
		when(permissions.getUserIdString()).thenReturn(String.valueOf(USER_ID));
    }

    @Test
    public void testGetReportAccessesForSearch_NullSearchTermCallsTopTenFavorites() {
        List<ReportInfo> reports = manageReportsService.getReportsForSearch(null, permissions, reportPagination);
        Set<Integer> set = new HashSet<Integer>();
        set.add(1294);

        assertNotNull(reports);
        verify(reportInfoProvider).findTenMostFavoritedReports(permissions);
    }

    @Test
    public void testGetReportAccessesForSearch_BlankSearchTermCallsTopTenFavorites() {
        List<ReportInfo> reports = manageReportsService.getReportsForSearch(Strings.EMPTY_STRING, permissions, reportPagination);
        Set<Integer> set = new HashSet<Integer>();
        set.add(1294);

        assertNotNull(reports);
        verify(reportInfoProvider).findTenMostFavoritedReports(permissions);
    }

    @Test
    public void testTransferOwnership_previousOwnerShouldRetainEditPermission() throws Exception {
        Report report = new Report();
        User fromOwner = new User("From Owner");
        report.setOwner(fromOwner);
        User toOwner = new User("To Owner");
        when(permissionService.canTransferOwnership(fromOwner, report, permissions)).thenReturn(true);

        manageReportsService.transferOwnership(fromOwner, toOwner, report, permissions);

        verify(reportPreferencesService).loadOrCreateReportUser(fromOwner.getId(), report.getId());
        verify(permissionService).grantEdit(fromOwner.getId(), report.getId());
    }

    @Test
    public void testTransferOwnership_newOwnerShouldBeSetOnReport() throws Exception {
        Report report = new Report();
        User fromOwner = new User("From Owner");
        report.setOwner(fromOwner);
        User toOwner = new User("To Owner");
        when(permissionService.canTransferOwnership(fromOwner, report, permissions)).thenReturn(true);

        Report resultReport = manageReportsService.transferOwnership(fromOwner, toOwner, report, permissions);

        assertNotSame(fromOwner, resultReport.getOwner());
        assertEquals(toOwner, resultReport.getOwner());
    }

    @Test
    public void testDeleteReport_anyoneWithPermissionCanDelete() throws Exception {
        Report report = new Report();
        User deleterUser = new User("Joe Owner");
        report.setOwner(deleterUser);
        when(permissionService.canUserDeleteReport(deleterUser, report, permissions)).thenReturn(true);

        Report resultReport = manageReportsService.deleteReport(deleterUser, report, permissions);

        assertTrue(DateBean.isToday(resultReport.getUpdateDate()));
        assertEquals(deleterUser, resultReport.getUpdatedBy());
        verify(reportDAO).remove(resultReport);
    }

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithUser_WhenUserCantViewOrEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		manageReportsService.shareReportWithUser(USER_ID, REPORT_ID, permissions, false);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithUser_WhenUserCanViewButNotEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		manageReportsService.shareReportWithUser(USER_ID, REPORT_ID, permissions, false);
	}

	@Test
	public void testShareReportWithUser_WhenUserCanViewAndEdit_AndEditableIsTrue_ThenReportIsSharedWithEditPermission() throws ReportPermissionException {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDAO.findById(REPORT_ID)).thenReturn(report);
		when(reportDAO.find(User.class, USER_ID)).thenReturn(user);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);
		boolean editable = true;

		ReportPermissionUser reportPermissionUser = manageReportsService.shareReportWithUser(USER_ID, REPORT_ID, permissions, editable);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertEquals(editable, reportPermissionUser.isEditable());
	}

	@Test
	public void testShareReportWithUser_WhenUserCanViewAndEdit_AndEditableIsFalse_ThenReportIsSharedWithoutEditPermission() throws ReportPermissionException {
		when(reportPermissionUserDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDAO.findById(REPORT_ID)).thenReturn(report);
		when(reportDAO.find(User.class, USER_ID)).thenReturn(user);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);
		boolean editable = false;

		ReportPermissionUser reportPermissionUser = manageReportsService.shareReportWithUser(USER_ID, REPORT_ID, permissions, editable);

		verify(reportPermissionUserDao).save(reportPermissionUser);
		assertEquals(REPORT_ID, reportPermissionUser.getReport().getId());
		assertEquals(USER_ID, reportPermissionUser.getUser().getId());
		assertEquals(editable, reportPermissionUser.isEditable());
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithAccount_WhenUserCantViewOrEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		manageReportsService.shareReportWithAccount(ACCOUNT_ID, REPORT_ID, permissions);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithAccount_WhenUserCanViewButNotEdit_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		manageReportsService.shareReportWithAccount(ACCOUNT_ID, REPORT_ID, permissions);
	}

	@Test
	public void testShareReportWithAccount_WhenUserCanViewAndEdit_ThenReportIsShared() throws ReportPermissionException {
		when(reportPermissionAccountDao.findOne(USER_ID, REPORT_ID)).thenThrow(new NoResultException());
		when(reportDAO.findById(REPORT_ID)).thenReturn(report);
		when(reportDAO.find(Account.class, ACCOUNT_ID)).thenReturn(account);
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(true);

		ReportPermissionAccount reportPermissionAccount = manageReportsService.shareReportWithAccount(ACCOUNT_ID, REPORT_ID, permissions);

		verify(reportPermissionAccountDao).save(reportPermissionAccount);
		assertEquals(REPORT_ID, reportPermissionAccount.getReport().getId());
		assertEquals(ACCOUNT_ID, reportPermissionAccount.getAccount().getId());
	}

	@Test
    public void testShareWithViewPermission_reportUserIsCreatedAndViewIsGrantedToTargetUser() throws Exception {
        Report report = new Report();
        report.setId(10);
        User sharerUser = new User("Joe Owner");
        sharerUser.setId(1);
        report.setOwner(sharerUser);
        User toUser = new User("To User");
        toUser.setId(2);
        when(permissionService.canUserShareReport(sharerUser, toUser, report, permissions)).thenReturn(true);

        manageReportsService.shareWithViewPermission(sharerUser, toUser, report, permissions);

        verify(reportPreferencesService).loadOrCreateReportUser(toUser.getId(), report.getId());
        verify(permissionService).grantView(toUser.getId(), report.getId());
    }

    @Test
    public void testShareWithEditPermission_reportUserIsCreatedAndEditIsGrantedToTargetUser() throws Exception {
        Report report = new Report();
        report.setId(10);
        User sharerUser = new User("Joe Owner");
        sharerUser.setId(1);
        report.setOwner(sharerUser);
        User toUser = new User("To User");
        toUser.setId(2);
        when(permissionService.canUserShareReport(sharerUser, toUser, report, permissions)).thenReturn(true);

        manageReportsService.shareWithEditPermission(sharerUser, toUser, report, permissions);

        verify(reportPreferencesService).loadOrCreateReportUser(toUser.getId(), report.getId());
        verify(permissionService).grantEdit(toUser.getId(), report.getId());
    }

    @Test
    public void testRemoveReport_anyoneWithPermissionShouldBeAbleToRemove() throws Exception {
        Report report = new Report();
        report.setId(10);
        User removerUser = new User("Joe Remover");
        removerUser.setId(1);
        report.setOwner(removerUser);
        ReportUser reportUser = new ReportUser(removerUser.getId(), report);
        when(permissionService.canUserRemoveReport(removerUser, report, permissions)).thenReturn(true);
        when(reportPreferencesService.loadReportUser(removerUser.getId(), report.getId())).thenReturn(reportUser);
        assertFalse(reportUser.isHidden());

        reportUser = manageReportsService.removeReportUser(removerUser, report, permissions);

        verify(reportUserDAO).remove(reportUser);
    }

    @Test
    public void testUnshare_anyoneWithPermissionShouldBeAbleToUnshare() throws Exception {
        Report report = new Report();
        report.setId(10);

        User sharerUser = new User("Joe Sharer");
        sharerUser.setId(1);
        report.setOwner(sharerUser);
        User toUser = new User("To User");
        toUser.setId(2);
        ReportUser reportUser = new ReportUser(toUser.getId(), report);
        when(permissionService.canUserShareReport(sharerUser, toUser, report, permissions)).thenReturn(true);
        when(reportPreferencesService.loadReportUser(toUser.getId(), report.getId())).thenReturn(reportUser);

        manageReportsService.unshare(sharerUser, toUser, report, permissions);

        verify(permissionService).unshare(toUser, report);
    }

}
