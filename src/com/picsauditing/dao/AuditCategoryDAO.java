package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.ContractorAccount;

@Transactional
@SuppressWarnings("unchecked")
public class AuditCategoryDAO extends PicsDAO {
	public AuditCategory save(AuditCategory o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		AuditCategory row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public AuditCategory find(int id) {
        return em.find(AuditCategory.class, id);
    }
	
	public List<AuditCategory> findCategoryNames(String categoryName) {
		String sql = "SELECT c FROM AuditCategory c WHERE c.category LIKE '%" + categoryName + "%'";
		Query query = em.createQuery(sql);
		return query.getResultList();
    }

	public List<AuditCategory> findByAuditTypeID(int id) {
		String sql = "SELECT c FROM AuditCategory c WHERE c.auditType.id = :auditTypeID";
		Query query = em.createQuery(sql);
		query.setParameter("auditTypeID", id);
		return query.getResultList();
    }

	public List<AuditCategory> findPqfCategories(ContractorAccount contractor) {
		String sql = "SELECT DISTINCT c.category FROM AuditCatOperator c " +
			"WHERE riskLevel = :riskLevel AND c.operatorAccount IN " +
					"(SELECT co.operatorAccount.inheritAuditCategories FROM ContractorOperator co WHERE co.contractorAccount = :contractor)";
		Query query = em.createQuery(sql);
		query.setParameter("contractor", contractor);
		if (contractor.getRiskLevel() == null)
			// until  we know for sure, assume high risk
			query.setParameter("riskLevel", 3);
		else
			query.setParameter("riskLevel", contractor.getRiskLevel().ordinal());
		return query.getResultList();
	}

	public List<AuditCategory> findDesktopCategories(int pqfAuditID) {
		String sql = "SELECT DISTINCT m.category FROM DesktopMatrix m "	+ 
			"WHERE m.question IN " +
					"(SELECT d.question FROM AuditData d WHERE d.audit.id = :pqfAuditID " + 
					"AND (" +
						"(question.questionType IN ('Service') AND d.answer LIKE 'C%') OR " +
						"(question.questionType IN ('Industry','Main Work') AND d.answer='X')" +
					"))";
		Query query = em.createQuery(sql);
		query.setParameter("pqfAuditID", pqfAuditID);
		return query.getResultList();
	}
}
