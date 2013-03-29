package com.picsauditing.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

public class ManageReportsService {

	@Autowired
	public ReportPreferencesService reportPreferencesService;
	@Autowired
	public PermissionService permissionService;
	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;

	public ReportUser moveFavoriteUp(ReportUser reportUser) throws Exception {
		return moveFavorite(reportUser, 1);
	}

	public ReportUser moveFavoriteDown(ReportUser reportUser) throws Exception {
		return moveFavorite(reportUser, -1);
	}

	private ReportUser moveFavorite(ReportUser reportUser, int magnitude) throws Exception {
		int userId = reportUser.getUser().getId();
		int numberOfFavorites = reportUserDao.getFavoriteCount(userId);
		int currentPosition = reportUser.getSortOrder();
		int newPosition = currentPosition + magnitude;

		if (moveIsUnnecessaryOrInvalid(currentPosition, newPosition, numberOfFavorites)) {
			return reportUser;
		}

		shiftFavoritesDisplacedByMove(userId, currentPosition, newPosition);

		reportUser.setSortOrder(newPosition);
		reportUserDao.save(reportUser);

		return reportUser;
	}

	private boolean moveIsUnnecessaryOrInvalid(int currentPosition, int newPosition, int numberOfFavorites) {
		if (currentPosition == newPosition) {
			return true;
		}

		if ((newPosition < 0) || (newPosition > numberOfFavorites)) {
			return true;
		}

		return false;
	}

	private void shiftFavoritesDisplacedByMove(int userId, int currentPosition, int newPosition) throws SQLException {
		reportUserDao.resetSortOrder(userId);

		int offsetAmount;
		int offsetRangeBegin;
		int offsetRangeEnd;

		if (currentPosition < newPosition) {
			// Moving up in list, displaced reports move down
			offsetAmount = -1;
			offsetRangeBegin = currentPosition + 1;
			offsetRangeEnd = newPosition;
		} else {
			// Moving down in list, displaced reports move up
			offsetAmount = 1;
			offsetRangeBegin = newPosition;
			offsetRangeEnd = currentPosition - 1;
		}

		reportUserDao.offsetSortOrderForRange(userId, offsetAmount, offsetRangeBegin, offsetRangeEnd);
	}

	public List<Report> getReportsForSearch(String searchTerm, Permissions permissions, Pagination<Report> pagination) {
		List<Report> reports = new ArrayList<Report>();

		if (Strings.isEmpty(searchTerm)) {
			// By default, show the top ten most favorited reports sorted by
			// number of favorites
			List<ReportUser> reportUsers = reportUserDao.findTenMostFavoritedReports(permissions);
			for (ReportUser reportUser : reportUsers) {
				reports.add(reportUser.getReport());
			}
		} else {
			ReportPaginationParameters parameters = new ReportPaginationParameters(permissions, searchTerm);
			pagination.initialize(parameters, reportDao);
			reports = pagination.getResults();
		}

		return reports;
	}

	public Report transferOwnership(User fromOwner, User toOwner, Report report, Permissions permissions) throws Exception {
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
			throw new Exception("User " + fromOwner + " does not have permission to transfer ownership of report " + report.getId());
		}

		grantCurrentOwnerEditPermission(report);
		report.setOwner(toOwner);
		reportDao.save(report);

		return report;
	}

	private boolean canTransferOwnership(User fromOwner, Report report, Permissions permissions) {
		return fromOwner.equals(report.getOwner()) || permissionService.isReportDevelopmentGroup(permissions);
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
		if (user == null) {
			throw new Exception("Cannot delete report. user is null");
		}

		if (report == null) {
			throw new Exception("Cannot delete report. report is null");
		}

		if (!canDeleteReport(user, report, permissions)) {
			throw new Exception("User " + user.getId() + " does not have permission to delete report " + report.getId());
		}

		report.setAuditColumns(user);
		reportDao.remove(report);

		return report;
	}

	private boolean canDeleteReport(User user, Report report, Permissions permissions) {
		return permissionService.canUserDeleteReport(user, report, permissions);
	}

	public ReportPermissionUser shareWithViewPermission(User sharerUser, User toUser, Report report, Permissions permissions) throws Exception {
		return shareWithPermission(sharerUser, toUser, report, false, permissions);
	}

	private ReportPermissionUser shareWithPermission(User sharerUser, User toUser, Report report, boolean grantEdit, Permissions permissions) throws Exception {
		if (!canShareReport(sharerUser, toUser, report, permissions)) {
			throw new Exception("User " + sharerUser.getId() + " does not have permission to share report " + report.getId());
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

	private boolean canShareReport(User sharerUser, User toUser, Report report, Permissions permissions) {
		return permissionService.canUserShareReport(sharerUser, toUser, report, permissions);
	}

	public ReportPermissionUser shareWithEditPermission(User sharerUser, User toUser, Report report, Permissions permissions) throws Exception {
		return shareWithPermission(sharerUser, toUser, report, true, permissions);
	}

	public void removeReport(User viewerUser, Report report, Permissions permissions) {
		//To change body of created methods use File | Settings | File Templates.
	}
}
