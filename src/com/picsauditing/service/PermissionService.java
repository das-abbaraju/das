package com.picsauditing.service;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.UserGroup;

public class PermissionService {

    private static final int REPORT_DEVELOPER_GROUP = 77375;

    @Autowired
    private ReportDAO reportDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ReportPermissionUserDAO reportPermissionUserDao;
    @Autowired
    private ReportPermissionAccountDAO reportPermissionAccountDao;

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    public boolean canUserViewReport(Permissions permissions, int reportId) {
        // TODO If it's a PICS report, return true

        try {
            reportPermissionUserDao.findOneByPermissions(permissions, reportId);
        } catch (NoResultException nre) {
            try {
                reportPermissionAccountDao.findOne(permissions.getAccountId(), reportId);
            } catch (NoResultException nr) {
                return isReportDevelopmentGroup(permissions);
            }
        }

        return true;
    }

    public boolean canUserEditReport(Permissions permissions, int reportId) {
        try {
            ReportPermissionUser reportPermissionUser = reportPermissionUserDao.findOneByPermissions(permissions, reportId);
            if (reportPermissionUser == null) {
                return false;
            }

            if (reportPermissionUser.isEditable()) {
                return true;
            }
        } catch (NoResultException nre) {
            logger.error("No results found for {} and reportId = {}", permissions.toString(), reportId);
        }

        return isReportDevelopmentGroup(permissions);
    }

    public boolean canTransferOwnership(User fromOwner, Report report, Permissions permissions) {
        return isOwner(fromOwner, report) || isReportDevelopmentGroup(permissions);
    }

    public boolean canUserDeleteReport(User user, Report report, Permissions permissions) {
        if (report == null) {
            return false;
        }
        return isOwner(user, report) || isReportDevelopmentGroup(permissions);
    }

    public boolean canUserShareReport(User granterUser, User toUser, Report report, Permissions permissions) {
        return isOwnerOrHasEdit(granterUser, report, permissions);
    }

    public boolean canUserRemoveReport(User removerUser, Report report, Permissions permissions) {
        return isOwnerOrHasView(removerUser, report, permissions);
    }

    private boolean isOwnerOrHasEdit(User user, Report report, Permissions permissions) {
        return isOwner(user, report) || canUserEditReport(permissions, report.getId());
    }

    private boolean isOwnerOrHasView(User user, Report report, Permissions permissions) {
        return isOwner(user, report) || canUserViewReport(permissions, report.getId());
    }

    private boolean isOwner(User user, Report report) {
        return report.getOwner().equals(user);
    }

    // todo: Refactor. Permissions is basically a user session and should not be passed in this far.
    public boolean isReportDevelopmentGroup(Permissions permissions) {
        try {
            int userID = permissions.getUserId();

            if (permissions.getAdminID() > 0) {
                userID = permissions.getAdminID();
            }

            String where = "group.id = " + REPORT_DEVELOPER_GROUP + " AND user.id = " + userID;

            reportPermissionUserDao.findOne(UserGroup.class, where);
        } catch (NoResultException nre) {
            return false;
        }

        return true;
    }

    public ReportPermissionUser grantEdit(int userId, int reportId) throws Exception {
        ReportPermissionUser reportPermissionUser = loadOrCreateReportPermissionUser(userId, reportId);
        reportPermissionUser.setEditable(true);
        reportPermissionUser.setAuditColumns(new User(userId));
        reportPermissionAccountDao.save(reportPermissionUser);

        return reportPermissionUser;
    }

    public ReportPermissionUser grantView(int userId, int reportId) {
        ReportPermissionUser reportPermissionUser = loadOrCreateReportPermissionUser(userId, reportId);
        reportPermissionUser.setEditable(true);
        reportPermissionUser.setAuditColumns(new User(userId));
        reportPermissionAccountDao.save(reportPermissionUser);

        return reportPermissionUser;
    }

    public void unshare(User user, Report report) {
        try {
            ReportPermissionUser reportPermissionUser = loadReportPermissionUser(user.getId(), report.getId());
            reportPermissionAccountDao.remove(reportPermissionUser);
        } catch (NoResultException dontCare) {
        }
    }

    private ReportPermissionUser loadOrCreateReportPermissionUser(int userId, int reportId) {
        ReportPermissionUser reportPermissionUser;
        try {
            reportPermissionUser = loadReportPermissionUser(userId, reportId);
        } catch (NoResultException nre) {
            reportPermissionUser = createReportPermissionUser(userId, reportId);
        }
        return reportPermissionUser;
    }

    private ReportPermissionUser loadReportPermissionUser(int userId, int reportId) throws NoResultException, NonUniqueResultException {
        return reportPermissionUserDao.findOne(userId, reportId);
    }

    private ReportPermissionUser createReportPermissionUser(int userId, int reportId) {
        Report report = reportDao.findById(reportId);
        User user = userDAO.find(userId);
        ReportPermissionUser reportPermissionUser = new ReportPermissionUser(user, report);
        return reportPermissionUser;
    }
}
