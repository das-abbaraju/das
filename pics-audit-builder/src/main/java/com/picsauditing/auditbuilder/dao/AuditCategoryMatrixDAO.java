package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.AuditCategory;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class AuditCategoryMatrixDAO extends PicsDAO {
	public List<AuditCategory> findCategoriesForCompetencies(int accountID) {
		Query query = em.createQuery("SELECT a.category FROM AuditCategoryMatrixCompetencies a "
				+ "WHERE a.operatorCompetency IN (SELECT DISTINCT jc.competency FROM JobCompetency jc "
				+ "WHERE jc.jobRole.account.id = :accountID AND jc.jobRole IN "
				+ "(SELECT DISTINCT er.jobRole FROM EmployeeRole er WHERE er.employee.account.id = :accountID))");
		query.setParameter("accountID", accountID);

		return query.getResultList();
	}
}