package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class ReportUserDAO extends PicsDAO {

	private static final Logger logger = LoggerFactory.getLogger(ReportUserDAO.class);

	public ReportUser findOne(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		return findOne(ReportUser.class, query);
	}

	public List<ReportUser> findTenMostFavoritedReports(int userId, int accountId) {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();

		try {
			SelectSQL sql = setupSqlForSearchFilterQuery(userId, accountId);

			sql.setLimit(10);

			Database db = new Database();
			List<BasicDynaBean> results = db.select(sql.toString(), false);
			reportUsers = populateReportUsers(results);
		} catch (Exception e) {
			logger.error("Unexpected exception in findTopTenFavoriteReports()");
		}

		return reportUsers;
	}
	
	private List<ReportUser> populateReportUsers(List<BasicDynaBean> results) {
		List<ReportUser> reportUsers = new ArrayList<ReportUser>();

		for (BasicDynaBean result : results) {
			Report report = new Report();

			report.setId(Integer.parseInt(result.get("id").toString()));
			report.setName(result.get("name").toString());
			report.setDescription(result.get("description").toString());

			Object userName = result.get("userName");
			Object userID = result.get("userId");

			if (userName != null) {
				User user = new User(userName.toString());
				user.setId(Integer.parseInt(userID.toString()));
			}
			
			report.setNumTimesFavorited(Integer.parseInt(result.get("numTimesFavorited").toString()));

			ReportUser reportUser = new ReportUser(0, report);
			Object favorite = result.get("favorite");
			if (favorite != null)
				reportUser.setFavorite(Boolean.parseBoolean(favorite.toString()));
			reportUsers.add(reportUser);
		}

		return reportUsers;
	}

	public List<ReportUser> findAll(int userId) {
		String query = "t.user.id = " + userId;
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllFavorite(int userId) {
		String where = "t.user.id = " + userId + " AND favorite = 1";
		String orderBy = "sortOrder";
		int limit = 0;

		List<ReportUser> reportUsers = findWhere(ReportUser.class, where, limit, orderBy);

		return reportUsers;
	}

	public List<ReportUser> findAllByReportId(int reportId) {
		String query = "t.report.id = " + reportId;
		return findWhere(ReportUser.class, query);
	}

	public int getFavoriteCount(int userId) throws SQLException, Exception {
		int favoriteCount = 0;

		SelectSQL sql = new SelectSQL("report_user");
		sql.addField("count(reportID) AS favoriteCount");
		sql.addWhere("userID = " + userId + " AND favorite = 1");

		Database database = new Database();
		List<BasicDynaBean> results = database.select(sql.toString(), false);

		if (CollectionUtils.isNotEmpty(results)) {
			Long favoriteCountLong = (Long) results.get(0).get("favoriteCount");
			favoriteCount = favoriteCountLong.intValue();
		}

		return favoriteCount;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void cascadeFavoriteReportSorting(int userId, int offset, int start, int end) throws SQLException {
		String sql = "UPDATE report_user" +
				" SET sortOrder = sortOrder + " + offset +
				" WHERE userID = " + userId +
				" AND sortOrder >= " + start +
				" AND sortOrder <= " + end;

		Query query = em.createNativeQuery(sql);
		query.executeUpdate();
	}

	public static SelectSQL setupSqlForSearchFilterQuery(int userId, int accountId) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id");
		sql.addField("r.name");
		sql.addField("r.description");
		sql.addField("u.name as userName");
		sql.addField("u.id as userId");
		sql.addField("rpuru.favorite as favorite");
		sql.addField("COUNT(DISTINCT ru.id) AS numTimesFavorited");

		sql.addGroupBy("r.id");

		sql.addJoin("LEFT JOIN report_user as ru ON r.id = ru.reportID AND ru.favorite = 1");
		sql.addJoin("LEFT JOIN report_permission_user AS rpu ON r.id = rpu.reportID");
		sql.addJoin("LEFT JOIN report_user as rpuru ON rpu.userID = rpuru.userID AND r.id = ru.reportID");
		sql.addJoin("LEFT JOIN users as u ON rpu.userID = u.id");
		sql.addJoin("LEFT JOIN report_permission_account AS rpa ON r.id = rpa.reportID");
		sql.addWhere("rpu.userID = " + userId + " OR rpa.accountID = " + accountId);
		sql.addOrderBy("numTimesFavorited DESC");

		return sql;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void resetSortOrder(int userId) throws SQLException {
		String sql = "UPDATE report_user ru " +
				"JOIN (SELECT t.id FROM report_user t " +
				"		JOIN (SELECT @row := 0) r " +
				"		WHERE favorite = 1 " +
				"		AND userID = " + userId + 
				"		ORDER BY userID, sortOrder) AS t ON ru.id = t.id " +
				" SET sortOrder = @row := @row + 1";

		Database db = new Database();
		db.executeUpdate(sql);
	}

}