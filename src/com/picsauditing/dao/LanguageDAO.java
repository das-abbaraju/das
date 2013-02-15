package com.picsauditing.dao;

import com.picsauditing.jpa.entities.Language;
import com.picsauditing.jpa.entities.LanguageStatus;
import com.picsauditing.model.i18n.LanguageProvider;
import com.picsauditing.util.Strings;

import javax.persistence.Query;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class LanguageDAO extends PicsDAO implements LanguageProvider {
	@Override
	public Language find(String id) {
		return em.find(Language.class, id);
	}

	@Override
	public Language find(Locale locale) {
		return em.find(Language.class, locale);
	}

	@Override
	public List<Language> findAll() {
		Query query = em.createQuery("SELECT l FROM Language l");
		return query.getResultList();
	}

	@Override
	public List<Language> findByStatus(LanguageStatus status) {
		Query query = em.createQuery("SELECT l FROM Language l WHERE l.status = '" + status + "'");

		return query.getResultList();
	}

	@Override
	public List<Language> findByStatuses(LanguageStatus[] statuses) {
		Query query = em.createQuery("SELECT l FROM Language l WHERE l.status IN (" +
				Strings.implodeForDB(statuses, ", ") + ")");

		return query.getResultList();
	}

	@Override
	public List<Language> findWhere(String where) {
		Query query = em.createQuery("SELECT l FROM Language l WHERE " + where);
		return query.getResultList();
	}
}
