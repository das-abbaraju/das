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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.ReportModel;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class ReportUserDAO extends PicsDAO {

	private static final Logger logger = LoggerFactory.getLogger(ReportUserDAO.class);

	@Transactional(propagation = Propagation.NESTED)
	public ReportUser save(ReportUser userReport) {
		if (userReport.getId() == 0) {
			em.persist(userReport);
		} else {
			userReport = em.merge(userReport);
		}

		return userReport;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int userId, int reportId) {
		ReportUser userReport = findOne(userId, reportId);
		remove(userReport);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(ReportUser userReport) {
		if (userReport != null) {
			em.remove(userReport);
		}
	}

	public ReportUser findOne(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		return findOne(ReportUser.class, query);
	}

	public ReportUser findOneByFavoriteIndex(int userId, int favoriteSortIndex) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.favoriteSortIndex = " + favoriteSortIndex;
		return findOne(ReportUser.class, query);
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

	public List<ReportUser> findAll(int userId) {
		String query = "t.user.id = " + userId;
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllFavorite(int userId) {
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

	public List<ReportUser> findAllEditable(int userId) {
		String query = "t.user.id = " + userId + " AND editable = 1";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllSortByAlphaAsc(int userId) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		try {
			userReports = findAll(userId);

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

	public List<ReportUser> findAllSortByAlphaDesc(int userId) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		try {
			userReports = findAll(userId);

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

	public List<ReportUser> findAllSortByDateAddedAsc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY creationDate ASC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllSortByDateAddedDesc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY creationDate DESC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllSortByLastUsedAsc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY lastOpened ASC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllSortByLastUsedDesc(int userId) {
		String query = "t.user.id = " + userId + " ORDER BY lastOpened DESC";
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllForSearchFilter(int userId, String dirtyQuery) {
		List<ReportUser> userReports = new ArrayList<ReportUser>();

		// TODO escape properly
		String query = "\"%" + Strings.escapeQuotes(dirtyQuery) + "%\"";

		try {
			SelectSQL sql = setupSqlForSearchFilterQuery(userId);

			sql.addWhere("r.name LIKE " + query +
					" OR r.description LIKE " + query +
					" OR u.name LIKE " + query);

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

	public List<ReportUser> findAllByReportId(int reportId) {
		String query = "t.report.id = " + reportId;
		return findWhere(ReportUser.class, query);
	}

	public void setEditPermissions(Report report, User user, boolean value) throws NoResultException, NonUniqueResultException {
		ReportUser userReport = findOne(user.getId(), report.getId());
		userReport.setEditable(value);
		save(userReport);
	}

	public int getFavoriteCount(int userId) throws SQLException, Exception {
		SelectSQL sql = new SelectSQL("report_user");
		sql.addField("count(reportID) AS favoriteCount");
		sql.addWhere("userID = " + userId + " AND favorite = 1");

		Database database = new Database();
		List<BasicDynaBean> results = database.select(sql.toString(), false);

		int favoriteCount;

		if (CollectionUtils.isNotEmpty(results)) {
			Long favoriteCountLong = (Long) results.get(0).get("favoriteCount");
			favoriteCount = favoriteCountLong.intValue();
		} else {
			favoriteCount = findAllFavorite(userId).size();
		}

		return favoriteCount;
	}

	public void updateLastOpened(int userId, int reportId) {
		ReportUser userReport;

		try {
			userReport = findOne(userId, reportId);
		} catch (NoResultException nre) {
			// If the user is viewing a new public report, can't update this yet
			return;
		}

		userReport.setLastOpened(new Date());
		save(userReport);
	}

	public void cascadeFavoriteReportSorting(int userId, int offset, int start, int end) throws SQLException {
		String query = "UPDATE report_user" +
				" SET favoriteSortIndex = favoriteSortIndex + " + offset +
				" WHERE userID = " + userId +
				" AND favoriteSortIndex >= " + start +
				" AND favoriteSortIndex <= " + end;

		Database database = new Database();
		database.executeUpdate(query);
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
}
