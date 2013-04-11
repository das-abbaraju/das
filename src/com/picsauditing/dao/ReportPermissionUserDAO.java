package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ReportPermissionUser;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings( {"unchecked", "deprecation"} )
public class ReportPermissionUserDAO extends PicsDAO {

	public ReportPermissionUser findOne(int userId, int reportId) throws NoResultException, NonUniqueResultException {
		return findOne(ReportPermissionUser.class, "t.user.id = " + userId + " AND t.report.id = " + reportId);
	}

	public ReportPermissionUser findOneByPermissions(Permissions permissions, int reportId) throws NoResultException {
		SelectSQL sql = new SelectSQL("report_permission_user rpu");
		sql.addWhere("rpu.reportID = :reportId");
		sql.addWhere("rpu.userID = :userId OR rpu.userID IN ( :groupIds )");
		sql.addOrderBy("editable DESC");
		sql.setLimit(1);

		Query query = em.createNativeQuery(sql.toString(), ReportPermissionUser.class);
		query.setParameter("reportId", reportId);
		query.setParameter("userId", permissions.getUserId());
		query.setParameter("groupIds", permissions.getAllInheritedGroupIds());
		return (ReportPermissionUser) query.getSingleResult();
	}

	@Transactional(propagation = Propagation.NESTED)
	public int revokePermissions(int userId, int reportId) {
		String sql = "DELETE FROM ReportPermissionUser rpu WHERE rpu.user.id = :userId AND rpu.report.id = :reportId ";

		Query query = em.createQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("reportId", userId);

		return query.executeUpdate();
	}

	public List<ReportPermissionUser> findAllByReportId(int reportId) {
		return findWhere(ReportPermissionUser.class, "t.report.id = " + reportId);
	}

	public List<ReportPermissionUser> findAllUsersByReportId(int reportId) {
		return findWhere(ReportPermissionUser.class, "t.report.id = " + reportId + " AND t.user.isGroup = 'No'");
	}

	public List<ReportPermissionUser> findAllGroupsByReportId(int reportId) {
		return findWhere(ReportPermissionUser.class, "t.report.id = " + reportId + " AND t.user.isGroup = 'Yes'");
	}
}