package com.picsauditing.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.report.ManageReports;
import com.picsauditing.dao.ReportDAO;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.ReportPaginationParameters;
import com.picsauditing.util.Strings;
import com.picsauditing.util.pagination.Pagination;

public class ReportModel {

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;

	private static final Logger logger = LoggerFactory.getLogger(ReportModel.class);

	public boolean canUserViewAndCopy(int userId, int reportId) {
		if (isReportPublic(reportId))
			return true;

		try {
			ReportUser userReport = reportUserDao.findOne(userId, reportId);
			if (userReport != null)
				return true;
		} catch (NoResultException e) {
			return false;
		}

		return false;
	}

	public boolean canUserEdit(int userId, Report report) {
		try {
			ReportUser userReport = reportUserDao.findOne(userId, report.getId());
			return userReport.isEditable();
		} catch (NoResultException e) {
			// We don't care. The user can't edit.
		}

		return false;
	}

	// This must be static because it's called from list_favorites.jsp and list_my_reports.jsp
	public static boolean canUserDelete(int userId, Report report) {
		if (report.getCreatedBy().getId() == userId)
			return true;

		return false;
	}

	public Report copy(Report sourceReport, User user) throws NoRightsException, ReportValidationException {
		if (!canUserViewAndCopy(user.getId(), sourceReport.getId()))
			throw new NoRightsException("User " + user.getId() + " does not have permission to copy report " + sourceReport.getId());

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO the front end is passing new report data in the current report,
		// so we need to change sourceReport to it's old state.
		// Is this is the desired behavior?
		reportDao.refresh(sourceReport);

		reportDao.save(newReport, user);
		// This is a new report owned by the user, unconditionally give them edit permission
		connectReportToUserEditable(newReport, user.getId());

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		if (!canUserEdit(permissions.getUserId(), report) && !permissions.isDeveloperEnvironment())
			throw new NoRightsException("User " + permissions.getUserId() + " cannot edit report " + report.getId());

		reportDao.save(report, new User(permissions.getUserId()));
	}

	private Report copyReportWithoutPermissions(Report sourceReport) {
		Report newReport = new Report();

		newReport.setModelType(sourceReport.getModelType());
		newReport.setName(sourceReport.getName());
		newReport.setDescription(sourceReport.getDescription());
		newReport.setParameters(sourceReport.getParameters());

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

		try {
			new JSONParser().parse(report.getParameters());
		} catch (ParseException e) {
			throw new ReportValidationException(e, report);
		}
	}

	/**
	 * By default, show the top ten most favorited reports sorted by number of favorites.
	 * Otherwise, search on all public reports and all of the user's reports.
	 */
	public List<ReportUser> getUserReportsForSearch(String searchTerm, int userId, Pagination<ReportUser> pagination) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		if (Strings.isEmpty(searchTerm)) {	
			userReports = reportUserDao.findTenMostFavoritedReports(userId);
		} else {
			ReportPaginationParameters parameters = new ReportPaginationParameters(userId, searchTerm);		
			pagination.Initialize(parameters, reportUserDao);
			userReports = pagination.getResults();
		}

		return userReports;
	}

	public List<ReportUser> getUserReportsForMyReports(String sort, String direction, int userId) throws IllegalArgumentException {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		if (Strings.isEmpty(sort)) {
			userReports = reportUserDao.findAll(userId);
		} else if (sort.equals(ManageReports.ALPHA_SORT)) {
			if (ManageReports.ASC.equals(direction)) {
				userReports = reportUserDao.findAllSortByAlphaAsc(userId);
			} else {
				userReports = reportUserDao.findAllSortByAlphaDesc(userId);
			}
		} else if (sort.equals(ManageReports.DATE_ADDED_SORT)) {
			if (ManageReports.ASC.equals(direction)) {
				userReports = reportUserDao.findAllSortByDateAddedAsc(userId);
			} else {
				userReports = reportUserDao.findAllSortByDateAddedDesc(userId);
			}
		} else if (sort.equals(ManageReports.LAST_OPENED_SORT)) {
			if (ManageReports.ASC.equals(direction)) {
				userReports = reportUserDao.findAllSortByLastUsedAsc(userId);
			} else {
				userReports = reportUserDao.findAllSortByLastUsedDesc(userId);
			}
		} else {
			throw new IllegalArgumentException("Unexpected sort type '" + sort + "'");
		}

		return userReports;
	}

	public static List<ReportUser> populateUserReports(List<BasicDynaBean> results) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		for (BasicDynaBean result : results) {
			Report report = new Report();

			report.setId(Integer.parseInt(result.get("id").toString()));
			report.setName(result.get("name").toString());
			report.setDescription(result.get("description").toString());

			User user = new User(result.get("userName").toString());
			user.setId(Integer.parseInt(result.get("userId").toString()));
			report.setCreatedBy(user);

			report.setNumTimesFavorited(Integer.parseInt(result.get("numTimesFavorited").toString()));

			userReports.add(new ReportUser(0, report));
		}

		return userReports;
	}

	public void favoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException, SQLException, Exception {
		ReportUser userReport;

		try {
			userReport = reportUserDao.findOne(userId, reportId);
		} catch (NoResultException nre) {
			// Need to connect user to report first
			Report report = reportDao.find(Report.class, reportId);
			userReport = connectReportToUser(report, userId);
		}

		int favoriteCount =  reportUserDao.getFavoriteCount(userId);
		reportUserDao.cascadeFavoriteReportSorting(userId, 1, 1, favoriteCount);

		userReport.setFavoriteSortIndex(1);
		userReport.setFavorite(true);
		reportDao.save(userReport);
	}

	public void unfavoriteReport(int userId, int reportId) throws NoResultException, NonUniqueResultException, SQLException, Exception {
		ReportUser unfavoritedUserReport = reportUserDao.findOne(userId, reportId);
		int removedSortIndex = unfavoritedUserReport.getFavoriteSortIndex();

		int favoriteCount =  reportUserDao.getFavoriteCount(userId);
		reportUserDao.cascadeFavoriteReportSorting(userId, -1, removedSortIndex + 1, favoriteCount);

		unfavoritedUserReport.setFavoriteSortIndex(0);
		unfavoritedUserReport.setFavorite(false);
		reportDao.save(unfavoritedUserReport);
	}

	public void moveUserReportUpOne(int userId, int reportId) throws Exception {
		ReportUser reportToMove = reportUserDao.findOne(userId, reportId);
		int prevIndex = reportToMove.getFavoriteSortIndex();
		moveUserReportToIndex(userId, reportToMove, prevIndex - 1);
	}

	public void moveUserReportDownOne(int userId, int reportId) throws Exception {
		ReportUser reportToMove = reportUserDao.findOne(userId, reportId);
		int prevIndex = reportToMove.getFavoriteSortIndex();
		moveUserReportToIndex(userId, reportToMove, prevIndex + 1);
	}

	public void moveUserReportToIndex(int userId, int reportId, int newIndex) throws Exception {
		ReportUser userReport = reportUserDao.findOne(userId, reportId);
		moveUserReportToIndex(userId, userReport, newIndex);
	}

	public void moveUserReportToIndex(int userId, ReportUser userReport, int newIndex) throws Exception {
		if (newIndex < 1 || newIndex > reportUserDao.getFavoriteCount(userId))
			return;

		if (userReport.getFavoriteSortIndex() == newIndex)
			return;

		int offset, start, end;

		if (userReport.getFavoriteSortIndex() < newIndex) {
			// Moving down in list, other reports move up
			offset = -1;
			start = userReport.getFavoriteSortIndex() + 1;
			end = newIndex;
		} else {
			// Moving up in list, other reports move down
			offset = 1;
			start = newIndex;
			end = userReport.getFavoriteSortIndex() - 1;
		}

		reportUserDao.cascadeFavoriteReportSorting(userId, offset, start, end);

		userReport.setFavoriteSortIndex(newIndex);
		reportDao.save(userReport);
	}

	public ReportUser connectReportToUser(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);

		userReport.setAuditColumns(new User(userId));
		userReport.setEditable(false);
		userReport.setFavorite(false);
		userReport.setLastOpened(new Date());

		reportUserDao.save(userReport);

		return userReport;
	}

	public ReportUser connectReportToUserEditable(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);

		userReport.setAuditColumns(new User(userId));
		userReport.setEditable(true);
		userReport.setFavorite(false);
		userReport.setLastOpened(new Date());

		reportUserDao.save(userReport);

		return userReport;
	}

	public void grantEditPermission(Report report, User user) {
		// TODO check if current user has permission to grant edit
		reportUserDao.setEditPermissions(report, user, true);
	}


	public void revokeEditPermission(Report report, User user) {
		// TODO check if current user has permission to revoke edit
		reportUserDao.setEditPermissions(report, user, false);
	}

	public void removeAndCascade(int reportId) {
		Report report = reportDao.find(Report.class, reportId);
		removeAndCascade(report);
	}

	public void removeAndCascade(Report report) {
		List<ReportUser> userReports = reportUserDao.findAllByReportId(report.getId());

		for (ReportUser userReport : userReports) {
			int userId = userReport.getUser().getId();

			try {
				moveUserReportToIndex(userId, userReport, reportUserDao.getFavoriteCount(userId));
			} catch (Exception e) {
				logger.error("Unable to get favorite count to cascade favorite indices in ReportDAO.removeAndCascade(Report)");
			}

			reportUserDao.remove(userReport);
		}

		reportDao.remove(report);
	}

	public boolean isReportPublic(int reportId) {
		try {
			Report report = reportDao.find(Report.class, reportId);
			if (report != null && report.isPublic()) {
				return true;
			}
		} catch (NoResultException nre) {
			// If the report doesn't exist, it's not public
		}

		return false;
	}
}
