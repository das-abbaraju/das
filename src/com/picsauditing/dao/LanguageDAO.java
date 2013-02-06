package com.picsauditing.dao;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;
import com.picsauditing.util.Strings;

import javax.persistence.Query;
import java.util.List;

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
		Query query = em.createQuery("FROM Language l WHERE l.status = '" + status + "'");

		return query.getResultList();
	}

	public List<Language> findByStatuses(LanguageStatus[] statuses) {
		Query query = em.createQuery("FROM Language l WHERE l.status IN (" +
				Strings.implodeForDB(statuses, ", ") + ")");

		return query.getResultList();
	}

	public List<Language> findWhere(String where) {
		Query query = em.createQuery("FROM Country WHERE " + where);
		return query.getResultList();
	}
}
