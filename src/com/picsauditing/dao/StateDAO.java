package com.picsauditing.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;

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
}
