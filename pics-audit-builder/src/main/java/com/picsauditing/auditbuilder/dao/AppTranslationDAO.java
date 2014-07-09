package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.AppTranslation;

import javax.persistence.Query;
import java.util.List;

public class AppTranslationDAO extends PicsDAO {
	public List<AppTranslation> findWhere(String where) {
		Query q = em.createQuery("FROM " + AppTranslation.class + " t WHERE " + where);

		return q.getResultList();
	}
}