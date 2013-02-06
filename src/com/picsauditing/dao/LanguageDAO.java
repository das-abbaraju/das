package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;

@SuppressWarnings("unchecked")
public class LanguageDAO extends PicsDAO {

	public Language find(String id) {
		return em.find(Language.class, id);
	}
	
	public List<Language> findAll() {
		Query query = em.createQuery("FROM Language l");
		return query.getResultList();
	}

	public List<Language> findByStatus(LanguageStatus status) {
		Query query = em.createQuery("FROM Language l WHERE l.status = ?");
		query.setParameter(1, status);

		return query.getResultList();
	}

	public List<Language> findWhere(String where) {
		Query query = em.createQuery("FROM Country WHERE " + where);
		return query.getResultList();
	}
}
