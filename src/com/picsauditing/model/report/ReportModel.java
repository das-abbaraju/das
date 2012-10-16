package com.picsauditing.model.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportPermissionAccountDAO;
import com.picsauditing.dao.ReportPermissionUserDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportPermissionAccount;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

public class ReportModel {

	private static final int REPORT_DEVELOPER_GROUP = 77375;

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;
	@Autowired
	private ReportPermissionUserDAO reportPermissionUserDao;
	@Autowired
	private ReportPermissionAccountDAO reportPermissionAccountDao;

	public boolean canUserViewAndCopy(Permissions permissions, int reportId) {
		try {
			ReportPermissionUser reportPermissionUser = reportPermissionUserDao.findOne(permissions, reportId);
			if (reportPermissionUser != null)
				return true;
			ReportPermissionAccount reportPermissionAccount = reportPermissionAccountDao.findOne(
					permissions.getAccountId(), reportId);
			if (reportPermissionAccount != null)
				return true;
		} catch (NoResultException e) {
			return false;
		}

		return false;
	}

	public boolean canUserEdit(Permissions permissions, Report report) {
		try {
			ReportPermissionUser reportPermissionUser = reportPermissionUserDao.findOne(permissions, report.getId());
			if (reportPermissionUser.isEditable())
				return true;

			String userID = permissions.getUserIdString();
			
			if (permissions.getAdminID() > 0)
				userID = "" + permissions.getAdminID();

			String where = "group.id = " + REPORT_DEVELOPER_GROUP + " AND user.id = " + userID;
			
			reportDao.findOne(UserGroup.class, where);
			
			return true;
		} catch (NoResultException e) {

		}

		return false;
	}

	public void setEditPermissions(Permissions permissions, int id, int reportId, boolean editable) throws NoResultException,
			NonUniqueResultException, SQLException, Exception {
		ReportPermissionUser reportPermissionUser = connectReportPermissionUser(permissions, id, reportId, editable);

		reportPermissionUserDao.save(reportPermissionUser);
	}

	public Report copy(Report sourceReport, Permissions permissions) throws NoRightsException,
			ReportValidationException {
		if (!canUserViewAndCopy(permissions, sourceReport.getId()))
			throw new NoRightsException("User " + permissions.getUserId() + " does not have permission to copy report "
					+ sourceReport.getId());

		boolean editable = true;

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO the front end is passing new report data in the current report,
		// so we need to change sourceReport to it's old state.
		// Is this is the desired behavior?
		reportDao.refresh(sourceReport);

		ReportModel.validate(newReport);
		newReport.setAuditColumns(permissions);
		reportDao.save(newReport);

		// This is a new report owned by the user, unconditionally give them
		// edit permission
		connectReportUser(permissions.getUserId(), newReport.getId());
		connectReportPermissionUser(permissions, permissions.getUserId(), newReport.getId(), editable);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		if (!canUserEdit(permissions, report))
			throw new NoRightsException("User " + permissions.getUserId() + " cannot edit report " + report.getId());

		report.setAuditColumns(permissions);
		reportDao.save(report);
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();

		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());
		newReport.setVersion(sourceReport.getVersion());

		return newReport;
	}

	/**
	 * Rudimentary validation of a report object. Currently, this only means
	 * that the model type is set and that the report-spec is parsable as valid
	 * JSON.
	 */
	public static void validate(Report report) throws ReportValidationException {
		if (report == null)
			throw new ReportValidationException("Report object is null. (Possible security concern.)");

		if (report.getModelType() == null)
			throw new ReportValidationException("Report " + report.getId() + " is missing its base", report);

		if (report.getDefinition().getColumns().size() == 0)
			throw new ReportValidationException("Report contained no columns");

		try {
			new JSONParser().parse(report.getParameters());
		} catch (ParseException e) {
			throw new ReportValidationException(e, report);
		}
	}

	public List<Report> getReportsForSearch(String searchTerm, Permissions permissions, Pagination<Report> pagination) {
		List<Report> reports = new ArrayList<Report>();

		if (Strings.isEmpty(searchTerm)) {
			// By default, show the top ten most favorited reports sorted by
			// number of favorites
			List<ReportUser> reportUsers = reportUserDao.findTenMostFavoritedReports(permissions.getUserId(),
					permissions.getAccountId());
			for (ReportUser reportUser : reportUsers) {
				reports.add(reportUser.getReport());
			}
		} else {
			ReportPaginationParameters parameters = new ReportPaginationParameters(permissions.getUserId(),
					permissions.getAccountId(), searchTerm);
			pagination.Initialize(parameters, reportDao);
			reports = pagination.getResults();
		}

		return reports;
	}

	public List<ReportPermissionUser> getReportPermissionUsersForMyReports(String sort, String direction, Permissions permissions)
			throws IllegalArgumentException {
		List<ReportPermissionUser> reportPermissionUsers = new ArrayList<ReportPermissionUser>();

		if (Strings.isEmpty(sort)) {
			reportPermissionUsers = reportPermissionUserDao.findAll(permissions);
		} else if (sort.equals(ManageReports.ALPHA_SORT)) {
			reportPermissionUsers = reportPermissionUserDao.findAllSortByAlpha(permissions, direction);
		} else if (sort.equals(ManageReports.DATE_ADDED_SORT)) {
			reportPermissionUsers = reportPermissionUserDao.findAllSortByDateAdded(permissions, direction);
		} else if (sort.equals(ManageReports.LAST_VIEWED_SORT)) {
			reportPermissionUsers = reportPermissionUserDao.findAllSortByLastViewed(permissions, direction);
		} else {
			throw new IllegalArgumentException("Unexpected sort type '" + sort + "'");
		}

		return reportPermissionUsers;
	}

	public void updateLastViewedDate(int userID, Report report) {
		ReportUser reportUser = new ReportUser();

		try {
			reportUser = reportUserDao.findOne(userID, report.getId());
		} catch (NoResultException nre) {
			User user = new User();
			user.setId(userID);

			reportUser.setUser(user);
			reportUser.setReport(report);
		}

		reportUser.setLastViewedDate(new Date());
		reportUser.setViewCount(reportUser.getViewCount() + 1);
		reportUserDao.save(reportUser);
	}

	public void favoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException,
			SQLException, Exception {
		ReportUser reportUser = connectReportUser(userId, reportId);

		reportUserDao.cascadeFavoriteReportSorting(userId, 1, 1, Integer.MAX_VALUE);

		reportUser.setSortOrder(1);
		reportUser.setFavorite(true);
		reportUserDao.save(reportUser);
	}

	public void unfavoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException,
			SQLException, Exception {
		ReportUser unfavoritedReportUser = connectReportUser(userId, reportId);
		int removedSortIndex = unfavoritedReportUser.getSortOrder();

		reportUserDao.cascadeFavoriteReportSorting(userId, -1, removedSortIndex + 1, Integer.MAX_VALUE);

		unfavoritedReportUser.setSortOrder(0);
		unfavoritedReportUser.setFavorite(false);
		reportUserDao.save(unfavoritedReportUser);
	}

	public void moveReportUser(int userId, int reportId, int magnitude) throws Exception {
		reportUserDao.resetSortOrder(userId);

		ReportUser reportUser = reportUserDao.findOne(userId, reportId);
		int numberOfFavorites = reportUserDao.getFavoriteCount(userId);
		int currentPosition = reportUser.getSortOrder();
		int newPosition = currentPosition + magnitude;

		if (currentPosition == newPosition || newPosition < 0 || newPosition > numberOfFavorites)
			return;

		int offsetPosition;
		int topPositionToMove;
		int bottomPositionToMove;

		if (currentPosition < newPosition) {
			// Moving down in list, other reports move up
			offsetPosition = -1;
			topPositionToMove = currentPosition + 1;
			bottomPositionToMove = newPosition;
		} else {
			// Moving up in list, other reports move down
			offsetPosition = 1;
			topPositionToMove = newPosition;
			bottomPositionToMove = currentPosition - 1;
		}

		reportUserDao.cascadeFavoriteReportSorting(userId, offsetPosition, topPositionToMove, bottomPositionToMove);

		reportUser.setSortOrder(newPosition);
		reportUserDao.save(reportUser);
	}

	public ReportUser connectReportUser(int userId, int reportId) {
		ReportUser reportUser;

		try {
			reportUser = reportUserDao.findOne(userId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportUser = new ReportUser(userId, report);
			reportUser.setAuditColumns(new User(userId));
			reportUser.setFavorite(false);
			reportUserDao.save(reportUser);
		}

		return reportUser;
	}

	/**
	 * Create permissions to access the report permissions.
	 * 
	 * @param permissions Permissions object from request
	 * @param id Could be either the User ID or Group ID to share with
	 * @param reportId 
	 * @param editable
	 * @return
	 */
	public ReportPermissionUser connectReportPermissionUser(Permissions permissions, int id, int reportId, boolean editable) {
		ReportPermissionUser reportPermissionUser;

		try {
			reportPermissionUser = reportPermissionUserDao.findOne(id, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportPermissionUser = new ReportPermissionUser(id, report);
			reportPermissionUser.setAuditColumns(new User(permissions.getUserId()));
		}

		reportPermissionUser.setEditable(editable);
		reportPermissionUserDao.save(reportPermissionUser);

		return reportPermissionUser;
	}
	
	public ReportPermissionAccount connectReportPermissionAccount(int accountId, int reportId, Permissions permissions) {
		ReportPermissionAccount reportPermissionAccount;

		try {
			reportPermissionAccount = reportPermissionAccountDao.findOne(accountId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			reportPermissionAccount = new ReportPermissionAccount(accountId, report);
			reportPermissionAccount.setAuditColumns(new User(permissions.getUserId()));
		}

		reportPermissionAccountDao.save(reportPermissionAccount);

		return reportPermissionAccount;
	}

	// The code in this method was commented out to temporarily disable this functionality
	public void disconnectReportPermissionUser(int id, int reportId) {
//		try {
//			reportPermissionUserDao.revokePermissions(id, reportId);
//		} catch (NoResultException nre) {
//
//		}
	}

	// The code in this method was commented out to temporarily disable this functionality
	public void disconnectReportPermissionAccount(int accountId, int reportId) {
//		ReportPermissionAccount reportPermissionAccount;
//
//		try {
//			reportPermissionAccount = reportPermissionAccountDao.findOne(accountId, reportId);
//			reportPermissionAccountDao.remove(reportPermissionAccount);
//		} catch (NoResultException nre) {
//
//		}
	}

	public void removeAndCascade(Report report) {
		List<ReportPermissionUser> reportPermissionUsers = reportPermissionUserDao.findAllByReportId(report.getId());
		for (ReportPermissionUser reportPermissionUser : reportPermissionUsers) {
			reportPermissionUserDao.remove(reportPermissionUser);
		}

		List<ReportPermissionAccount> reportPermissionAccounts = reportPermissionAccountDao.findAllByReportId(report
				.getId());
		for (ReportPermissionAccount reportPermissionAccount : reportPermissionAccounts) {
			reportPermissionAccountDao.remove(reportPermissionAccount);
		}
	}
}