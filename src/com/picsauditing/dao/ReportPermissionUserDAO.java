package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ReportPermissionUser;

@SuppressWarnings("unchecked")
public class ReportPermissionUserDAO extends PicsDAO {
	
	private static final String FIND_REPORTS_FOR_USER = "SELECT rpu.*, r.name, ru.lastViewedDate FROM report_permission_user rpu " + 
			"JOIN report r ON r.id = rpu.reportID " +
			"LEFT JOIN report_user ru on ru.reportID = rpu.reportID AND ru.userID = :userId " +
			"WHERE rpu.userID = :userId ";
	
	private static final String FIND_REPORTS_FOR_GROUP = "SELECT rpu.*, r.name, ru.lastViewedDate " + 
			"FROM report_permission_user rpu " +
			"JOIN report r ON r.id = rpu.reportID " +
			"LEFT JOIN report_user ru on ru.reportID = rpu.reportID AND ru.userID = :userId " +
			"WHERE rpu.userID IN ( :groupIds ) ";

	private static final String FIND_REPORTS_BASED_ON_USER_PERMISSIONS = "SELECT DISTINCT * FROM ( " + 
			"%s UNION %s ) t GROUP BY t.reportID ";
	
	private static final String FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS = 
			FIND_REPORTS_BASED_ON_USER_PERMISSIONS + " ORDER BY %s ";
	
	public ReportPermissionUser findOne(int id, int reportId) throws NoResultException, NonUniqueResultException {
		Query query = em.createNativeQuery(FIND_REPORTS_FOR_USER + " AND rpu.reportID = :reportId ");
		query.setParameter("userId", id);
		query.setParameter("reportId", reportId);
		
		return (ReportPermissionUser) query.getSingleResult();
	}
	
	public ReportPermissionUser findOne(Permissions permissions, int reportId) throws NoResultException, NonUniqueResultException {
		String findReportForUser = FIND_REPORTS_FOR_USER + " AND rpu.reportID = :reportId ";
		String findReportForGroup = FIND_REPORTS_FOR_GROUP + " AND rpu.reportID = :reportId ";

		// The reason a Limit is needed is that we are searching for user permissions and group permissions 
		String sql = String.format(FIND_REPORTS_BASED_ON_USER_PERMISSIONS, findReportForUser, findReportForGroup) + " LIMIT 1 ";
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("reportId", reportId);
		query.setParameter("groupIds", permissions.getGroupIds());
		
		return (ReportPermissionUser) query.getSingleResult();
	}
	
	@Transactional(propagation = Propagation.NESTED)
	public int revokePermissions(int userId, int reportId) {
		Query query = em.createQuery("DELETE FROM ReportPermissionUser rpu WHERE rpu.user.id = :userId AND rpu.report.id = :reportId ");
		query.setParameter("userId", userId);
		query.setParameter("reportId", userId);
		
		return query.executeUpdate();		
	}

	public List<ReportPermissionUser> findAll(Permissions permissions) {
		String sql = String.format(FIND_REPORTS_BASED_ON_USER_PERMISSIONS, FIND_REPORTS_FOR_USER, 
				FIND_REPORTS_FOR_GROUP);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getGroupIds());
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllEditable(Permissions permissions) {
		String findReportForUser = FIND_REPORTS_FOR_USER + " AND editable = 1";
		String findReportForGroup = FIND_REPORTS_FOR_GROUP + " AND editable = 1";
		String sql = String.format(FIND_REPORTS_BASED_ON_USER_PERMISSIONS, findReportForUser, findReportForGroup);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getGroupIds());
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllSortByAlpha(Permissions permissions, String direction) {
		String sql = String.format(FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS, FIND_REPORTS_FOR_USER, 
				FIND_REPORTS_FOR_GROUP, "name " + direction);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getGroupIds());
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllSortByDateAdded(Permissions permissions, String direction) {
		String sql = String.format(FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS, FIND_REPORTS_FOR_USER, 
				FIND_REPORTS_FOR_GROUP, "creationDate " + direction);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getGroupIds());
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllSortByLastViewed(Permissions permissions, String direction) {
		String sql = String.format(FIND_ORDERED_REPORTS_BASED_ON_USER_PERMISSIONS, FIND_REPORTS_FOR_USER, 
				FIND_REPORTS_FOR_GROUP, "lastViewedDate " + direction);
		
		Query query = em.createNativeQuery(sql, ReportPermissionUser.class);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getGroupIds());
		
		return query.getResultList();
	}

	public List<ReportPermissionUser> findAllByReportId(int reportId) {
		String query = "t.report.id = " + reportId;
		return findWhere(ReportPermissionUser.class, query);
	}
}