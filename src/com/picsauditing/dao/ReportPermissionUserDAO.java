package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("unchecked")
public class ReportPermissionUserDAO extends PicsDAO {

	public ReportPermissionUser findOne(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String query = "t.user.id = " + userId + " AND t.report.id = " + reportId;
		return findOne(ReportPermissionUser.class, query);
	}

	public List<ReportPermissionUser> findAll(int userId) {
		String query = "t.user.id = " + userId;
		return findWhere(ReportPermissionUser.class, query);
	}

	public List<ReportPermissionUser> findAllEditable(int userId) {
		String query = "t.user.id = " + userId + " AND editable = 1";
		return findWhere(ReportPermissionUser.class, query);
	}

	public List<ReportPermissionUser> findAllSortByAlpha(int userId, String direction) {
		List<ReportPermissionUser> reportPermissionUsers = new ArrayList<ReportPermissionUser>();

		String where = "t.user.id = " + userId;
		String orderBy = "t.report.name " + direction;
		
		reportPermissionUsers = findWhere(ReportPermissionUser.class, where, 0, orderBy);

		return reportPermissionUsers;
	}

	public List<ReportPermissionUser> findAllSortByDateAdded(int userId, String direction) {
		List<ReportPermissionUser> reportPermissionUsers = new ArrayList<ReportPermissionUser>();

		String where = "t.user.id = " + userId;
		String orderBy = "creationDate " + direction;

		reportPermissionUsers = findWhere(ReportPermissionUser.class, where, 0, orderBy);

		return reportPermissionUsers;
	}

	public List<ReportPermissionUser> findAllSortByLastViewed(int userId, String direction) {
		String orderBy = "ru.lastViewedDate " + direction;
		
		SelectSQL sql = new SelectSQL("report_permission_user rpu");
		sql.addField("rpu.*");
		sql.addJoin("JOIN report_user ru ON ru.userID = rpu.userID AND ru.reportID = rpu.reportID");
		sql.addWhere("rpu.userID = :userID");
		sql.addOrderBy(orderBy);
		
		Query query = em.createNativeQuery(sql.toString(), ReportPermissionUser.class);

		query.setParameter("userID", userId);
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllByReportId(int reportId) {
		String query = "t.report.id = " + reportId;
		return findWhere(ReportPermissionUser.class, query);
	}
}