package com.picsauditing.service;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

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
	private ReportPermissionUserDAO reportPermissionUserDAO;
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
	@Mock
	private ReportUser reportUser;

	private final int REPORT_ID = 29;
	private final int USER_ID = 23;
	private final int ACCOUNT_ID = 23;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        manageReportsService = new ManageReportsService();

        setInternalState(manageReportsService, "reportUserDAO", reportUserDAO);
		setInternalState(manageReportsService, "reportDAO", reportDAO);
		setInternalState(manageReportsService, "reportPermissionUserDAO", reportPermissionUserDAO);
        setInternalState(manageReportsService, "reportPreferencesService", reportPreferencesService);
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
        verify(permissionService).grantUserEditPermission(fromOwner.getId(), fromOwner.getId(), report.getId());
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
	public void testShareReportWithUser_WhenUserCantShare_ThenExceptionIsThrown() throws Exception {
		when(permissionService.canUserShareReport(user, report, permissions)).thenReturn(false);

		manageReportsService.shareReportWithUserOrGroup(user, user, report, permissions, false);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithUser_WhenUserCantViewOrEdit_ThenExceptionIsThrown() throws Exception {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(false);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		manageReportsService.shareReportWithUserOrGroup(user, user, report, permissions, false);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithUser_WhenUserCanViewButNotEdit_ThenExceptionIsThrown() throws Exception {
		when(permissionService.canUserViewReport(permissions, REPORT_ID)).thenReturn(true);
		when(permissionService.canUserEditReport(permissions, REPORT_ID)).thenReturn(false);

		manageReportsService.shareReportWithUserOrGroup(user, user, report, permissions, false);
	}

	@Test
	public void testShareReportWithUser_WhenUserCanViewAndEdit_AndEditableIsTrue_ThenReportIsSharedWithEditPermission() throws Exception {
		when(reportDAO.findById(REPORT_ID)).thenReturn(report);
		when(reportDAO.find(User.class, USER_ID)).thenReturn(user);
		when(permissionService.canUserShareReport(user, report, permissions)).thenReturn(true);
		when(reportPreferencesService.loadOrCreateReportUser(USER_ID, REPORT_ID)).thenReturn(reportUser);
		boolean editable = true;

		manageReportsService.shareReportWithUserOrGroup(user, user, report, permissions, editable);

		verify(permissionService).grantUserEditPermission(USER_ID, USER_ID, REPORT_ID);
	}

	@Test
	public void testShareReportWithUser_WhenUserCanViewAndEdit_AndEditableIsFalse_ThenReportIsSharedWithViewPermission() throws Exception {
		when(reportDAO.findById(REPORT_ID)).thenReturn(report);
		when(reportDAO.find(User.class, USER_ID)).thenReturn(user);
		when(permissionService.canUserShareReport(user, report, permissions)).thenReturn(true);
		when(reportPreferencesService.loadOrCreateReportUser(USER_ID, REPORT_ID)).thenReturn(reportUser);
		boolean editable = false;

		manageReportsService.shareReportWithUserOrGroup(user, user, report, permissions, editable);

		verify(permissionService).grantUserViewPermission(USER_ID, USER_ID, REPORT_ID);
	}

	@Test(expected = ReportPermissionException.class)
	public void testShareReportWithAccount_WhenUserCantShare_ThenExceptionIsThrown() throws ReportPermissionException {
		when(permissionService.canUserShareReport(user, report, permissions)).thenReturn(false);

		manageReportsService.shareReportWithAccountViewPermission(user, account, report, permissions);
	}

	@Test
	public void testShareReportWithAccount_WhenUserCanViewAndEdit_ThenReportIsShared() throws ReportPermissionException {
		when(reportDAO.findById(REPORT_ID)).thenReturn(report);
		when(reportDAO.find(Account.class, ACCOUNT_ID)).thenReturn(account);
		when(permissionService.canUserShareReport(user, report, permissions)).thenReturn(true);

		manageReportsService.shareReportWithAccountViewPermission(user, account, report, permissions);

		verify(permissionService).grantAccountViewPermission(USER_ID, account, report);
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
        when(permissionService.canUserShareReport(sharerUser, report, permissions)).thenReturn(true);
        when(reportPreferencesService.loadReportUser(toUser.getId(), report.getId())).thenReturn(reportUser);

        manageReportsService.unshareUser(sharerUser, toUser, report, permissions);

        verify(permissionService).unshareUserOrGroup(toUser, report);
    }

	@Test
	public void testShareWithViewPermission_reportUserIsUnhidden() throws Exception {
		Report report = new Report();
		report.setId(10);
		User sharerUser = new User("Joe Owner");
		sharerUser.setId(1);
		report.setOwner(sharerUser);
		User toUser = new User("To User");
		toUser.setId(2);
		when(permissionService.canUserShareReport(sharerUser, report, permissions)).thenReturn(true);
		ReportUser reportUser = new ReportUser();
		reportUser.setHidden(true);
		when(reportPreferencesService.loadOrCreateReportUser(toUser.getId(), report.getId())).thenReturn(reportUser);

		manageReportsService.shareReportWithUserOrGroup(sharerUser, toUser, report, permissions, false);

		assertFalse(reportUser.isHidden());
	}

}
