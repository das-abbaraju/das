package com.picsauditing.service;

import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.UserGroup;

public class PermissionService {

	private static final int REPORT_DEVELOPER_GROUP = 77375;

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
