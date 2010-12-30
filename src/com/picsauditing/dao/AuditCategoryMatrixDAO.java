package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryMatrix;

@Transactional
@SuppressWarnings("unchecked")
public class AuditCategoryMatrixDAO extends PicsDAO {
	public AuditCategoryMatrix find(int id) {
		return em.find(AuditCategoryMatrix.class, id);
	}

	public List<AuditCategory> findCategoriesForCompetencies(int accountID) {
		Query query = em.createQuery("SELECT a.category FROM AuditCategoryMatrixCompetencies a "
				+ "WHERE a.operatorCompetency IN (SELECT DISTINCT jc.competency FROM JobCompetency jc "
				+ "WHERE jc.jobRole.account.id = :accountID AND jc.jobRole IN "
				+ "(SELECT DISTINCT er.jobRole FROM EmployeeRole er WHERE er.employee.account.id = :accountID))");
		query.setParameter("accountID", accountID);

		return query.getResultList();
	}
}
