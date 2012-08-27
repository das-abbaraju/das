package com.picsauditing.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.util.Strings;

public class ReportModel {

	@Autowired
	private ReportDAO reportDao;
	@Autowired
	private ReportUserDAO reportUserDao;

	private static final List<Integer> baseReports =
			Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));

	public boolean canUserViewAndCopy(int userId, Report report) {
		if (report == null)
			return false;

		return canUserViewAndCopy(userId, report.getId());
	}

	public boolean canUserViewAndCopy(int userId, int reportId) {
		if (baseReports.contains(reportId))
			return true;

		if (reportDao.isPublic(reportId))
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
		if (!canUserViewAndCopy(user.getId(), sourceReport))
			throw new NoRightsException("User " + user.getId() + " does not have permission to copy report " + sourceReport.getId());

		Report newReport = copyReportWithoutPermissions(sourceReport);

		// TODO the front end is passing new report data in the current report,
		// so we need to change sourceReport to it's old state.
		// Is this is the desired behavior?
		reportDao.refresh(sourceReport);

		reportDao.save(newReport, user);
		connectReportToUser(newReport, user);
		// This is a new report owned by the user, unconditionally give them edit permission
		reportUserDao.setEditPermissions(newReport, user, true);

		return newReport;
	}

	public void edit(Report report, Permissions permissions) throws Exception {
		if (!canUserEdit(permissions.getUserId(), report))
			throw new NoRightsException("User " + permissions.getUserId() + " cannot edit report " + report.getId());

		reportDao.save(report, new User(permissions.getUserId()));
	}

	public static Map<String, Field> buildAvailableFields(AbstractTable baseTable, Permissions permissions) {
		Map<String, Field> availableFields = new HashMap<String, Field>();

		addAllAvailableFields(availableFields, baseTable);

		Iterator<String> iterator = availableFields.keySet().iterator();
		while(iterator.hasNext()) {
			String fieldName = iterator.next();
			Field field = availableFields.get(fieldName);

			if (!field.canUserSeeQueryField(permissions)) {
				iterator.remove();
			}
		}

		return availableFields;
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
	 * This method is recursively building the available fields. It works like this
	 * because the set of tables that comprise available fields for a model is a tree,
	 * which we've decided to walk recursively.
	 */
	private static void addAllAvailableFields(Map<String, Field> availableFields, AbstractTable table) {
		availableFields.putAll(table.getAvailableFields());

		for (AbstractTable joinTable : table.getJoins()) {
			addAllAvailableFields(availableFields, joinTable);
		}
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

	public List<ReportUser> getUserReportsForSearch(String searchTerm, int userId) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		if (Strings.isEmpty(searchTerm)) {
			// By default, show the top ten most favorited reports sorted by number of favorites
			userReports = reportUserDao.findTenMostFavoritedReports(userId);
		} else {
			// Otherwise, search on all public reports and all of the user's reports
			userReports = reportUserDao.findAllForSearchFilter(userId, searchTerm);
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
			Report report = reportDao.findOne(reportId);
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

	public ReportUser connectReportToUser(Report report, User user) {
		ReportUser userReport = new ReportUser();

		userReport.setAuditColumns(user);
		userReport.setReport(report);
		userReport.setUser(user);
		userReport.setEditable(false);
		userReport.setLastOpened(new Date());

		reportUserDao.save(userReport);

		return userReport;
	}

	public ReportUser connectReportToUser(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);
		userReport.setAuditColumns(new User(userId));
		userReport.setLastOpened(new Date());

		reportUserDao.save(userReport);

		return userReport;
	}


	public ReportUser connectReportToUserEditable(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);
		userReport.setAuditColumns(new User(userId));
		userReport.setLastOpened(new Date());
		userReport.setEditable(true);

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
}
