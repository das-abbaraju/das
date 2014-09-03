package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.AuditType;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class DocumentTypeDAO extends PicsDAO {
	public List<AuditType> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em.createQuery("FROM com.picsauditing.auditbuilder.entities.AuditType t " + where + " ORDER BY t.classType, t.displayOrder");
		return query.getResultList();
	}
}