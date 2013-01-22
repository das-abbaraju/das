package com.picsauditing.service;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.UserGroup;

public class PermissionService {

	private static final int REPORT_DEVELOPER_GROUP = 77375;

	@Autowired
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Autowired
	private ReportPermissionAccountDAO reportPermissionAccountDao;

	private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	public boolean canUserViewAndCopyReport(Permissions permissions, int reportId) {
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

	public boolean canUserEditReport(Permissions permissions, Report report) {
		boolean editable = false;

		try {
			editable = reportPermissionUserDao.findOneByPermissions(permissions, report.getId()).isEditable();
		} catch (NoResultException nre) {
			logger.error("No results found for {} and reportId = {}", permissions.toString(), report.getId());
		}

		return editable || isReportDevelopmentGroup(permissions);
	}

	private boolean isReportDevelopmentGroup(Permissions permissions) {
		try {
			int userID = permissions.getUserId();

			if (permissions.getAdminID() > 0) {
				userID = permissions.getAdminID();
			}

			String where = "group.id = " + REPORT_DEVELOPER_GROUP + " AND user.id = " + userID;

			// FIXME this method should be something like UserService.isUserInGroup(userId, groupId)
			reportPermissionUserDao.findOne(UserGroup.class, where);
		} catch (NoResultException nre) {
			return false;
		}

		return true;
	}

}
