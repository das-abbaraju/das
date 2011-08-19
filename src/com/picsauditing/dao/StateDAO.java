package com.picsauditing.dao;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class StateDAO extends PicsDAO {

	public List<State> findAll() {
		Query query = em.createQuery("FROM State t ORDER BY t.english");
		return query.getResultList();
	}

	public State find(String id) {
		return em.find(State.class, id);
	}

	public Multimap<Country, State> getStateMap(String country) {
		Multimap<Country, State> result = LinkedHashMultimap.create();

		Query query = em.createQuery("FROM State t ORDER BY t.english");
		List<State> states = (List<State>) query.getResultList();

		for (State state : states) {
			result.put(state.getCountry(), state);
		}

		return result;
	}

	public List<State> findByCountry(Country country) {
		return findByCountry(country.getIsoCode());
	}

	public List<State> findByCountry(String country) {
		Query query = em.createQuery("FROM State WHERE country.isoCode = ?");
		query.setParameter(1, country);

		return query.getResultList();
	}

	public List<State> findByCountries(Collection<String> countries, boolean negative) {
		String q = "FROM State WHERE country.isoCode ";
		if (negative)
			q += "NOT ";
		q += "IN (:countries)";

		Query query = em.createQuery(q).setParameter("countries", countries);

		return query.getResultList();
	}

	public List<State> findByCSR(int csrID) {
		Query query = em.createQuery("FROM State WHERE csr.id = ?");
		query.setParameter(1, csrID);

		return query.getResultList();
	}

	public List<State> findWhere(String where) {
		Query query = em.createQuery("FROM State WHERE " + where);
		return query.getResultList();
	}

	public List<State> findByTranslatableField(String value) {
		return findByTranslatableField("", value, Locale.ENGLISH);
	}

	public List<State> findByTranslatableField(String value, Locale locale) {
		return findByTranslatableField("", value, locale);
	}

	// TODO: Refactor this and merge Country and state DAO.
	public List<State> findByTranslatableField(String where, String value, Locale locale) {
		SelectSQL sql = new SelectSQL("ref_state t");
		sql.addField("t.*");
		sql.addJoin("JOIN app_translation tr ON CONCAT('State.',t.isoCode) = tr.msgKey");

		if (!Strings.isEmpty(where)) {
			sql.addWhere(where);
		}

		sql.addWhere("tr.msgValue LIKE :value");
		sql.addWhere("(tr.locale = :locale OR (tr.locale != :locale AND tr.locale = :lang) OR ( tr.locale != :locale AND tr.locale != :lang AND tr.locale = :default))");

		// fr_ca
		// fr_ca || (!fr_ca && fr) || (!fr_ca && !fr && en)

		Query query = em.createNativeQuery(sql.toString(), State.class);
		query.setParameter("value", value);
		query.setParameter("locale", locale);
		query.setParameter("lang", locale.getLanguage());
		query.setParameter("default", I18nCache.DEFAULT_LANGUAGE);

		return query.getResultList();
	}
}