package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class CountryDAO extends PicsDAO {

	public List<Country> findAll() {
		Query query = em.createQuery("FROM Country t ORDER BY t.english");
		List<Country> list = new ArrayList<Country>();
		list.add(em.find(Country.class, "US"));
		list.add(em.find(Country.class, "CA"));
		list.add(em.find(Country.class, "GB"));

		List<Country> results = query.getResultList();
		list.addAll(results);
		return list;
	}

	public Country find(String id) {
		return em.find(Country.class, id);
	}

	public List<Country> findByCSR(int csrID) {
		Query query = em.createQuery("FROM Country WHERE csr.id = ?");
		query.setParameter(1, csrID);

		return query.getResultList();
	}

	public List<Country> findWhere(String where) {
		Query query = em.createQuery("FROM Country WHERE " + where);
		return query.getResultList();
	}

	public List<Country> findByTranslatableField(String value) {
		return findByTranslatableField("", value, Locale.ENGLISH);
	}

	public List<Country> findByTranslatableField(String value, Locale locale) {
		return findByTranslatableField("", value, locale);
	}

	// TODO: Refactor this and merge Country and state DAO.
	public List<Country> findByTranslatableField(String where, String value, Locale locale) {
		SelectSQL sql = new SelectSQL("ref_country t");
		sql.addField("t.*");
		sql.addJoin("JOIN app_translation tr ON CONCAT('Country.',t.isoCode) = tr.msgKey");

		if (!Strings.isEmpty(where)) {
			sql.addWhere(where);
		}

		sql.addWhere("tr.msgValue LIKE :value");
		sql.addWhere("(tr.locale = :locale OR (tr.locale != :locale AND tr.locale = :lang) OR ( tr.locale != :locale AND tr.locale != :lang AND tr.locale = :default))");

		// fr_ca
		// fr_ca || (!fr_ca && fr) || (!fr_ca && !fr && en)

		Query query = em.createNativeQuery(sql.toString(), Country.class);
		query.setParameter("value", value);
		query.setParameter("locale", locale);
		query.setParameter("lang", locale.getLanguage());
		query.setParameter("default", I18nCache.DEFAULT_LANGUAGE);
		return query.getResultList();
	}
}