package com.picsauditing.service;

import java.util.List;
import java.util.Locale;

import javax.persistence.NoResultException;

import com.picsauditing.access.ReportPermissionException;
import com.picsauditing.access.UserService;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

public class ManageReportsService {

    @Autowired
    public ReportPreferencesService reportPreferencesService;
    @Autowired
    public PermissionService permissionService;
	@Autowired
	private UserService userService;
	@Autowired
    private ReportDAO reportDAO;
	@Autowired
    private ReportUserDAO reportUserDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private UserGroupDAO userGroupDAO;
	@Autowired
	private ReportPermissionUserDAO reportPermissionUserDAO;
	@Autowired
	private ReportPermissionAccountDAO reportPermissionAccountDAO;
	@Autowired
    private ReportInfoProvider reportInfoProvider;
	@Autowired
	private ReportPermissionInfoConverter reportPermissionInfoConverter;

	public List<ReportInfo> getReportsForOwnedByUser(ReportSearch reportSearch) {
		return reportDAO.findByOwnerID(reportSearch);
	}

	public List<ReportInfo> getReportsForSharedWithUser(ReportSearch reportSearch) {
		return reportDAO.findReportForSharedWith(reportSearch);
	}

	public List<ReportInfo> getReportsForSearch(String searchTerm, Permissions permissions, Pagination<ReportInfo> pagination) {
		// By default, show the top ten most favorited reports sorted by number	 of favorites
		if (Strings.isEmpty(searchTerm)) {
			return reportInfoProvider.findTenMostFavoritedReports(permissions);
		}

		ReportPaginationParameters parameters = new ReportPaginationParameters(permissions, searchTerm);
		pagination.initialize(parameters, reportInfoProvider);
		return pagination.getResults();
	}

	public List<ReportPermissionInfo> buildUserAccessList(Report report) {
		List<ReportPermissionUser> users = reportPermissionUserDAO.findAllUsersByReportId(report.getId());
		List<ReportPermissionInfo> userAccessList = reportPermissionInfoConverter.convertUsersToReportPermissionInfo(users);

		makeSureOwnerIsInList(report.getOwner().getId(), userAccessList);

		return userAccessList;
	}

	// When we can assure that new reports will always have a row in report_permission_user for the owner,
	// then we can remove this method.
	private void makeSureOwnerIsInList(int ownerId, List<ReportPermissionInfo> userAccessList) {
		boolean found = false;

		for (ReportPermissionInfo info : userAccessList) {
			if (info.getId() == ownerId) {
				found = true;
				break;
			}
		}

		if (!found) {
			User owner = userService.loadUser(ownerId);

			ReportPermissionInfo ownerInfo = new ReportPermissionInfo();

			ownerInfo.setId(ownerId);
			ownerInfo.setName(owner.getName());
			ownerInfo.setOwner(true);
			ownerInfo.setEditable(true);
			ownerInfo.setLocation(owner.getAccount().getName());
			ownerInfo.setType(ReportPermissionInfoConverter.TYPE_USER);
			ownerInfo.setAccessType(ReportPermissionInfoConverter.ACCESS_TYPE_USER);

			userAccessList.add(0, ownerInfo);
		}
	}

	public List<ReportPermissionInfo> buildGroupAndAccountAccessList(Report report, Locale locale) {
		List<ReportPermissionUser> groups = reportPermissionUserDAO.findAllGroupsByReportId(report.getId());
		List<ReportPermissionInfo> groupInfoList = reportPermissionInfoConverter.convertUsersToReportPermissionInfo(groups);

		List<ReportPermissionAccount> accounts = reportPermissionAccountDAO.findAllByReportId(report.getId());
		List<ReportPermissionInfo> accountInfoList = reportPermissionInfoConverter.convertAccountsToReportPermissionInfo(accounts, locale);

		return ListUtils.union(groupInfoList, accountInfoList);
	}

	public ReportPermissionUser shareReportWithUserOrGroup(User sharerUser, User toUser, Report report, Permissions permissions,
			boolean editable) throws Exception {
		if (!permissionService.canUserShareReport(sharerUser, report, permissions)) {
			// TODO translate this
			throw new ReportPermissionException("You cannot share a report that you cannot edit.");
		}

        ReportPermissionUser reportPermissionUser;
        if (editable) {
            reportPermissionUser = permissionService.grantUserEditPermission(sharerUser.getId(), toUser.getId(), report.getId());
        } else {
            reportPermissionUser = permissionService.grantUserViewPermission(sharerUser.getId(), toUser.getId(), report.getId());
        }

		if (!toUser.isGroup()) {
			ReportUser reportUser = reportPreferencesService.loadOrCreateReportUser(toUser.getId(), report.getId());
			reportUser.setAuditColumns(permissions);
			reportUser.setHidden(false);
			reportUserDAO.save(reportUser);
		}

        return reportPermissionUser;
	}

	public ReportPermissionAccount shareReportWithAccountViewPermission(User sharerUser, Account toAccount, Report report, Permissions permissions) throws ReportPermissionException {
		if (!permissionService.canUserShareReport(sharerUser, report, permissions)) {
			// TODO translate this
			throw new ReportPermissionException("You cannot share a report that you cannot edit.");
		}

		return permissionService.grantAccountViewPermission(sharerUser.getId(), toAccount, report);
	}

	public ReportPermissionAccount shareReportWithAccountEditPermission(User sharerUser, Account toAccount, Report report, Permissions permissions) throws ReportPermissionException {
		if (!permissionService.canUserShareReport(sharerUser, report, permissions)) {
			// TODO translate this
			throw new ReportPermissionException("You cannot share a report that you cannot edit.");
		}

		return permissionService.grantAccountEditPermission(sharerUser.getId(), toAccount, report);
	}

	public void unshareUser(User revokerUser, User fromUser, Report report, Permissions permissions) throws Exception {
		if (!permissionService.canUserShareReport(revokerUser, report, permissions)) {
			throw new Exception("User " + revokerUser.getId() + " does not have permission to unshareUser report " + report.getId());
		}

		permissionService.unshareUserOrGroup(fromUser, report);

		removeReportPreferencesIfCannotView(fromUser, report, permissions);
	}

	public void unshareGroup(User revokerUser, User group, Report report, Permissions permissions) throws Exception {
		if (!permissionService.canUserShareReport(revokerUser, report, permissions)) {
			throw new Exception("User " + revokerUser.getId() + " does not have permission to unshareUser report " + report.getId());
		}

		permissionService.unshareUserOrGroup(group, report);

		for (UserGroup userGroup : userGroupDAO.findByGroup(group.getId())) {
			User user = userGroup.getUser();

			removeReportPreferencesIfCannotView(user, report, permissions);
		}
	}

	public void unshareAccount(User revokerUser, Account account, Report report, Permissions permissions) throws Exception {
		if (!permissionService.canUserShareReport(revokerUser, report, permissions)) {
			throw new Exception("User " + revokerUser.getId() + " does not have permission to unshareUser report " + report.getId());
		}

		permissionService.unshareAccount(account, report);

		for (User user : userDAO.findUsersByAccountId(account.getId())) {
			removeReportPreferencesIfCannotView(user, report, permissions);
		}
	}

	private void removeReportPreferencesIfCannotView(User user, Report report, Permissions permissions) {
		if (!permissionService.canUserViewReport(user, report, permissions)) {
			try {
				ReportUser reportUser = reportPreferencesService.loadReportUser(user.getId(), report.getId());
				reportUserDAO.remove(reportUser);
			} catch (NoResultException nre) {
				// Don't care
			}
		}
	}

	public Report transferOwnership(User fromOwner, User toOwner, Report report, Permissions permissions)
			throws Exception {
		checkExceptionConditionsToTransferOwnership(fromOwner, toOwner, report, permissions);

		// Make sure former owner has these rows
		reportPreferencesService.loadOrCreateReportUser(fromOwner.getId(), report.getId());
		permissionService.grantUserEditPermission(fromOwner.getId(), fromOwner.getId(), report.getId());

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

		if (!permissionService.canTransferOwnership(fromOwner, report, permissions)) {
			throw new Exception("User " + fromOwner + " does not have permission to transfer ownership of report "
					+ report.getId());
		}
	}

	public ReportUser removeReportUser(User removerUser, Report report, Permissions permissions) throws Exception {
        if (!permissionService.canUserRemoveReport(removerUser, report, permissions)) {
            throw new Exception("User " + removerUser.getId() + " does not have permission to remove report "
                    + report.getId());
        }

        ReportUser reportUser = null;

        try {
            reportUser = reportPreferencesService.loadReportUser(removerUser.getId(), report.getId());
            reportUser.setHidden(true);
			reportUserDAO.save(reportUser);
        } catch (NoResultException dontCare) {
        }

        return reportUser;
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

		if (!permissionService.canUserDeleteReport(user, report, permissions)) {
			throw new Exception("User " + user.getId() + " does not have permission to delete report " + report.getId());
		}
	}

}
