package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import com.picsauditing.jpa.entities.ReportPermissionUser;

@SuppressWarnings("unchecked")
public class ReportPermissionUserDAO extends PicsDAO {
	
	private static final String FIND_REPORTS_FOR_USER = "SELECT rpu.*, r.name, ru.lastViewedDate FROM report_permission_user rpu " + 
			"JOIN report r ON r.id = rpu.reportID " +
			"JOIN report_user ru on ru.reportID = rpu.reportID AND ru.userID = :userId " +
			"WHERE rpu.userID = :userId ";
	
	private static final String FIND_REPORTS_FOR_GROUP = "SELECT rpu.*, r.name, ru.lastViewedDate FROM usergroup ug " + 
			"JOIN report_permission_user rpu ON rpu.userID = ug.groupID " +
			"JOIN report r ON r.id = rpu.reportID " +
			"JOIN report_user ru on ru.reportID = rpu.reportID AND ru.userID = :userId " +
			"WHERE ug.userID = :userId ";

	private static final String FIND_REPORTS_BASED_ON_USER_PERMISSIONS = "SELECT DISTINCT * FROM ( " + 
			"%s UNION %s ) t ";
	
	private static final String FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS = 
			FIND_REPORTS_BASED_ON_USER_PERMISSIONS + " ORDER BY %s";
	
	public ReportPermissionUser findOne(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		String findReportForUser = FIND_REPORTS_FOR_USER + " AND rpu.reportID = :reportId";
		String findReportForGroup = FIND_REPORTS_FOR_GROUP + " AND rpu.reportID = :reportId";

		// The reason a Limit is needed is that we are searching for user permissions and group permissions 
		String sql = String.format(FIND_REPORTS_BASED_ON_USER_PERMISSIONS, findReportForUser, findReportForGroup) + " LIMIT 1";
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", userId);
		query.setParameter("reportId", reportId);
		
		return (ReportPermissionUser) query.getSingleResult();
	}

	public List<ReportPermissionUser> findAll(int userId) {
		String sql = String.format(FIND_REPORTS_BASED_ON_USER_PERMISSIONS, 
				FIND_REPORTS_FOR_USER, FIND_REPORTS_FOR_GROUP);
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", userId);
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllEditable(int userId) {
		String findReportForUser = FIND_REPORTS_FOR_USER + " AND editable = 1";
		String findReportForGroup = FIND_REPORTS_FOR_GROUP + " AND editable = 1";
		String sql = String.format(FIND_REPORTS_BASED_ON_USER_PERMISSIONS, findReportForUser, findReportForGroup);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", userId);
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllSortByAlpha(int userId, String direction) {
		String sql = String.format(FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS, 
				FIND_REPORTS_FOR_USER, FIND_REPORTS_FOR_GROUP, "name " + direction);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", userId);
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllSortByDateAdded(int userId, String direction) {
		String sql = String.format(FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS, 
				FIND_REPORTS_FOR_USER, FIND_REPORTS_FOR_GROUP, "creationDate " + direction);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", userId);
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllSortByLastViewed(int userId, String direction) {
		String sql = String.format(FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS, 
				FIND_REPORTS_FOR_USER, FIND_REPORTS_FOR_GROUP, "lastViewedDate " + direction);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", userId);
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllByReportId(int reportId) {
		String query = "t.report.id = " + reportId;
		return findWhere(ReportPermissionUser.class, query);
	}
}