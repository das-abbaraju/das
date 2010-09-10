package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCatData;

@Transactional
public class AuditCategoryDataDAO extends PicsDAO {
	public AuditCatData save(AuditCatData o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditCatData row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditCatData find(int id) {
		AuditCatData a = em.find(AuditCatData.class, id);
		return a;
	}

	@SuppressWarnings("unchecked")
	public AuditCatData findAuditCatData(int auditID, int catID) {
		String selectQuery = "FROM AuditCatData d " + "WHERE d.category.id=" + catID + " AND d.audit.id=" + auditID;
		Query query = em.createQuery(selectQuery);
		return (AuditCatData) query.getSingleResult();
	}

}
