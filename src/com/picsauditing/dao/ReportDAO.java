package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class ReportDAO extends PicsDAO {

	// TODO temp solution for save not working. I have no clue about this
	@Autowired
	private BasicDAO basicDao;

	private Database database = null;

	private static final Logger logger = LoggerFactory.getLogger(ReportDAO.class);

	public Report findOneReport(int id) throws NoResultException {
		return findOne(Report.class, "t.id = " + id);
	}

	public List<Report> findPublicReports() {
		String query = "private = 0";
		List<Report> publicReports = findWhere(Report.class, query);

		return publicReports;
	}

	public List<ReportUser> findTenMostFavoritedReports(int userId) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		try {
			SelectSQL sql = setupSqlForSearchFilterQuery(userId);

			sql.setLimit(10);

			Database db = new Database();
			List<BasicDynaBean> results = db.select(sql.toString(), false);
			userReports = ReportModel.populateUserReports(results);
		} catch (SQLException se) {
			logger.warn("SQL Exception in findTopTenFavoriteReports()", se);
		} catch (Exception e) {
			logger.error("Unexpected exception in findTopTenFavoriteReports()");
		}

		return userReports;
	}

	public List<ReportUser> findUserReportsForSearchFilter(int userId, String dirtyQuery) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		// TODO escape properly
		String query = Strings.escapeQuotes(dirtyQuery);

		try {
			SelectSQL sql = setupSqlForSearchFilterQuery(userId);

			sql.addWhere("r.name LIKE \"%" + query + "%\" OR r.description LIKE \"%" + query + "%\"");

			Database db = new Database();
			List<BasicDynaBean> results = db.select(sql.toString(), false);
			userReports = ReportModel.populateUserReports(results);
		} catch (SQLException se) {
			logger.warn("SQL Exception in findReportsForSearchFilter()", se);
		} catch (Exception e) {
			logger.error("Unexpected exception in findReportsForSearchfilter()");
		}

		return userReports;
	}

	private SelectSQL setupSqlForSearchFilterQuery(int userId) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id");
		sql.addField("r.name");
		sql.addField("r.description");
		sql.addField("u.name as userName");
		sql.addField("u.id as userId");
		sql.addField("count(ru.favorite) as numTimesFavorited");

		sql.addGroupBy("r.id");

		sql.addJoin("LEFT JOIN report_user as ru ON r.id = ru.reportID AND ru.favorite = 1");
		sql.addJoin("JOIN users as u ON r.createdBy = u.id");

		sql.addWhere("r.private = 0 OR r.createdBy = " + userId);

		sql.addOrderBy("numTimesFavorited DESC");

		return sql;
	}

	public List<Report> findAllReports() {
		return findAll(Report.class);
	}

	public ReportUser findOneUserReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		return findOne(ReportUser.class, query);
	}

	public ReportUser findOneUserReportByFavoriteSortIndex(int userId, int favoriteSortIndex) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.favoriteSortIndex = " + favoriteSortIndex;
		return findOne(ReportUser.class, query);
	}

	public List<ReportUser> findFavoriteUserReports(int userId) {
		String query = "t.user.id = " + userId + " AND favorite = 1";
		List<ReportUser> userReports = findWhere(ReportUser.class, query);

		Collections.sort(userReports, new Comparator<ReportUser>() {
			@Override
			public int compare(ReportUser ru1, ReportUser ru2) {
				return ru1.getFavoriteSortIndex() - ru2.getFavoriteSortIndex();
			}
		});

		return userReports;
	}

	public List<ReportUser> findEditableUserReports(int userId) {
		String query = "t.user.id = " + userId + " AND editable = 1";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findUserReports(int userId) {
		String query = "t.user.id = " + userId;
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findUserReportsSortByAlphaAsc(int userId) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		try {
			userReports = findUserReports(userId);

			// Sort by report name, ignoring case
			Collections.sort(userReports, new Comparator<ReportUser>() {
				@Override
				public int compare(ReportUser ru1, ReportUser ru2) {
					return ru1.getReport().getName().compareToIgnoreCase(ru2.getReport().getName());
				}
			});
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDAO.findAllUserReportsByAlpha()", e);
		}

		return userReports;
	}

	public List<ReportUser> findUserReportsSortByAlphaDesc(int userId) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		try {
			userReports = findUserReports(userId);

			// Sort by report name, ignoring case
			Collections.sort(userReports, new Comparator<ReportUser>() {
				@Override
				public int compare(ReportUser ru1, ReportUser ru2) {
					return ru2.getReport().getName().compareToIgnoreCase(ru1.getReport().getName());
				}
			});
		} catch (Exception e) {
			logger.error("Unexpected exception in ReportDAO.findAllUserReportsByAlpha()", e);
		}

		return userReports;
	}

	public List<ReportUser> findUserReportsSortByDateAddedAsc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY creationDate ASC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findUserReportsSortByDateAddedDesc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY creationDate DESC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findUserReportsSortByLastUsedAsc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY lastOpened ASC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findUserReportsSortByLastUsedDesc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY lastOpened DESC";
		return findWhere(ReportUser.class, query);
	}

	public void refreshReport(Report report) {
		refresh(report);
	}

	public void saveReport(Report report, User user) throws ReportValidationException {
		ReportModel.validate(report);
		report.setAuditColumns(user);

		basicDao.save(report);
	}

	public void deleteReport(Report report) {
		List<ReportUser> userReports = findWhere(ReportUser.class, "t.report.id = " + report.getId());
		for (ReportUser userReport : userReports) {
			basicDao.remove(userReport);
		}

		basicDao.remove(report);
	}

	public boolean isReportPublic(int reportId) {
		try {
			Report report = findOneReport(reportId);
			if (report != null && report.isPublic()) {
				return true;
			}
		} catch (NoResultException nre) {
			// If the report doesn't exist, it's not public
		}

		return false;
	}

	public ReportUser connectReportToUser(Report report, User user) {
		ReportUser userReport = new ReportUser();

		userReport.setAuditColumns(user);
		userReport.setReport(report);
		userReport.setUser(user);
		userReport.setEditable(false);
		userReport.setLastOpened(new Date());

		basicDao.save(userReport);

		return userReport;
	}

	public ReportUser connectReportToUser(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);
		userReport.setAuditColumns(new User(userId));
		userReport.setLastOpened(new Date());

		basicDao.save(userReport);

		return userReport;
	}

	public ReportUser connectReportToUserEditable(Report report, int userId) {
		ReportUser userReport = new ReportUser(userId, report);
		userReport.setAuditColumns(new User(userId));
		userReport.setLastOpened(new Date());
		userReport.setEditable(true);

		basicDao.save(userReport);

		return userReport;
	}

	public void grantEditPermission(Report report, User user) {
		setUserEditPermissions(report, user, true);
	}

	public void revokeEditPermission(Report report, User user) {
		setUserEditPermissions(report, user, false);
	}

	private void setUserEditPermissions(Report report, User user, boolean value) throws NoResultException, NonUniqueResultException {
		ReportUser userReport = findOneUserReport(user.getId(), report.getId());
		userReport.setEditable(value);
		basicDao.save(userReport);
	}

	public void removeUserReport(User user, Report report) throws NoResultException, NonUniqueResultException {
		removeUserReport(user.getId(), report.getId());
	}

	public void removeUserReport(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		ReportUser userReport = findOneUserReport(userId, reportId);
		basicDao.remove(userReport);
	}

	@SuppressWarnings("unchecked")
	public List<BasicDynaBean> runQuery(SelectSQL sql, JSONObject json) throws SQLException {
		Database db = database();
		List<BasicDynaBean> rows = db.select(sql.toString(), true);
		json.put("total", db.getAllRows());

		return rows;
	}

	public void updateLastOpened(int userId, int reportId) {
		ReportUser userReport;

		try {
			userReport = findOneUserReport(userId, reportId);
		} catch (NoResultException nre) {
			// If the user is viewing a new public report, can't update this yet
			return;
		}

		userReport.setLastOpened(new Date());
		basicDao.save(userReport);
	}

	public void cascadeFavoriteReportSorting(int userId, int offset, int start, int end) throws SQLException {
		String query = "UPDATE report_user" +
				" SET favoriteSortIndex = favoriteSortIndex + " + offset +
				" WHERE userID = " + userId +
				" AND favoriteSortIndex >= " + start +
				" AND favoriteSortIndex <= " + end;

		database().executeUpdate(query);
	}

	public int getFavoriteCount(int userId) throws SQLException, Exception {
		SelectSQL sql = new SelectSQL("report_user");
		sql.addField("count(reportID) AS favoriteCount");
		sql.addWhere("userID = " + userId + " AND favorite = 1");

		List<BasicDynaBean> results = database().select(sql.toString(), false);

		int favoriteCount;
		if (CollectionUtils.isNotEmpty(results)) {
			Long favoriteCountLong = (Long) results.get(0).get("favoriteCount");
			favoriteCount = favoriteCountLong.intValue();
		} else {
			favoriteCount = findFavoriteUserReports(userId).size();
		}

		return favoriteCount;
	}

	private Database database() {
		if (database == null) {
			database = new Database();
		}

		return database;
	}
}
