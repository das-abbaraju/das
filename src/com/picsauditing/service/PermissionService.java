package com.picsauditing.service;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;

import java.util.HashSet;
import java.util.Set;

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

	@Deprecated
	public boolean canUserViewReport(Permissions permissions, int reportId) {
		Report report = null;
		try {
			report = reportDao.findById(reportId);
		} catch (Exception e) {
			// don't care
		}

		User user = null;
		try {
			user = userDAO.find(permissions.getUserId());
		} catch (Exception e) {
			// don't care
		}

		return canUserViewReport(user, report, permissions);
	}

    // TODO: Refactor the queries at the bottom with reportUserDAO.getPermissionsUnion or something that will get you the viewClause.
	public boolean canUserViewReport(User user, Report report, Permissions permissions) {
		if (user == null) {
			return false;
		}

		if (report == null) {
			return false;
		}

		if (report.isPublic()) {
			return true;
		}

		if (isOwner(user, report)) {
			return true;
		}

		if (isReportDevelopmentGroup(permissions)) {
			return true;
		}

		try {
			reportPermissionUserDao.findOne(user.getId(), report.getId());
			return true;
		} catch (NoResultException nre) {
			// Don't care
		}

		for (UserGroup group : user.getGroups()) {
			try {
				reportPermissionUserDao.findOne(group.getGroup().getId(), report.getId());
				return true;
			} catch (NoResultException nre) {
				// Don't care
			}
		}

		try {
			reportPermissionAccountDao.findOne(user.getAccount().getId(), report.getId());
			return true;
		} catch (NoResultException nre) {
			// Don't care
		}

        for (Integer parent : permissions.getCorporateParent()) {
            try {
                reportPermissionAccountDao.findOne(parent.intValue(), report.getId());
                return true;
            } catch (NoResultException nre) {
                // Don't care
            }
        }

		return false;
	}

	@Deprecated
    public boolean canUserEditReport(Permissions permissions, int reportId) {
		Report report = null;
		try {
			report = reportDao.findById(reportId);
		} catch (Exception e) {
			// don't care
		}

		User user = null;
		try {
			user = userDAO.find(permissions.getUserId());
		} catch (Exception e) {
			// don't care
		}

		return canUserEditReport(user, report, permissions);
    }

    // TODO: Refactor the queries at the bottom with reportUserDAO.getPermissionsUnion or something that will get you the editClause.
	public boolean canUserEditReport(User user, Report report, Permissions permissions) {
		if (user == null) {
			return false;
		}

		if (report == null) {
			return false;
		}

		if (isOwner(user, report)) {
			return true;
		}

		if (isReportDevelopmentGroup(permissions)) {
			return true;
		}

		try {
			ReportPermissionUser reportPermissionUser = reportPermissionUserDao.findOne(user.getId(), report.getId());

			if (reportPermissionUser != null && reportPermissionUser.isEditable()) {
				return true;
			}
		} catch (Exception e) {
			// Don't care
		}

		for (UserGroup userGroup : user.getGroups()) {
			try {
				ReportPermissionUser reportPermissionGroup = reportPermissionUserDao.findOne(userGroup.getGroup().getId(), report.getId());

				if (reportPermissionGroup != null && reportPermissionGroup.isEditable()) {
					return true;
				}
			} catch (NoResultException nre) {
				// Don't care
			}
		}

		try {
			ReportPermissionAccount reportPermissionAccount = reportPermissionAccountDao.findOne(user.getAccount().getId(), report.getId());

			if (reportPermissionAccount != null && reportPermissionAccount.isEditable()) {
				return true;
			}
		} catch (Exception e) {
			// Don't care
		}

        for (Integer parent : permissions.getCorporateParent()) {
            try {
                ReportPermissionAccount reportPermissionAccount = reportPermissionAccountDao.findOne(parent.intValue(), report.getId());

                if (reportPermissionAccount != null && reportPermissionAccount.isEditable()) {
                    return true;
                }
            } catch (NoResultException nre) {
                // Don't care
            }
        }

        return false;
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

	public boolean canUserShareReport(User user, Report report, Permissions permissions) {
		return isOwnerOrHasEdit(user, report, permissions);
	}

	public boolean canUserRemoveReport(User removerUser, Report report, Permissions permissions) {
        return isOwnerOrHasView(removerUser, report, permissions);
    }

	public boolean canUserPublicizeReport(User user, Report report) {
		return isOwner(user, report);
	}

	private boolean isOwnerOrHasEdit(User user, Report report, Permissions permissions) {
		return isOwner(user, report) || canUserEditReport(user, report, permissions);
	}

	private boolean isOwnerOrHasView(User user, Report report, Permissions permissions) {
        return isOwner(user, report) || canUserViewReport(user, report, permissions);
    }

    public boolean isOwner(User user, Report report) {
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

    public ReportPermissionUser grantUserEditPermission(int sharerId, int sharedToId, int reportId) throws Exception {
        ReportPermissionUser reportPermissionUser = loadOrCreateReportPermissionUser(sharedToId, reportId);
        reportPermissionUser.setEditable(true);
        reportPermissionUser.setAuditColumns(new User(sharerId));
		reportPermissionUserDao.save(reportPermissionUser);

        return reportPermissionUser;
    }

    public ReportPermissionUser grantUserViewPermission(int sharerId, int sharedToId, int reportId) {
        ReportPermissionUser reportPermissionUser = loadOrCreateReportPermissionUser(sharedToId, reportId);
        reportPermissionUser.setEditable(false);
        reportPermissionUser.setAuditColumns(new User(sharerId));
		reportPermissionUserDao.save(reportPermissionUser);

        return reportPermissionUser;
    }

	public ReportPermissionAccount grantAccountViewPermission(int sharerId, Account toAccount, Report report) {
		ReportPermissionAccount reportPermissionAccount;

		try {
			reportPermissionAccount = reportPermissionAccountDao.findOne(toAccount.getId(), report.getId());
		} catch (NoResultException nre) {
			reportPermissionAccount = new ReportPermissionAccount(toAccount, report);
			reportPermissionAccount.setAuditColumns(new User(sharerId));
		}

		reportPermissionAccount.setEditable(false);
		reportPermissionAccountDao.save(reportPermissionAccount);

		return reportPermissionAccount;
	}

	public ReportPermissionAccount grantAccountEditPermission(int sharerId, Account toAccount, Report report) {
		ReportPermissionAccount reportPermissionAccount;

		try {
			reportPermissionAccount = reportPermissionAccountDao.findOne(toAccount.getId(), report.getId());
		} catch (NoResultException nre) {
			reportPermissionAccount = new ReportPermissionAccount(toAccount, report);
			reportPermissionAccount.setAuditColumns(new User(sharerId));
		}

		reportPermissionAccount.setEditable(true);
		reportPermissionAccountDao.save(reportPermissionAccount);

		return reportPermissionAccount;
	}

    public void unshareUserOrGroup(User user, Report report) {
        try {
            ReportPermissionUser reportPermissionUser = loadReportPermissionUser(user.getId(), report.getId());
            reportPermissionUserDao.remove(reportPermissionUser);
        } catch (NoResultException nre) {
			// Don't care
        }
    }

	public void unshareAccount(Account account, Report report) {
		try {
			ReportPermissionAccount reportPermissionAccount = reportPermissionAccountDao.findOne(account.getId(), report.getId());
			reportPermissionAccountDao.remove(reportPermissionAccount);
		} catch (NoResultException nre) {
			// Don't care
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
