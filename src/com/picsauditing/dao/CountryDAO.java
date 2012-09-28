package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Country;

@SuppressWarnings("unchecked")
public class CountryDAO extends PicsDAO {

	public List<Country> findAll() {
		Query query = em.createQuery("FROM Country t");
		List<Country> list = new ArrayList<Country>();

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

    public Country findbyISO(String iso) {
        return (Country) em.createQuery("FROM Country c WHERE c.isoCode = '" + iso + "'").getSingleResult();
    }
}