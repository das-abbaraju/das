package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Country;

@Transactional
@SuppressWarnings("unchecked")
public class CountryDAO extends PicsDAO {

	public List<Country> findAll() {
		Query query = em.createQuery("FROM Country t ORDER BY t.english");
		List<Country> list = new ArrayList<Country>();
		list.add(find("US"));
		list.add(find("CA"));
		list.add(find("GB"));
		list.addAll(query.getResultList());
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
}
