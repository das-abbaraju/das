package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.Collections;
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

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.mapper.ReportInfoMapper;
import com.picsauditing.dao.mapper.UserMapper;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.service.ReportInfo;
import com.picsauditing.util.Strings;

public class ReportUserDAO extends PicsDAO {

	private static final Logger logger = LoggerFactory.getLogger(ReportUserDAO.class);

	public ReportUser findOne(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		return findOne(ReportUser.class, query);
	}

	public List<ReportInfo> findTenMostFavoritedReports(Permissions permissions) {
		try {
			SelectSQL sql = setupSqlForSearchFilterQuery(permissions);
			sql.setLimit(10);
			return Database.select(sql.toString(), new ReportInfoMapper());
		} catch (Exception e) {
			logger.error("Unexpected exception in findTopTenFavoriteReports()");
		}

		return Collections.emptyList();
	}

	public List<ReportUser> findAll(int userId) {
		String query = "t.user.id = " + userId;
		return findWhere(ReportUser.class, query);
	}

	public List<ReportUser> findAllFavorite(int userId) {
		String where = "t.user.id = " + userId + " AND favorite = 1";
		String orderBy = "sortOrder DESC";
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
	public void offsetSortOrderForRange(int userId, int offset, int start, int end) throws SQLException {
		String sql = "UPDATE report_user SET sortOrder = sortOrder + " + offset + " WHERE userID = " + userId
				+ " AND sortOrder >= " + start + " AND sortOrder <= " + end;

		Query query = em.createNativeQuery(sql);
		query.executeUpdate();
	}

	public static SelectSQL setupSqlForSearchFilterQuery(Permissions permissions) {
		SelectSQL sql = new SelectSQL("report r");

		sql.addField("r.id AS " + ReportInfoMapper.ID_FIELD);
		sql.addField("r.name AS " + ReportInfoMapper.NAME_FIELD);
		sql.addField("r.description AS " + ReportInfoMapper.DESCRIPTION_FIELD);
		sql.addField("r.creationDate AS " + ReportInfoMapper.CREATION_DATE_FIELD);
		sql.addField("f.total AS " + ReportInfoMapper.NUMBER_OF_TIMES_FAVORITED);
		sql.addField("0 AS " + ReportInfoMapper.FAVORITE_FIELD);
		sql.addField("0 AS " + ReportInfoMapper.EDITABLE_FIELD);
		sql.addField("NULL AS " + ReportInfoMapper.LAST_VIEWED_DATE_FIELD);
		sql.addField("u.id AS '" + UserMapper.USER_ID_FIELD + "'");
		sql.addField("u.name AS '" + UserMapper.USER_NAME_FIELD + "'");

		sql.addJoin("LEFT JOIN users AS u ON r.createdBy = u.id");
//		sql.addJoin("LEFT JOIN report_user as ru");
		sql.addJoin("LEFT JOIN (SELECT reportID, SUM(favorite) total, SUM(viewCount) viewCount FROM report_user GROUP BY reportID) AS f ON r.id = f.reportID");

		String permissionsUnion = "SELECT reportID FROM report_permission_user WHERE userID = " + permissions.getUserId()
				+ " UNION SELECT reportID FROM report_permission_user WHERE userID IN (" + Strings.implode(permissions.getAllInheritedGroupIds()) + ")"
				+ " UNION SELECT reportID FROM report_permission_account WHERE accountID = " + permissions.getAccountId();
		sql.addWhere("r.id IN (" + permissionsUnion + ")");

		sql.addOrderBy("f.total DESC");
		sql.addOrderBy("f.viewCount DESC");
		sql.addOrderBy("r.creationDate");

		return sql;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void resetSortOrder(int userId) throws SQLException {
		String sql = "UPDATE report_user ru " + "JOIN (SELECT t.id FROM report_user t "
				+ "		JOIN (SELECT @row := 0) r " + "		WHERE favorite = 1 " + "		AND userID = " + userId
				+ "		ORDER BY userID, sortOrder) AS t ON ru.id = t.id " + " SET sortOrder = @row := @row + 1";

		Database db = new Database();
		db.executeUpdate(sql);
	}

	public int findMaxSortIndex(int userId) {
		Query query = em.createQuery("SELECT MAX(ru.sortOrder) FROM ReportUser ru WHERE ru.user.id = :userId");
		query.setParameter("userId", userId);

		int result;
		try {
			result = (Integer) query.getSingleResult();
		} catch (Exception e) {
			result = 0;
		}

		return result;
	}

}
