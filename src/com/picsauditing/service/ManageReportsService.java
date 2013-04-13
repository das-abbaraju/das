package com.picsauditing.service;

import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

public class ManageReportsService {

    @Autowired
    public ReportPreferencesService reportPreferencesService;
    @Autowired
    public PermissionService permissionService;
    @Autowired
    private ReportDAO reportDAO;
    @Autowired
    private ReportUserDAO reportUserDAO;
    @Autowired
    private ReportInfoProvider reportInfoProvider;

	public List<ReportInfo> getReportsForSearch(String searchTerm, Permissions permissions,
                                                Pagination<ReportInfo> pagination) {
        // By default, show the top ten most favorited reports sorted by number
        // of favorites
        if (Strings.isEmpty(searchTerm)) {
            return reportInfoProvider.findTenMostFavoritedReports(permissions);
        }

        ReportPaginationParameters parameters = new ReportPaginationParameters(permissions, searchTerm);
        pagination.initialize(parameters, reportInfoProvider);
        return pagination.getResults();
    }

    public Report transferOwnership(User fromOwner, User toOwner, Report report, Permissions permissions)
            throws Exception {
        checkExceptionConditionsToTransferOwnership(fromOwner, toOwner, report, permissions);

        grantCurrentOwnerEditPermission(report);
        report.setOwner(toOwner);
        reportDAO.save(report);

        return report;
    }

    private void checkExceptionConditionsToTransferOwnership(User fromOwner, User toOwner, Report report,
                                                             Permissions permissions) throws Exception {
        if (fromOwner == null) {
            throw new Exception("Cannot transfer ownership. fromOwner is null");
        }

        if (toOwner == null) {
            throw new Exception("Cannot transfer ownership. toOwner is null");
        }

        if (report == null) {
            throw new Exception("Cannot transfer ownership. report is null");
        }

        if (!canTransferOwnership(fromOwner, report, permissions)) {
            throw new Exception("User " + fromOwner + " does not have permission to transfer ownership of report "
                    + report.getId());
        }
    }

    private boolean canTransferOwnership(User fromOwner, Report report, Permissions permissions) {
        return permissionService.canTransferOwnership(fromOwner, report, permissions);
    }

    private ReportPermissionUser grantCurrentOwnerEditPermission(Report report) throws Exception {
        User currentOwner = getCurrentOwnerOfReport(report);
        reportPreferencesService.loadOrCreateReportUser(currentOwner.getId(), report.getId());
        ReportPermissionUser reportPermissionUser = permissionService.grantEdit(currentOwner.getId(), report.getId());
        return reportPermissionUser;
    }

    private User getCurrentOwnerOfReport(Report report) {
        User currentOwner = report.getOwner();
        if (currentOwner == null) {
            currentOwner = report.getCreatedBy();
        }
        return currentOwner;
    }

    public Report deleteReport(User user, Report report, Permissions permissions) throws Exception {
        checkExceptionConditionsToDeleteReport(user, report, permissions);

        report.setAuditColumns(user);
        reportDAO.remove(report);

        return report;
    }

    private void checkExceptionConditionsToDeleteReport(User user, Report report, Permissions permissions)
            throws Exception {
        if (user == null) {
            throw new Exception("Cannot delete report. user is null");
        }

        if (report == null) {
            throw new Exception("Cannot delete report. report is null");
        }

        if (!canDeleteReport(user, report, permissions)) {
            throw new Exception("User " + user.getId() + " does not have permission to delete report " + report.getId());
        }
    }

    private boolean canDeleteReport(User user, Report report, Permissions permissions) {
        return permissionService.canUserDeleteReport(user, report, permissions);
    }

    public ReportPermissionUser shareWithViewPermission(User sharerUser, User toUser, Report report,
                                                        Permissions permissions) throws Exception {
        return shareWithPermission(sharerUser, toUser, report, false, permissions);
    }

    private ReportPermissionUser shareWithPermission(User sharerUser, User toUser, Report report, boolean grantEdit,
                                                     Permissions permissions) throws Exception {
        if (!canShareReport(sharerUser, toUser, report, permissions)) {
            throw new Exception("User " + sharerUser.getId() + " does not have permission to share report "
                    + report.getId());
        }
        reportPreferencesService.loadOrCreateReportUser(toUser.getId(), report.getId());
        ReportPermissionUser reportPermissionUser;
        if (grantEdit) {
            reportPermissionUser = permissionService.grantEdit(toUser.getId(), report.getId());
        } else {
            reportPermissionUser = permissionService.grantView(toUser.getId(), report.getId());
        }

        return reportPermissionUser;
    }

    public void unshare(User sharerUser, User toUser, Report report, Permissions permissions) throws Exception {
        if (!canShareReport(sharerUser, toUser, report, permissions)) {
            throw new Exception("User " + sharerUser.getId() + " does not have permission to unshare report "
                    + report.getId());
        }
        permissionService.unshare(toUser, report);
    }

    private boolean canShareReport(User sharerUser, User toUser, Report report, Permissions permissions) {
        return permissionService.canUserShareReport(sharerUser, toUser, report, permissions);
    }

    public ReportPermissionUser shareWithEditPermission(User sharerUser, User toUser, Report report,
                                                        Permissions permissions) throws Exception {
        return shareWithPermission(sharerUser, toUser, report, true, permissions);
    }

    public ReportUser removeReportUser(User removerUser, Report report, Permissions permissions) throws Exception {
        if (!canRemoveReport(removerUser, report, permissions)) {
            throw new Exception("User " + removerUser.getId() + " does not have permission to remove report "
                    + report.getId());
        }

        ReportUser reportUser = null;
        try {
            reportUser = reportPreferencesService.loadReportUser(removerUser.getId(), report.getId());
            reportUserDAO.remove(reportUser);
        } catch (NoResultException dontCare) {
        }

        return reportUser;
    }

    private boolean canRemoveReport(User removerUser, Report report, Permissions permissions) {
        return permissionService.canUserRemoveReport(removerUser, report, permissions);
    }

    public List<ReportInfo> getReportsForOwnedByUser(ReportSearch reportSearch) {
        return reportDAO.findByOwnerID(reportSearch);
    }

    public List<ReportInfo> getReportsForSharedWithUser(ReportSearch reportSearch) {
        return reportDAO.findReportForSharedWith(reportSearch);
    }

}
